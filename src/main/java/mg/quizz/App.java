package mg.quizz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Charger la page de bienvenue (Welcome.fxml) au démarrage
        scene = new Scene(loadFXML("welcome"), 640, 480);
        stage.setTitle("Bienvenue");
        stage.setScene(scene);
        stage.show();
    }

    // Méthode pour changer la scène (redirection vers un autre fichier FXML)
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Méthode pour charger les fichiers FXML
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/mg/quizz/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
