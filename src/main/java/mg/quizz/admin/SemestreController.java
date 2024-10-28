package mg.quizz.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mg.quizz.DatabaseConnection;

import java.sql.*;

public class SemestreController {

    @FXML
    private TextField semestreField;

    @FXML
    private ListView<String> semestreListView;

    private ObservableList<String> semestreList = FXCollections.observableArrayList();

    private Connection connection;

    public void initialize() {
        // Établir la connexion à la base de données
        try {
            connection = DatabaseConnection.getConnection(); // Obtenir la connexion
            loadSemestres(); // Charger les semestres après la connexion
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de se connecter à la base de données.");
        }
    }

    // Charger les semestres existants dans la ListView
    private void loadSemestres() {
        semestreList.clear();
        String query = "SELECT nom FROM semestre ORDER BY id ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                semestreList.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        semestreListView.setItems(semestreList);
    }

    @FXML
    private void handleAddSemestre() {
        String semestreName = semestreField.getText().trim();
        if (semestreName.isEmpty() || !semestreName.matches("^S[0-9]+$")) {
            showAlert("Erreur", "Le nom du semestre doit commencer par 'S' suivi de chiffres.");
            return;
        }

        String query = "INSERT INTO semestre (nom) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, semestreName);
            pstmt.executeUpdate();
            semestreList.add(semestreName);
            semestreField.clear();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Code d'erreur pour doublon
                showAlert("Erreur", "Ce semestre existe déjà.");
            } else {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteSemestre() {
        String selectedSemestre = semestreListView.getSelectionModel().getSelectedItem();
        if (selectedSemestre == null) {
            showAlert("Erreur", "Veuillez sélectionner un semestre à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer le semestre " + selectedSemestre + " ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                String query = "DELETE FROM semestre WHERE nom = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, selectedSemestre);
                    pstmt.executeUpdate();
                    semestreList.remove(selectedSemestre);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
