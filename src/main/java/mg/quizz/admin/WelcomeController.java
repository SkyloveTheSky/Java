package mg.quizz.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class WelcomeController {

    @FXML
    public void handleRegisterButton(ActionEvent event) {
        try {
            // Rediriger vers la page d'inscription
            AppAdmin.setRoot("registration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLoginButton(ActionEvent event) {
        try {
            // Rediriger vers la page de connexion
            AppAdmin.setRoot("connexion");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
