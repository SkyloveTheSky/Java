package mg.quizz.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import mg.quizz.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ConnexionController {
    @FXML
    private TextField adminUserNameField;
    @FXML
    private PasswordField adminpasswordField;
    @FXML
    public void handleLoginButton(ActionEvent event){
        String adminUserName = adminUserNameField.getText();
        String adminPassword = adminpasswordField.getText();
        if (adminUserName.isEmpty()||adminPassword.isEmpty()){
            showAlert(AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }
        if (authenticateAdmin(adminUserName, adminPassword)) {
            showAlert(AlertType.INFORMATION, "Connexion réussie", "Vous êtes maintenant connecté.");
            try {
                AppAdmin.setRoot("HomePage");
            } catch (IOException e) {
                e.printStackTrace();
            }
            } else {
                showAlert(AlertType.ERROR, "Échec de connexion", "Nom d'utilisateur ou mot de passe incorrect.");
            }
        }
        private boolean authenticateAdmin(String adminUserName, String adminPassword) {
            String hashedPassword = hashPassword(adminPassword); // Hachez le mot de passe ici
            String sql = "SELECT * FROM admin WHERE userName = ? AND password = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
        
                statement.setString(1, adminUserName);
                statement.setString(2, hashedPassword); // Utilisez le mot de passe haché
        
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
        
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

