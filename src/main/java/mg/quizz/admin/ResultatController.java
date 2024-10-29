package mg.quizz.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mg.quizz.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultatController {

    @FXML
    private TableView<Resultat> resultatTable;
    @FXML
    private TableColumn<Resultat, String> userNameColumn;
    @FXML
    private TableColumn<Resultat, String> matiereNomColumn;
    @FXML
    private TableColumn<Resultat, Integer> noteColumn;

    private ObservableList<Resultat> resultatsList = FXCollections.observableArrayList();

    // Méthode appelée pour initialiser les données ou mettre à jour les résultats
    public void initialize() {
        // Configuration des colonnes du tableau
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        matiereNomColumn.setCellValueFactory(new PropertyValueFactory<>("matiereNom"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        // Requête SQL pour récupérer les noms d'utilisateur, de matière et la note
        String query = "SELECT u.name AS user_name, m.nom AS matiere_nom, un.note " +
                       "FROM user_notes un " +
                       "JOIN users u ON un.user_id = u.id " +
                       "JOIN matiere m ON un.matiere_id = m.id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Parcourir les résultats et les ajouter à la liste
            while (resultSet.next()) {
                String userName = resultSet.getString("user_name");
                String matiereNom = resultSet.getString("matiere_nom");
                int note = resultSet.getInt("note");

                // Ajouter un nouvel élément dans la liste des résultats
                resultatsList.add(new Resultat(userName, matiereNom, note));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Ajouter les résultats dans le tableau
        resultatTable.setItems(resultatsList);
    }

    // Méthode pour retourner à la page d'accueil
    @FXML
    private void handleRetourButton() {
        try {
            AppAdmin.setRoot("homePage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Classe interne pour représenter chaque résultat
    public static class Resultat {
        private final String userName;
        private final String matiereNom;
        private final int note;

        public Resultat(String userName, String matiereNom, int note) {
            this.userName = userName;
            this.matiereNom = matiereNom;
            this.note = note;
        }

        public String getUserName() {
            return userName;
        }

        public String getMatiereNom() {
            return matiereNom;
        }

        public int getNote() {
            return note;
        }
    }
}
