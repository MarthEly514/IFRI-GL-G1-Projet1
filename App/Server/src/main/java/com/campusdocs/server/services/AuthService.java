package com.campusdocs.server.services;

import com.campusdocs.server.dto.request.LoginRequest;
import com.campusdocs.server.dto.request.SignupRequest;
import com.campusdocs.server.dto.response.ApiResponse;
import com.campusdocs.server.dto.response.LoginResponse;
import com.campusdocs.server.models.User;
import com.campusdocs.server.repositories.UsagerRepository;
import com.campusdocs.server.repositories.UserRepository;
import com.campusdocs.server.security.JwtUtils;
import com.campusdocs.server.security.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.campusdocs.server.models.Usager;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsagerRepository usagerRepository;

    // ── Connexion ──
    public LoginResponse login(LoginRequest request) {

        // 1. Chercher l'utilisateur par email
        Optional<User> optUser = userRepository.findByEmail(request.getEmail());

        if (optUser.isEmpty()) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        User user = optUser.get();

        // 2. Vérifier que le compte est actif
        if (!user.isActif()) {
            throw new RuntimeException("Compte désactivé");
        }

        // 3. Vérifier le mot de passe
        boolean passwordOk = PasswordUtils.checkPassword(
                request.getPassword(),
                user.getPassword(),
                user.getPasswordSalt()
        );

        if (!passwordOk) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        // 4. Générer le token JWT
        String token = jwtUtils.generateToken(
                user.getEmail(),
                user.getRole(),
                user.getId()
        );

        // 5. Retourner la réponse
        return new LoginResponse(
                token,
                user.getRole(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getId()
        );
    }

    // ── Inscription ──
    public ApiResponse signup(SignupRequest request) {

        // 1. Vérifier si l'email existe déjà
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // 2. Générer le sel et hacher le mot de passe
        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(request.getPassword(), salt);

        // 3. Créer l'usager
        Usager  usager = new Usager();
        usager.setNom(request.getNom());
        usager.setPrenom(request.getPrenom());
        usager.setEmail(request.getEmail());
        usager.setPassword(hashedPassword);
        usager.setPasswordSalt(salt);
        usager.setRole("ETUDIANT");
        usager.setActif(true);
        usager.setDateCreation(LocalDateTime.now());

        // Valeurs par défaut ( à adapter )
        usager.setMatricule(0L);
        usager.setFiliere("Non renseignée");
        usager.setNiveau("Non renseignée");

        // 4. Sauvegarder en base
        userRepository.save(usager);

        return new ApiResponse(true, "Compte créé avec succès");
    }
}