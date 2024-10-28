package mg.quizz.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mg.quizz.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChoixSemestreController {

    @FXML
    private VBox semestreContainer;

    @FXML
    private Label noSemestreLabel;

    @FXML
    public void initialize() {
        try {
            loadSemestres(); // Chargement des semestres au démarrage
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Chargement des semestres depuis la base de données
    private void loadSemestres() throws SQLException {
        String sql = "SELECT id, nom FROM semestre";  // Requête pour obtenir les semestres
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            boolean hasSemestres = false;  // Variable pour vérifier si des semestres existent

            while (resultSet.next()) {
                hasSemestres = true;
                String semestreNom = resultSet.getString("nom");
                int semestreId = resultSet.getInt("id");

                // Créer un bouton pour chaque semestre
                Button semestreButton = new Button(semestreNom);
                semestreButton.setPrefWidth(200);

                // Ajouter une action au bouton pour aller vers choixExamen.fxml avec l'ID du semestre
                semestreButton.setOnAction(event -> {
                    try {
                        handleChoixExamen(semestreId); // Méthode pour passer à la page de choixExamen
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                // Ajouter le bouton dans le conteneur
                semestreContainer.getChildren().add(semestreButton);
            }

            // Si aucun semestre n'est trouvé, afficher un message
            if (!hasSemestres) {
                noSemestreLabel.setVisible(true);  // Afficher le label "Aucun semestre disponible"
            }
        }
    }

     // Méthode pour charger la vue ChoixExamen et transmettre l'ID du semestre
    private void handleChoixExamen(int semestreId) throws IOException {
        // Charger la vue choixExamen.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/quizz/Content/choixExamen.fxml"));
        Parent root = loader.load();

        ChoixExamenController choixExamenController = loader.getController();
        choixExamenController.setUserId(userId);

        // Obtenir le contrôleur associé à choixExamen.fxml
        ChoixExamenController controller = loader.getController();

        // Transmettre l'ID du semestre au contrôleur
        controller.setSemestreId(semestreId);  // Appeler la méthode non statique

        // Changer de scène
        Stage stage = (Stage) semestreContainer.getScene().getWindow();  // Récupérer la fenêtre actuelle
        stage.setScene(new Scene(root));  // Afficher la nouvelle scène
        stage.show();
    }

    private int userId;

    // Méthode pour définir l'ID de l'utilisateur
    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("User ID reçu dans ChoixSemestre : " + userId);
    }
}
