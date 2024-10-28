package mg.quizz.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageController {

    private int userId; // Stocke l'ID de l'utilisateur

    // Méthode pour définir l'ID de l'utilisateur
    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("User ID reçu : " + userId);
    }

    @FXML
    private void handleLogoutButton() {
        // Code pour gérer la déconnexion
        try {
            App.setRoot("welcome");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
private void handleExamButton(ActionEvent event) {
    // Charger la page ou l'interface pour faire l'examen
    try {
        System.out.println("L'utilisateur avec l'ID " + userId + " fait l'examen.");
        
        // Charger la vue ChoixSemestre.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/quizz/Content/choixSemestre.fxml"));
        Parent root = loader.load();

        // Obtenir le contrôleur associé à la vue
        ChoixSemestreController controller = loader.getController();
        
        // Passer l'ID utilisateur au contrôleur si nécessaire
        controller.setUserId(userId); // Assurez-vous d'avoir une méthode setUserId dans le contrôleur

        // Changer de scène
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Récupérer la fenêtre actuelle
        stage.setScene(new Scene(root)); // Afficher la nouvelle scène
        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Erreur", "Impossible de charger la page Choisir un semestre.");
    }
}

    private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
}


}
