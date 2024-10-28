package mg.quizz.admin;

import javafx.fxml.FXML;


import java.io.IOException;

public class HomePageController {
    @FXML
    private void handleLogoutButton() {
        try {
            AppAdmin.setRoot("welcome");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGestionMatiereButton() {
        try {
            // Charger le fichier Matiere.fxml dans le répertoire Content
            AppAdmin.setRoot("matiere", "Content");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuizzButton() {
        try {
            // Rediriger vers le fichier fxml du quizz
            AppAdmin.setRoot("quizz", "Content");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGenerationCodeButton() {
    try {
        // Rediriger vers le fichier fxml de génération de code
        AppAdmin.setRoot("generationCode", "Content");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    @FXML
    private void handleSemestreButton() {
    try {
        // Rediriger vers le fichier semestre.fxml
        AppAdmin.setRoot("semestre", "Content");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

@FXML
private void handleResultatButton() {
    try {
        // Rediriger vers le fichier resultat.fxml
        AppAdmin.setRoot("resultat", "Content");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}
