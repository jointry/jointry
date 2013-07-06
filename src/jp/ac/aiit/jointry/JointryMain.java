package jp.ac.aiit.jointry;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jp.ac.aiit.jointry.controllers.BackStageController;
import jp.ac.aiit.jointry.controllers.BlocksController;
import jp.ac.aiit.jointry.controllers.FrontStageController;
import jp.ac.aiit.jointry.controllers.MainController;

public class JointryMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader;

        // Main
        loader = new FXMLLoader(getClass().getResource("controllers/fxml/Main.fxml"));
        BorderPane parent = (BorderPane) loader.load();
        MainController controller = loader.<MainController>getController();

        // FrontStage
        loader = new FXMLLoader(getClass().getResource("controllers/fxml/FrontStage.fxml"));
        Parent front = (Parent) loader.load();
        FrontStageController fsc = loader.<FrontStageController>getController();
        fsc.setMainController(controller);
        parent.setRight(front);
        controller.setFrontStageController(fsc);

        // BackStage
        loader = new FXMLLoader(getClass().getResource("controllers/fxml/BackStage.fxml"));
        Parent back = (Parent) loader.load();
        BackStageController bsc = loader.<BackStageController>getController();
        fsc.setMainController(controller);
        parent.setCenter(back);
        controller.setBackStageController(bsc);

        // BackStage
        loader = new FXMLLoader(getClass().getResource("controllers/fxml/Blocks.fxml"));
        Parent blocks = (Parent) loader.load();
        BlocksController bc = loader.<BlocksController>getController();
        bc.setMainController(controller);
        parent.setLeft(blocks);
        controller.setBlocksController(bc);

        // Staget setting
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
