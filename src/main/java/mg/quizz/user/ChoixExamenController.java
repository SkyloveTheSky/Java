package mg.quizz.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mg.quizz.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ChoixExamenController {
    
    @FXML
    private ComboBox<String> matiereComboBox;  // ComboBox pour les matières

    @FXML
    private Button commencerExamenButton; // Bouton pour commencer l'examen

    private ObservableList<String> matieresList = FXCollections.observableArrayList();

    private int semestreId;  // Nouveau champ pour stocker l'ID du semestre

    private int userId;  // ID de l'utilisateur

    // Méthode pour définir l'ID de l'utilisateur
    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("User ID reçu dans ChoixExamen : " + userId);
    }

    // Méthode pour définir l'ID du semestre depuis le contrôleur précédent
    public void setSemestreId(int semestreId) {
        this.semestreId = semestreId;
        System.out.println("Semestre ID reçu : " + semestreId);  // Débogage
        loadMatieresFromDatabase();
        matiereComboBox.setItems(matieresList); 
    }

    @FXML
    public void initialize() {
        loadMatieresFromDatabase();  // Charger les matières depuis la base de données
        matiereComboBox.setItems(matieresList);  // Définir les éléments du ComboBox des matières
    }

    // Charger les matières depuis la base de données
    private void loadMatieresFromDatabase() {
        try {
            Connection conn = DatabaseConnection.getConnection();  // Connexion à la base de données
            String query = "SELECT nom FROM matiere WHERE semestre_id = " + semestreId;
            System.out.println("Requête SQL exécutée : " + query);  // Afficher la requête exécutée
    
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
    
            while (rs.next()) {
                String matiere = rs.getString("nom");
                System.out.println("Matière trouvée : " + matiere);  // Afficher chaque matière récupérée
                matieresList.add(matiere);
            }
    
            conn.close();  // Fermer la connexion après utilisation
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    @FXML
public void handleCommencerExamButton(ActionEvent event) {
    try {
        String selectedMatiere = matiereComboBox.getSelectionModel().getSelectedItem();
        if (selectedMatiere != null) {
            System.out.println("User ID avant de charger l'examen : " + userId);  // Vérification

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/mg/quizz/Content/faireExamen.fxml"));
            Parent faireExamenRoot = loader.load();

            FaireExamenController faireExamenController = loader.getController();
            faireExamenController.setUserId(userId);  // Passer l'ID de l'utilisateur
            faireExamenController.setSelectedMatiere(selectedMatiere);

            Stage stage = (Stage) commencerExamenButton.getScene().getWindow();
            stage.setScene(new Scene(faireExamenRoot));
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}





    @FXML
    private void handleRetourHomePage() {
        try {
            App.setRoot("HomePage", "User");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

