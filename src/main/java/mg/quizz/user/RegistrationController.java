package mg.quizz.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import mg.quizz.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

        // Vérifie si tous les champs sont remplis
        if (name.isEmpty() || firstName.isEmpty() || userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérifie si les mots de passe correspondent
        if (!password.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Mots de passe non correspondants", "Les mots de passe ne correspondent pas.");
            return;
        }

        // Hacher le mot de passe avant de l'enregistrer
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur est survenue lors du hachage du mot de passe.");
            return;
        }

        // Appeler la méthode pour enregistrer l'utilisateur
        if (registerUser(name, firstName, userName, email, hashedPassword)) {
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

    private boolean registerUser(String name, String firstName, String userName, String email, String hashedPassword) {
        String sql = "INSERT INTO users (name, first_name, username, email, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, firstName);
            statement.setString(3, userName);
            statement.setString(4, email);
            statement.setString(5, hashedPassword); // Utiliser le mot de passe haché

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hachage du mot de passe avec SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
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
