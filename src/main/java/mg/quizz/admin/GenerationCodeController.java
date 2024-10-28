package mg.quizz.admin;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import mg.quizz.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class GenerationCodeController {

    @FXML
    private ComboBox<String> userComboBox;

    @FXML
    private Label generatedCodeLabel;

    private ObservableList<String> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        populateUserComboBox();
    }

    // Gérer le bouton Copier le code
    @FXML
    public void handleCopyCodeButton() {
        String code = generatedCodeLabel.getText().replace("Code généré : ", ""); // Supprimer le préfixe du label
        if (code.isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur de copie", "Aucun code à copier. Veuillez d'abord générer un code.");
            return;
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(code);
        clipboard.setContent(content);

        showAlert(AlertType.INFORMATION, "Copie réussie", "Le code a été copié dans le presse-papiers.");
    }

    // Populer la ComboBox avec les utilisateurs
    private void populateUserComboBox() {
        String sql = "SELECT username FROM users";  // Supposant que 'username' est la colonne qui stocke le nom d'utilisateur

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                userList.add(username);
            }

            userComboBox.setItems(userList);

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur de base de données", "Erreur lors de la récupération des noms d'utilisateurs depuis la base de données.");
            e.printStackTrace();
        }
    }

    // Gérer le bouton Générer un code
    @FXML
    public void handleGenerateCodeButton() {
        String selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(AlertType.ERROR, "Erreur de sélection", "Veuillez sélectionner un utilisateur d'abord.");
            return;
        }

        // Logique de génération de code ici
        String generatedCode = "ESMIA-" + generateRandomCode(10);

        // Afficher le code généré dans le label
        generatedCodeLabel.setText("Code généré : " + generatedCode);

        // Sauvegarder le code généré dans la base de données pour l'utilisateur sélectionné
        saveCodeToDatabase(selectedUser, generatedCode);
    }

    // Méthode pour générer un code aléatoire
    private String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            code.append(characters.charAt(index));
        }
        return code.toString();
    }

    // Sauvegarder le code généré dans la base de données
    private void saveCodeToDatabase(String userName, String generatedCode) {
        String sql = "UPDATE user_code SET code = ? WHERE user_id = (SELECT id FROM users WHERE username = ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, generatedCode);
            statement.setString(2, userName);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Succès", "Le code a été généré et sauvegardé avec succès.");
            } else {
                showAlert(AlertType.ERROR, "Échec de mise à jour", "Échec de la mise à jour du code utilisateur.");
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur de base de données", "Erreur lors de la sauvegarde du code généré.");
            e.printStackTrace();
        }
    }

    // Gérer le bouton Réinitialiser tous les codes
    @FXML
    public void handleResetCodesButton() {
        String sql = "UPDATE user_code SET code = NULL";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Succès", "Tous les codes ont été réinitialisés.");
            } else {
                showAlert(AlertType.ERROR, "Échec de réinitialisation", "Aucun code n'a été réinitialisé.");
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur de base de données", "Erreur lors de la réinitialisation des codes.");
            e.printStackTrace();
        }
    }

    // Gérer le bouton Réinitialiser le code d'un utilisateur sélectionné
    @FXML
    public void handleResetUserCodeButton() {
        String selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(AlertType.ERROR, "Erreur de sélection", "Veuillez sélectionner un utilisateur d'abord.");
            return;
        }

        String sql = "UPDATE user_code SET code = NULL WHERE user_id = (SELECT id FROM users WHERE username = ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, selectedUser);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Succès", "Le code de l'utilisateur a été réinitialisé.");
            } else {
                showAlert(AlertType.ERROR, "Échec de réinitialisation", "Le code de l'utilisateur n'a pas été réinitialisé.");
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur de base de données", "Erreur lors de la réinitialisation du code de l'utilisateur.");
            e.printStackTrace();
        }
    }

    // Méthode pour afficher les alertes
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
