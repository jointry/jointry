package jp.ac.aiit.jointry.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import jp.ac.aiit.jointry.models.Sprite;

public class MainController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private MenuItem fopen;
    private BackStageController backStageController;
    private FrontStageController frontStageController;
    private BlocksController blocksController;
    @FXML
    private Label dummylabel;

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

        //初期スプライト
        URL path = getClass().getResource("images/scratch_cat1.gif");
        Sprite sprite = new Sprite(path.toString(), this);
        URL costume_path = getClass().getResource("images/scratch_cat2.gif");
        sprite.createCostume(new Image(costume_path.toString()));
        frontStageController.showSprite(sprite);
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

    @FXML
    public void fopen(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Pick a Sound File");
        File f = fc.showOpenDialog(dummylabel.getScene().getWindow());
        if (f != null) {
            // do something
        }
    }
}
