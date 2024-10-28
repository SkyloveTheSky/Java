package mg.quizz.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import mg.quizz.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;


public class ConnexionController {

    @FXML
    private TextField userNameField;

    @FXML
    private TextField codeField;

    @FXML
    public void handleLoginButton(ActionEvent event) {
    String userName = userNameField.getText();
    String code = codeField.getText();

    // Vérifier que les champs ne sont pas vides
    if (userName.isEmpty() || code.isEmpty()) {
        showAlert(AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs.");
        return;
    }

    // Authentifier l'utilisateur et récupérer son ID
    int userId = authenticateUser(userName, code);

    if (userId != -1) {
        showAlert(AlertType.INFORMATION, "Connexion réussie", "Vous êtes maintenant connecté.");

        // Rediriger l'utilisateur vers la page d'accueil et passer l'ID utilisateur
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/quizz/User/HomePage.fxml")); // chemin modifié
            Parent root = loader.load();

            

            // Récupérer le contrôleur de la page d'accueil
            HomePageController homePageController = loader.getController();

            // Passer l'ID utilisateur au contrôleur
            homePageController.setUserId(userId);

            // Afficher la nouvelle scène
            Stage stage = (Stage) userNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page d'accueil.");
        }
    } else {
        showAlert(AlertType.ERROR, "Échec de connexion", "Nom d'utilisateur ou code incorrect.");
    }
}


    private int authenticateUser(String userName, String code) {
        String sql = "SELECT u.id FROM users u JOIN user_code uc ON u.id = uc.user_id WHERE u.username = ? AND uc.code = ?";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setString(1, userName);
            statement.setString(2, code);
    
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                // Récupérer l'ID de l'utilisateur
                return resultSet.getInt("id");
            } else {
                // Retourner -1 si l'authentification échoue
                return -1;
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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
            App.setRoot("welcome");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
