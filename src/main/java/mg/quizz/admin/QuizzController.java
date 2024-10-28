package mg.quizz.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mg.quizz.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class QuizzController {
    @FXML
    private Spinner<Integer> noteReductionSpinner;
    @FXML
    private Spinner<Integer> noteQuizzSpinner;
    @FXML
    private TextField titreQuizzField;
    @FXML
    private DatePicker dateQuizzPicker;
    @FXML
    private ListView<String> quizzListView;
    @FXML
    private ComboBox<String> matiereComboBox;
    @FXML
    private TextField questionQuizzField;
    @FXML
    private Spinner<Integer> nbrOptionsSpinner;
    @FXML
    private VBox optionsContainer;
    private int matiereId;
    @FXML
    public void initialize() {
        try {
            loadQuizz(matiereId);
            loadMatieres();
            configureOptionFields();
            SpinnerValueFactory<Integer> valueFactoryOptions = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 2);
            nbrOptionsSpinner.setValueFactory(valueFactoryOptions);
            SpinnerValueFactory<Integer> valueFactoryNote = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
            noteQuizzSpinner.setValueFactory(valueFactoryNote);
            SpinnerValueFactory<Integer> valueFactoryReductionNote = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 0);
            noteReductionSpinner.setValueFactory(valueFactoryReductionNote);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur d'initialisation", e.getMessage());
        }
    }

    public void loadQuizz(int matiereId) {
        this.matiereId = matiereId;
        quizzListView.getItems().clear();
        String sql = "SELECT titre FROM quizz WHERE matiere_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, matiereId);
            ResultSet resultSet = statement.executeQuery();
            List<String> quizz = new ArrayList<>();
            while (resultSet.next()) {
                quizz.add(resultSet.getString("titre"));
            }
            quizzListView.getItems().setAll(quizz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void configureOptionFields() {
        nbrOptionsSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            optionsContainer.getChildren().clear();
            for (int i = 1; i <= newValue; i++) {
                TextField optionField = new TextField();
                optionField.setPromptText("Option " + i);
                CheckBox correctBox = new CheckBox("Bonne réponse");
                HBox optionContainer = new HBox(10, optionField, correctBox);
                optionsContainer.getChildren().add(optionContainer);
            }
        });
    }
    @FXML
