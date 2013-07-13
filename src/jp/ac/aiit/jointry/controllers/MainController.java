package jp.ac.aiit.jointry.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class MainController implements Initializable {

    @FXML
    private BorderPane rootPane;
    private BackStageController backStageController;
    private FrontStageController frontStageController;
    private BlocksController blocksController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            FXMLLoader ld;
            Class<? extends MainController> klass = getClass();

            // FrontStage
            ld = new FXMLLoader(klass.getResource("FrontStage.fxml"));
            Parent front = (Parent) ld.load();
            FrontStageController fsc = ld.<FrontStageController>getController();
            fsc.setMainController(this);
            rootPane.setRight(front);
            setFrontStageController(fsc);

            // BackStage
            ld = new FXMLLoader(klass.getResource("BackStage.fxml"));
            Parent back = (Parent) ld.load();
            BackStageController bsc = ld.<BackStageController>getController();
            bsc.setMainController(this);
            rootPane.setCenter(back);
            setBackStageController(bsc);

            // Blocks
            ld = new FXMLLoader(klass.getResource("Blocks.fxml"));
            Parent blocks = (Parent) ld.load();
            BlocksController bc = ld.<BlocksController>getController();
            bc.setMainController(this);
            rootPane.setLeft(blocks);
            setBlocksController(bc);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setBackStageController(BackStageController controller) {
        this.backStageController = controller;
    }

    public void setFrontStageController(FrontStageController controller) {
        this.frontStageController = controller;
    }

    public void setBlocksController(BlocksController blocksController) {
        this.blocksController = blocksController;
    }

    public BackStageController getBackStageController() {
        return this.backStageController;
    }

    public FrontStageController getFrontStageController() {
        return this.frontStageController;
    }

    public BlocksController getBlocksController() {
        return this.blocksController;
    }
}
