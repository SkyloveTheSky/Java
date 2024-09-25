package mg.quizz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.IOException;


public class RegistrationController {

    @FXML
    private TextField nameField;
    
    @FXML
    private TextField firstNameField;
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField passwordField;
    
    @FXML
    private TextField confirmPasswordField;

    @FXML
public void handleRegisterButton(ActionEvent event) {
    String name = nameField.getText();
    String firstName = firstNameField.getText();
    String userName = userNameField.getText();
    String email = emailField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    // Vérifie si les mots de passe correspondent
    if (!password.equals(confirmPassword)) {
        showAlert(AlertType.ERROR, "Mots de passe non correspondants", "Les mots de passe ne correspondent pas.");
        return;
    }

    // Appeler la méthode pour enregistrer l'utilisateur
    if (registerUser(name, firstName, userName, email, password)) {
        showAlert(AlertType.INFORMATION, "Inscription réussie", "L'utilisateur a été enregistré avec succès.");

        // Rediriger vers la page d'accueil
        try {
            App.setRoot("HomePage"); // Redirige vers la page d'accueil
        } catch (IOException e) {
            e.printStackTrace();
        }
    } else {
        showAlert(AlertType.ERROR, "Échec de l'inscription", "Erreur lors de l'enregistrement.");
    }
}

    private boolean registerUser(String name, String firstName, String userName, String email, String password) {
        String sql = "INSERT INTO users (name, first_name, username, email, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, firstName);
            statement.setString(3, userName);
            statement.setString(4, email);
            statement.setString(5, password);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

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
        try {
            App.setRoot("homePage"); // Redirige vers la page d'accueil
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

