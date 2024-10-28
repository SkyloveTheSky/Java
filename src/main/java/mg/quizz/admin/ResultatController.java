package mg.quizz.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import mg.quizz.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultatController {

    @FXML
    private Label resultatLabel;

    // Méthode appelée pour initialiser les données ou mettre à jour les résultats
    public void initialize() {
        StringBuilder resultats = new StringBuilder();

        // Requête SQL pour récupérer les noms d'utilisateur, de matière et la note
        String query = "SELECT u.name AS user_name, m.nom AS matiere_nom, un.note " +
                       "FROM user_notes un " +
                       "JOIN users u ON un.user_id = u.id " +
                       "JOIN matiere m ON un.matiere_id = m.id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Parcourir les résultats et les afficher
            while (resultSet.next()) {
                String userName = resultSet.getString("user_name");
                String matiereNom = resultSet.getString("matiere_nom");
                int note = resultSet.getInt("note");

                // Construire la chaîne de texte à afficher dans le label
                resultats.append("Utilisateur : ").append(userName)
                        .append(", Matière : ").append(matiereNom)
                        .append(", Note : ").append(note)
                        .append("\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resultats.append("Erreur lors de la récupération des résultats.");
        }

        // Afficher les résultats dans le label
        resultatLabel.setText(resultats.toString());
    }

    // Méthode pour retourner à la page d'accueil
    @FXML
    private void handleRetourButton() {
        try {
            // Retour à la page d'accueil
            AppAdmin.setRoot("homePage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
