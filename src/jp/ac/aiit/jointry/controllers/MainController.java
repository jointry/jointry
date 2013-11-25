package jp.ac.aiit.jointry.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Sprite;
import broker.core.Agent;
import jp.ac.aiit.jointry.services.file.FileManager;
import jp.ac.aiit.jointry.util.StageUtil;

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
    private Agent agent;

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

    @FXML
    protected void handleSaveBtnAct(ActionEvent event) {
        FileManager manager = new FileManager();
        try {
            manager.save(frontStageController.getSprites());
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void fopen(ActionEvent event) {
        initWindow("load");

        FileManager manager = new FileManager();
        try {
            manager.open(this);
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void windowClose(ActionEvent event) {
        FileManager manager = new FileManager();
        try {
            manager.save(frontStageController.getSprites());
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void startCooperation(ActionEvent event) {
        //協同編集
        Window owner = rootPane.getScene().getWindow(); //画面オーナー
        URL fxml = getClass().getResource("Cooperation.fxml"); //表示するfxml
        final StageUtil stage = new StageUtil(null, owner, fxml, null);

        stage.getStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                CooperationController ctrl = (CooperationController) stage.getController();
                if (agent == null) agent = ctrl.getAgent();
                ctrl.windowClose();
            }
        });

        stage.getStage().show();
    }

    public void initWindow(String mode) {
        switch (mode) {
            case "new":
                //初期スプライト
                URL path = getClass().getResource("images/scratch_cat1.gif");
                Sprite sprite = new Sprite(path.toString(), this);
                URL costume_path = getClass().getResource("images/scratch_cat2.gif");
                sprite.addCostume("costume", new Image(costume_path.toString()));
                frontStageController.showSprite(sprite);
                break;

            case "load":
                this.initialize(null, null);

                break;

            default:
                break;
        }
    }

    public void windowClose() {
        if (agent != null) agent.close();
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

    public Agent getAgent() {
        return this.agent;
    }
}
