package mg.quizz.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import mg.quizz.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionMatiereController {
    @FXML
    private ComboBox<String> semestreComboBox;  // ComboBox pour sélectionner un semestre

    @FXML
    private TextField matiereField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Spinner<Integer> chronoSpinner; // Utiliser un Spinner pour la durée
    @FXML
    private ListView<String> matiereListView;

    // Instance de QuizzController pour gérer les quizz
    private QuizzController quizzController;

    @FXML
public void initialize() {
    loadMatiere();
    loadSemestres();  // Charge les semestres dans le ComboBox
    chronoSpinner.getValueFactory().setValue(1); 

    matiereListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
            matiereField.setText(newValue);
            loadDescription(newValue);
            int matiereId = getMatiereIdByName(newValue);
            if (matiereId != -1) {
                quizzController.loadQuizz(matiereId);
            }
        }
    });
}

// Charger les semestres pour le ComboBox
    private void loadSemestres() {
    List<String> semestres = new ArrayList<>();
    String sql = "SELECT nom FROM semestre";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
            semestres.add(resultSet.getString("nom"));
        }
        semestreComboBox.getItems().setAll(semestres);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    // Méthode pour lier le QuizzController à ce contrôleur
    public void setQuizzController(QuizzController quizzController) {
        this.quizzController = quizzController;
    }

    // Méthode pour charger les matières
    private void loadMatiere() {
        List<String> matieres = new ArrayList<>();
        String sql = "SELECT nom FROM matiere";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                matieres.add(resultSet.getString("nom"));
            }
            matiereListView.getItems().setAll(matieres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
private void handleAjouterMatiere() {
    String nomMatiere = matiereField.getText();
    String descriptionMatiere = descriptionField.getText();
    int chronoMatiere = chronoSpinner.getValue();
    String selectedSemestre = semestreComboBox.getValue();  // Récupération du semestre sélectionné

    if (nomMatiere.isEmpty() || selectedSemestre == null) {
        showAlert(AlertType.ERROR, "Champs vides", "Veuillez entrer un nom de matière et sélectionner un semestre.");
        return;
    }

    // Récupérer l'ID du semestre
    int semestreId = getSemestreIdByName(selectedSemestre);

    String sql = "INSERT INTO matiere (nom, description, chrono, semestre_id) VALUES (?, ?, ?, ?)";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, nomMatiere);
        statement.setString(2, descriptionMatiere);
        statement.setInt(3, chronoMatiere);
        statement.setInt(4, semestreId);  // Ajout de l'ID du semestre
        statement.executeUpdate();
        matiereField.clear();
        descriptionField.clear();
        chronoSpinner.getValueFactory().setValue(1);
        semestreComboBox.getSelectionModel().clearSelection();
        loadMatiere();  // Recharge la liste
        showAlert(AlertType.INFORMATION, "Succès", "Matière ajoutée avec succès.");
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(AlertType.ERROR, "Erreur", "Échec de l'ajout de la matière.");
    }
}
@FXML
private void handleModifierMatiere() {
    String selectedMatiere = matiereListView.getSelectionModel().getSelectedItem();
    String nomMatiere = matiereField.getText();
    String descriptionMatiere = descriptionField.getText(); // Récupération de la description
    int chronoMatiere = chronoSpinner.getValue(); // Récupération de la durée
    String selectedSemestre = semestreComboBox.getValue(); // Récupération du semestre sélectionné

    if (selectedMatiere == null || nomMatiere.isEmpty() || selectedSemestre == null) {
        showAlert(AlertType.ERROR, "Erreur", "Veuillez sélectionner une matière, entrer un nouveau nom et choisir un semestre.");
        return;
    }

    // Récupérer l'ID du semestre
    int semestreId = getSemestreIdByName(selectedSemestre);

    String sql = "UPDATE matiere SET nom = ?, description = ?, chrono = ?, semestre_id = ? WHERE nom = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, nomMatiere);
        statement.setString(2, descriptionMatiere); // Ajout de la description
        statement.setInt(3, chronoMatiere); // Ajout de la durée
        statement.setInt(4, semestreId); // Mise à jour de l'ID du semestre
        statement.setString(5, selectedMatiere);
        statement.executeUpdate();
        matiereField.clear();
        descriptionField.clear(); // Nettoyer le champ de description
        chronoSpinner.getValueFactory().setValue(1); // Réinitialiser le Spinner
        semestreComboBox.getSelectionModel().clearSelection(); // Réinitialiser le ComboBox
        loadMatiere();  // Recharge la liste
        showAlert(AlertType.INFORMATION, "Succès", "Matière modifiée avec succès.");
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(AlertType.ERROR, "Erreur", "Échec de la modification de la matière.");
    }
}


    @FXML
    private void handleSupprimerMatiere() {
        String selectedMatiere = matiereListView.getSelectionModel().getSelectedItem();
        if (selectedMatiere == null) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez sélectionner une matière à supprimer.");
            return;
        }

        String sql = "DELETE FROM matiere WHERE nom = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, selectedMatiere);
            statement.executeUpdate();
            loadMatiere();
            showAlert(AlertType.INFORMATION, "Succès", "Matière supprimée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Échec de la suppression de la matière.");
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Charger la description d'une matière
    private void loadDescription(String nomMatiere) {
        String sql = "SELECT description, chrono FROM matiere WHERE nom = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomMatiere);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                descriptionField.setText(resultSet.getString("description")); // Remplit le champ de description
                chronoSpinner.getValueFactory().setValue(resultSet.getInt("chrono")); // Remplit le Spinner de durée
            } else {
                descriptionField.clear(); 
                chronoSpinner.getValueFactory().setValue(1); // Réinitialiser le Spinner si aucune description trouvée
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour obtenir l'ID de la matière par son nom
    private int getMatiereIdByName(String nomMatiere) {
        String sql = "SELECT id FROM matiere WHERE nom = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomMatiere);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Si l'ID n'est pas trouvé
    }

    private int getSemestreIdByName(String nomSemestre) {
        String sql = "SELECT id FROM semestre WHERE nom = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomSemestre);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Si l'ID n'est pas trouvé
    }
    

    @FXML
    private void handleRetourHomePage() {
        try {
            AppAdmin.setRoot("HomePage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
