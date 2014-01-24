package jp.ac.aiit.jointry.services.picture.paint;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import jp.ac.aiit.jointry.services.picture.paint.ctrl.PaintController;
import jp.ac.aiit.jointry.services.picture.paint.model.PaintModel;

public class PaintApplication extends Application {

    private static PaintModel model;
    private PaintController controller;

    public static PaintModel getModel() {
        return model;
    }

    public Image getResult() {
        return controller.getCompleteImage();
    }

    @Override
    public void start(Stage stage) throws Exception {
        start(stage, null, null);
    }

    public Stage start(Image image, Window owner) throws Exception {
        return start(new Stage(), image, owner);
    }

    private Stage start(Stage stage, Image image, Window owner) throws Exception {
        model = new PaintModel();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/fxml/PaintTool.fxml"));
        Parent root = (Parent) fxmlLoader.load();

        controller = fxmlLoader.getController();
        controller.setInitImage(image);

        stage.setScene(new Scene(root));

        if (owner != null) {
            stage.initOwner(owner);
        }

        stage.show();

        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
