package mg.quizz.admin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AppAdmin extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Charger la page de bienvenue (Welcome.fxml) au démarrage
        scene = new Scene(loadFXML("welcome", "Admin"), 640, 480);
        stage.setTitle("Administrateur");
        stage.setScene(scene);
        stage.show();
    }

    // Méthode pour changer la scène (redirection vers un autre fichier FXML) avec dossier par défaut "Admin"
    static void setRoot(String fxml) throws IOException {
        setRoot(fxml, "Admin"); // Appel de la méthode avec un répertoire par défaut
    }

    // Méthode pour changer la scène (redirection vers un autre fichier FXML) avec dossier spécifié
    static void setRoot(String fxml, String folder) throws IOException {
        scene.setRoot(loadFXML(fxml, folder));
    }

    // Méthode pour charger les fichiers FXML dans le bon répertoire
    private static Parent loadFXML(String fxml, String folder) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppAdmin.class.getResource("/mg/quizz/" + folder + "/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
