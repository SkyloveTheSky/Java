package mg.quizz.admin;

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
    private TextField adminNameField;

    @FXML
    private TextField adminFirstNameField;

    @FXML
    private TextField adminUserNameField;

    @FXML
    private TextField  adminEmailField;

    @FXML
    private TextField adminPasswordField;

    @FXML
    private TextField adminConfirmPasswordField;

    @FXML
    public void handleRegisterButton(ActionEvent event) {
        String adminName = adminNameField.getText();
        String adminFirstName = adminFirstNameField.getText();
        String adminUserName = adminUserNameField.getText();
        String adminEmail = adminEmailField.getText();
        String adminPassword = adminPasswordField.getText();
        String adminConfirmPassword = adminConfirmPasswordField.getText();

        if (adminName.isEmpty() || adminFirstName.isEmpty() || adminUserName.isEmpty() || adminEmail.isEmpty() || adminPassword.isEmpty() || adminConfirmPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        if (!adminPassword.equals(adminConfirmPassword)) {
            showAlert(AlertType.ERROR, "Mots de passe non correspondants", "Les mots de passe ne correspondent pas.");
            return;
        }

        String hashedAdminPassword = hashPassword(adminPassword);
        if (hashedAdminPassword == null) {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur est survenue lors du hachage du mot de passe.");
            return;
        }

        if (registerAdmin(adminName, adminFirstName, adminUserName, adminEmail, hashedAdminPassword)) {
            showAlert(AlertType.INFORMATION, "Inscription réussie", "L'utilisateur a été enregistré avec succès.");
            try {
                AppAdmin.setRoot("HomePage");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(AlertType.ERROR, "Échec de l'inscription", "Erreur lors de l'enregistrement.");
        }
    }

    private boolean registerAdmin(String adminName, String adminFirstName, String adminUserName, String adminEmail, String hashedAdminPassword) {
        String sql = "INSERT INTO admin (Name, FirstName, username, Email, Password) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, adminName);
            statement.setString(2, adminFirstName);
            statement.setString(3, adminUserName);
            statement.setString(4, adminEmail);
            statement.setString(5, hashedAdminPassword);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
        try {
            AppAdmin.setRoot("welcome");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
