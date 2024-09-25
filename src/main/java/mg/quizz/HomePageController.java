package mg.quizz;

import javafx.fxml.FXML;
import java.io.IOException;

public class HomePageController {

    @FXML
    private void handleLogoutButton() {
        // Code pour gérer la déconnexion
        try {
            App.setRoot("welcome"); // Redirige vers la page de bienvenue
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
