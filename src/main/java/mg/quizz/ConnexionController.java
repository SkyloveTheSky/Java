package mg.quizz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;


public class ConnexionController {

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLoginButton(ActionEvent event) {
        String userName = userNameField.getText();
        String password = passwordField.getText();
    
        // Vérifie que les champs ne sont pas vides
        if (userName.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }
    
        // Vérifie les informations d'identification
        if (authenticateUser(userName, password)) {
            showAlert(AlertType.INFORMATION, "Connexion réussie", "Vous êtes maintenant connecté.");
    
            // Rediriger l'utilisateur vers la page d'accueil
            try {
                App.setRoot("HomePage"); // Redirige vers la page d'accueil
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(AlertType.ERROR, "Échec de connexion", "Nom d'utilisateur ou mot de passe incorrect.");
        }
    }

    private boolean authenticateUser(String userName, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, userName);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            // Si un résultat est trouvé, l'utilisateur est authentifié
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackButton() {
        // Revenir à la page de bienvenue
        try {
            App.setRoot("welcome");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
