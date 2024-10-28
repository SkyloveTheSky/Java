package mg.quizz.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import mg.quizz.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class FaireExamenController {

    @FXML
    private Label matiereLabel;

    @FXML
    private Label questionLabel;

    @FXML
    private VBox optionsContainer;

    @FXML
    private Button submitButton;

    @FXML
    private Button nextQuestionButton;

    @FXML
    private Label timerLabel; // Étiquette pour afficher le chrono

    private String selectedMatiere;
    private List<String> questionsList = new ArrayList<>();
    private List<List<String>> optionsList = new ArrayList<>();
    private List<String> correctAnswers = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int chrono; // Temps total en secondes
    private Timeline timeline;
    private int currentQuizzId; // Attribut pour stocker l'ID du quiz actuel

    // Attribut pour stocker l'utilisateur actuel
    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("User ID reçu dans FaireExamen : " + userId);  // Vérification
    }
    private int getCurrentUserId() {
        return userId;
    }

    public int getCurrentQuizzId() {
        return currentQuizzId;
    }

    @FXML
public void initialize() {
    if (userId == 0) {
        System.out.println("Erreur : User ID est 0 dans FaireExamen !");
    }

    // Initialiser le chrono
    timerLabel.setText("00:00");
}
    public void setSelectedMatiere(String matiere) {
        this.selectedMatiere = matiere;
        matiereLabel.setText("Matière : " + matiere);

        // Charger l'ID de la matière et le chrono de la base de données
        int matiereId = getMatiereIdAndChrono(selectedMatiere);

        if (matiereId == -1) {
            System.out.println("Matière non trouvée.");
            return;
        }

        loadQuestionsAndOptionsFromDatabase(matiereId);
        startTimer();
        displayQuestion();
    }

    private int getMatiereIdAndChrono(String matiereName) {
        int matiereId = -1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Erreur : Impossible de se connecter à la base de données.");
                return -1;
            }

            String query = "SELECT id, chrono FROM matiere WHERE nom = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, matiereName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                matiereId = rs.getInt("id");
                chrono = rs.getInt("chrono") * 60; // Convertir les minutes en secondes

                // Si le chrono est invalide ou nul, définir une valeur par défaut
                if (chrono <= 0) {
                    chrono = 600; // 10 minutes par défaut
                    System.out.println("Avertissement : Le chrono est invalide, valeur par défaut définie à 10 minutes.");
                }

                updateTimerLabel(); // Mettre à jour l'affichage du timer
            } else {
                System.out.println("Aucune matière trouvée pour : " + matiereName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matiereId;
    }

    private void loadQuestionsAndOptionsFromDatabase(int matiereId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Erreur : Impossible de se connecter à la base de données.");
                return;
            }

            String queryQuestions = "SELECT id, questions FROM quizz WHERE matiere_id = ?";
            PreparedStatement pstmtQuestions = conn.prepareStatement(queryQuestions);
            pstmtQuestions.setInt(1, matiereId);
            ResultSet rsQuestions = pstmtQuestions.executeQuery();

            while (rsQuestions.next()) {
                String question = rsQuestions.getString("questions");
                currentQuizzId = rsQuestions.getInt("id"); // Stocker l'ID du quiz
                questionsList.add(question);

                // Vérification : La question ne doit pas être vide
                if (question == null || question.isEmpty()) {
                    System.out.println("Avertissement : Une question vide a été détectée dans la base de données.");
                }

                // Récupérer les options pour chaque question
                loadOptionsForQuestion(conn, currentQuizzId);
            }

            if (questionsList.isEmpty()) {
                System.out.println("Aucune question trouvée pour cette matière.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOptionsForQuestion(Connection conn, int questionId) throws Exception {
        String queryOptions = "SELECT option_text, is_correct FROM options WHERE quizz_id = ?";
        PreparedStatement pstmtOptions = conn.prepareStatement(queryOptions);
        pstmtOptions.setInt(1, questionId);
        ResultSet rsOptions = pstmtOptions.executeQuery();

        List<String> optionsForQuestion = new ArrayList<>();
        String correctAnswer = "";

        while (rsOptions.next()) {
            String optionText = rsOptions.getString("option_text");
            boolean isCorrect = rsOptions.getBoolean("is_correct");

            // Vérification : Le texte de l'option ne doit pas être vide
            if (optionText == null || optionText.isEmpty()) {
                System.out.println("Avertissement : Une option vide a été détectée dans la base de données.");
            }

            optionsForQuestion.add(optionText);
            if (isCorrect) {
                correctAnswer = optionText;  // Enregistrer la bonne réponse
            }
        }

        if (optionsForQuestion.isEmpty()) {
            System.out.println("Avertissement : Aucune option trouvée pour la question " + questionId);
        }

        optionsList.add(optionsForQuestion);
        correctAnswers.add(correctAnswer);
    }

    private void startTimer() {
        // Initialiser et démarrer le chrono
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (chrono > 0) {
                chrono--;
                updateTimerLabel();
            } else {
                // Temps écoulé
                handleTimeUp();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerLabel() {
        int minutes = chrono / 60;
        int seconds = chrono % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void handleTimeUp() {
        timeline.stop();
        submitButton.setDisable(true);
        nextQuestionButton.setDisable(true);
        questionLabel.setText("Temps écoulé !");
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionsList.size()) {
            String currentQuestion = questionsList.get(currentQuestionIndex);
            questionLabel.setText(currentQuestion);

            // Effacer les options précédentes
            optionsContainer.getChildren().clear();

            // Ajouter les nouvelles options
            List<String> currentOptions = optionsList.get(currentQuestionIndex);
            for (String option : currentOptions) {
                CheckBox optionCheckBox = new CheckBox(option);
                optionsContainer.getChildren().add(optionCheckBox);
            }
        } else {
            endExam();
        }
    }

    private void endExam() {
        submitButton.setDisable(true);
        nextQuestionButton.setDisable(true);

        // Finaliser le score
        handleSubmitAnswerButton(null); // Appeler la méthode handleSubmitAnswerButton() pour calculer et soumettre le score
        questionLabel.setText("Examen terminé !");
    }

    @FXML
    private void handleSubmitAnswerButton(ActionEvent event) {
        // Vérification : S'assurer qu'une question a bien été sélectionnée
        if (currentQuestionIndex >= questionsList.size()) {
            System.out.println("Erreur : Aucune question active à soumettre.");
            return;
        }

        // Initialiser le score utilisateur
        int userScore = 0;

        // Récupérer l'ID du quizz actuel
        int quizzId = getCurrentQuizzId();

        // Récupérer les notes pour les options sélectionnées depuis la base de données
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Erreur : Impossible de se connecter à la base de données.");
                return;
            }

            // Requête pour récupérer les valeurs de note_is_correct et note_isnot_correct pour le quizz en cours
            String query = "SELECT option_text, note_is_correct, note_isnot_correct, is_correct FROM options WHERE quizz_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, quizzId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String optionText = rs.getString("option_text");
                int noteCorrect = rs.getInt("note_is_correct");
                int noteIncorrect = rs.getInt("note_isnot_correct");
                boolean isCorrect = rs.getBoolean("is_correct");

                // Parcourir les options sélectionnées dans l'interface pour vérifier si elles correspondent à cette option
                for (Node node : optionsContainer.getChildren()) {
                    if (node instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) node;
                        if (checkBox.isSelected() && checkBox.getText().equals(optionText)) {
                            // Ajouter la note correcte ou incorrecte en fonction de la sélection
                            if (isCorrect) {
                                userScore += noteCorrect;
                            } else {
                                userScore += noteIncorrect;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Stocker le score dans la base de données
        storeUserScore(userScore);  // Stocker le score dans la table user_notes

        // Désactiver le bouton "Soumettre" après la soumission
        submitButton.setDisable(true);  
    }

    // Fonction pour stocker la note de l'utilisateur dans la table user_notes
    private void storeUserScore(int userScore) {
        // Vérifier que l'ID utilisateur est valide
        int currentUserId = getCurrentUserId();
        if (currentUserId <= 0) {
            System.out.println("Erreur : Aucun utilisateur authentifié.");
            return;
        }
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Erreur : Impossible de se connecter à la base de données.");
                return;
            }
    
            // Vérifier que l'utilisateur existe dans la table users
            String checkUserQuery = "SELECT id FROM users WHERE id = ?";
            PreparedStatement checkUserPstmt = conn.prepareStatement(checkUserQuery);
            checkUserPstmt.setInt(1, currentUserId);
            ResultSet rs = checkUserPstmt.executeQuery();
    
            if (!rs.next()) {
                System.out.println("Erreur : L'utilisateur avec l'ID " + currentUserId + " n'existe pas.");
                return;
            }
    
            // Obtenir l'ID de la matière
            int matiereId = getMatiereIdAndChrono(selectedMatiere);
            
            // Vérifier que l'ID de la matière est valide
            if (matiereId <= 0) {
                System.out.println("Erreur : ID de la matière non valide.");
                return;
            }
    
            // Insérer ou mettre à jour le score de l'utilisateur
            String query = "INSERT INTO user_notes (user_id, matiere_id, note) VALUES (?, ?, ?) "
                         + "ON DUPLICATE KEY UPDATE note = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, matiereId);
            pstmt.setInt(3, userScore);
            pstmt.setInt(4, userScore);
            pstmt.executeUpdate();
            System.out.println("Score enregistré avec succès dans user_notes.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @FXML
    private void handleNextQuestionButton(ActionEvent event) {
        currentQuestionIndex++;
        displayQuestion();
    }
}
