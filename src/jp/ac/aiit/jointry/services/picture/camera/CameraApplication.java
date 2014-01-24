package jp.ac.aiit.jointry.services.picture.camera;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class CameraApplication extends Application {

    private CameraController controller;

    public Image getResult() {
        return controller.getResult();
    }

    public Stage start(Window owner) throws Exception {
        return start(new Stage(), owner);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        start(primaryStage, null);
    }

    private Stage start(Stage stage, Window owner) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Camera.fxml"));
        Parent root = (Parent) fxmlLoader.load();

        controller = fxmlLoader.getController();

        stage.setScene(new Scene(root));
        if (owner != null) {
            stage.initOwner(owner);
        }

        stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, controller);
        stage.show();

        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
