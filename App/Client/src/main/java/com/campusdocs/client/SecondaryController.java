package com.campusdocs.client;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToAct() throws IOException {
        App.setRoot("ActeView");
    }
}