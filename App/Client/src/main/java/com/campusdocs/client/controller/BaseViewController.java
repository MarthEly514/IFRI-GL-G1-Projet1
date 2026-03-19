/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.controller;

import com.campusdocs.client.App;
import java.io.IOException;
import javafx.fxml.Initializable;

/**
 *
 * @author ely
 */

public abstract class BaseViewController implements Initializable {

    protected void navigate(String page, String title) throws IOException {
        App.setRoot(page);       
        App.setTitle(title);     
    }
}
