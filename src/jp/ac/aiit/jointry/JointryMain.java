package jp.ac.aiit.jointry;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JointryMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("controllers/fxml/Main.fxml"));
        Parent parent = (Parent) loader.load();

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
