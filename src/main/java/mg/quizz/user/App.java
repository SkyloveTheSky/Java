package mg.quizz.user;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Charger la page de bienvenue (Welcome.fxml) au démarrage
        scene = new Scene(loadFXML("welcome", "User"), 640, 480);
        stage.setTitle("User");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        setRoot(fxml, "User"); // Appel de la méthode avec un répertoire par défaut
    }

    static void setRoot(String fxml, String folder) throws IOException {
        System.out.println("Chargement de : " + fxml + " dans le dossier " + folder);
        scene.setRoot(loadFXML(fxml, folder));
    }
    

    // Modification de loadFXML pour vérifier dans les répertoires 'user' et 'content'
    private static Parent loadFXML(String fxml, String folder) throws IOException {
        URL fxmlLocation = App.class.getResource("/mg/quizz/" + folder + "/" + fxml + ".fxml");
        if (fxmlLocation == null && "user".equals(folder)) {
            // Si le fichier n'est pas trouvé dans 'user', chercher dans 'content'
            fxmlLocation = App.class.getResource("/mg/quizz/Content/" + fxml + ".fxml");
        }
        
        if (fxmlLocation != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            return fxmlLoader.load();
        } else {
            throw new IOException("FXML file not found: " + fxml);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