private void handleAjouterQuizz() {
    String titreQuizz = titreQuizzField.getText();
    LocalDate dateQuizz = dateQuizzPicker.getValue();
    String questionQuizz = questionQuizzField.getText();
    String matiereNom = matiereComboBox.getValue();

    // Vérification des champs
    if (titreQuizz.isEmpty() || dateQuizz == null || questionQuizz.isEmpty() || matiereNom == null) {
        showAlert(AlertType.ERROR, "Champs vides", "Veuillez entrer un titre, une question, choisir une date et une matière.");
        return;
    }

    try {
        int matiereId = getMatiereId(matiereNom); // Obtenir l'ID de la matière
        String sqlQuizz = "INSERT INTO quizz (titre, date, questions, matiere_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statementQuizz = connection.prepareStatement(sqlQuizz, Statement.RETURN_GENERATED_KEYS)) {
            // Insérer le quizz
            statementQuizz.setString(1, titreQuizz);
            statementQuizz.setDate(2, java.sql.Date.valueOf(dateQuizz));
            statementQuizz.setString(3, questionQuizz);
            statementQuizz.setInt(4, matiereId);
            statementQuizz.executeUpdate();

            // Obtenir l'ID du quizz nouvellement ajouté
            ResultSet generatedKeys = statementQuizz.getGeneratedKeys();
            int quizzId = -1;
            if (generatedKeys.next()) {
                quizzId = generatedKeys.getInt(1);
            }

            // Si le quizz a bien été ajouté
            if (quizzId != -1) {
                // Insertion des options
                String sqlOptionInsert = "INSERT INTO options (quizz_id, option_text, is_correct, note_is_correct, note_isnot_correct) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement statementOption = connection.prepareStatement(sqlOptionInsert)) {
                    for (int i = 0; i < optionsContainer.getChildren().size(); i++) {
                        HBox optionContainer = (HBox) optionsContainer.getChildren().get(i);
                        TextField optionField = (TextField) optionContainer.getChildren().get(0);
                        CheckBox correctBox = (CheckBox) optionContainer.getChildren().get(1);

                        String optionText = optionField.getText();
                        boolean isCorrect = correctBox.isSelected();

                        // Utilisation des valeurs des spinners
                        int noteCorrect = isCorrect ? noteQuizzSpinner.getValue() : 0;  // Récupération de la note correcte
                        int noteIncorrect = !isCorrect ? noteReductionSpinner.getValue() : 0;  // Récupération de la note incorrecte

                        // Insertion de l'option
                        statementOption.setInt(1, quizzId);
                        statementOption.setString(2, optionText);
                        statementOption.setBoolean(3, isCorrect);
                        statementOption.setInt(4, noteCorrect);  // Note si correct
                        statementOption.setInt(5, noteIncorrect);  // Note si incorrect
                        statementOption.addBatch();
                    }
                    statementOption.executeBatch();
                }

                // Confirmation d'ajout
                showAlert(AlertType.INFORMATION, "Quizz ajouté", "Le quizz a été ajouté avec succès.");
                loadQuizz(matiereId); // Recharger la liste des quizz
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(AlertType.ERROR, "Erreur d'ajout", "Erreur lors de l'ajout du quizz : " + e.getMessage());
    }
}

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMatieres() {
        String sql = "SELECT nom FROM matiere";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<String> matieres = new ArrayList<>();
            while (resultSet.next()) {
                matieres.add(resultSet.getString("nom"));
            }
            matiereComboBox.getItems().setAll(matieres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getMatiereId(String matiereNom) throws SQLException {
        String sql = "SELECT id FROM matiere WHERE nom = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, matiereNom);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }
        throw new SQLException("Matière non trouvée");
    }

    @FXML
private void handleModifierQuizz() {
    String selectedQuizz = quizzListView.getSelectionModel().getSelectedItem();
    String titreQuizz = titreQuizzField.getText();
    LocalDate dateQuizz = dateQuizzPicker.getValue();
    String questionQuizz = questionQuizzField.getText();
    String matiereNom = matiereComboBox.getValue();

    // Vérification des champs
    if (selectedQuizz == null || titreQuizz.isEmpty() || dateQuizz == null || questionQuizz.isEmpty() || matiereNom == null) {
        showAlert(AlertType.ERROR, "Champs vides", "Veuillez sélectionner un quizz, entrer un nouveau titre, une question, choisir une nouvelle date, et une matière.");
        return;
    }

    try {
        int matiereId = getMatiereId(matiereNom); // Obtenir l'ID de la matière
        String sqlQuizz = "UPDATE quizz SET titre = ?, date = ?, questions = ?, matiere_id = ? WHERE titre = ? AND matiere_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statementQuizz = connection.prepareStatement(sqlQuizz)) {
            statementQuizz.setString(1, titreQuizz);
            statementQuizz.setDate(2, java.sql.Date.valueOf(dateQuizz));
            statementQuizz.setString(3, questionQuizz);
            statementQuizz.setInt(4, matiereId);
            statementQuizz.setString(5, selectedQuizz);
            statementQuizz.setInt(6, this.matiereId);
            statementQuizz.executeUpdate();

            // Mise à jour des options
            String sqlOptionUpdate = "UPDATE options SET option_text = ?, is_correct = ?, note_is_correct = ?, note_isnot_correct = ? WHERE quizz_id = ? AND option_text = ?";
            try (PreparedStatement statementOption = connection.prepareStatement(sqlOptionUpdate)) {
                int quizzId = getQuizzId(selectedQuizz, this.matiereId);
                for (int i = 0; i < optionsContainer.getChildren().size(); i++) {
                    HBox optionContainer = (HBox) optionsContainer.getChildren().get(i);
                    TextField optionField = (TextField) optionContainer.getChildren().get(0);
                    CheckBox correctBox = (CheckBox) optionContainer.getChildren().get(1);

                    String optionText = optionField.getText();
                    boolean isCorrect = correctBox.isSelected();

                    // Utilisation des valeurs des spinners pour les notes
                    int noteCorrect = isCorrect ? noteQuizzSpinner.getValue() : 0;  // Note si correct, récupérée du spinner
                    int noteIncorrect = !isCorrect ? noteReductionSpinner.getValue() : 0;  // Note si incorrect, récupérée du spinner

                    // Mise à jour de l'option
                    statementOption.setString(1, optionText);
                    statementOption.setBoolean(2, isCorrect);
                    statementOption.setInt(3, noteCorrect);  // Note si correct
                    statementOption.setInt(4, noteIncorrect);  // Note si incorrect
                    statementOption.setInt(5, quizzId);
                    statementOption.setString(6, optionText); // Option à mettre à jour
                    statementOption.addBatch();
                }
                statementOption.executeBatch();
            }
        }
        loadQuizz(matiereId); // Recharger les quizzes pour cette matière
        showAlert(AlertType.INFORMATION, "Quizz modifié", "Le quizz a été modifié avec succès.");
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(AlertType.ERROR, "Erreur de modification", "Erreur lors de la modification du quizz : " + e.getMessage());
    }
}



    private int getQuizzId(String titreQuizz, int matiereId) throws SQLException {
        String sql = "SELECT id FROM quizz WHERE titre = ? AND matiere_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, titreQuizz);
            statement.setInt(2, matiereId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }
        throw new SQLException("Quiz non trouvé");
    }
    @FXML
    private void handleSupprimerQuizz() {
        String selectedQuizz = quizzListView.getSelectionModel().getSelectedItem();
        if (selectedQuizz == null) {
            showAlert(AlertType.ERROR, "Sélectionnez un quiz", "Veuillez sélectionner un quiz à supprimer.");
            return;
        }
    try {
            int quizzId = getQuizzId(selectedQuizz, this.matiereId);
            String sql = "DELETE FROM options WHERE quizz_id = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, quizzId);
                statement.executeUpdate();
            }
            String sqlQuizz = "DELETE FROM quizz WHERE id = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statementQuizz = connection.prepareStatement(sqlQuizz)) {
                statementQuizz.setInt(1, quizzId);
                statementQuizz.executeUpdate();
            }
            loadQuizz(matiereId);
            showAlert(AlertType.INFORMATION, "Quiz supprimé", "Le quiz a été supprimé avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur de suppression", "Erreur lors de la suppression du quiz : " + e.getMessage());
        }
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
