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
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Sprite;
import broker.core.Agent;
import broker.core.DefaultMonitor;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import jp.ac.aiit.jointry.services.broker.app.JointryAccount;
import jp.ac.aiit.jointry.services.broker.app.MainDialog;
import jp.ac.aiit.jointry.services.file.FileManager;
import jp.ac.aiit.jointry.util.StageUtil;

public class MainController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private MenuItem roomEnter;
    @FXML
    private MenuItem roomExit;
    private BackStageController backStageController;
    private FrontStageController frontStageController;
    private BlocksController blocksController;
    private Agent agent;
    private ListView members = new ListView();
    private Timer syncTimer;

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
    protected void fsave(ActionEvent event) {
        try {
            new FileManager().save(frontStageController.getSprites());
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void fopen(ActionEvent event) {
        try {
            new FileManager().load(this);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        MainDialog.sendSynchronize();
    }

    @FXML
    protected void startCooperation(ActionEvent event) {
        //協同編集
        Window owner = rootPane.getScene().getWindow(); //画面オーナー
        URL fxml = getClass().getResource("Cooperation.fxml"); //表示するfxml
        final StageUtil stage = new StageUtil(null, owner, fxml, null);

        final CooperationController ctrl = (CooperationController) stage.getController();
        ctrl.setMainController(MainController.this);

        stage.getStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if (agent == null) {
                    agent = ctrl.getAgent();
                    if (agent != null) {
                        agent.setMonitor(new MainMonitor());

                        //サーバであれば10秒ごとに同期
                        if ("s".equals(String.valueOf(agent.mark()))) {
                            syncTimer = new Timer("10秒同期タイマー");
                            syncTimer.schedule(new SyncTask(), 0, TimeUnit.SECONDS.toMillis(10));
                        }
                    }
                }
                ctrl.windowClose();
            }
        });

        stage.getStage().show();
    }

    @FXML
    protected void endCooperation(ActionEvent event) {
        if (agent != null) {
            agent.close();
            agent = null;
        }

        if (syncTimer != null) {
            syncTimer.cancel();
            syncTimer = null;
        }

        initWindow("disconnect");
    }

    public void initWindow(String mode) {
        switch (mode) {
            case "new":
                //初期スプライト
                URL path = getClass().getResource("images/scratch_cat1.gif");
                Sprite sprite = new Sprite(path.toString());
                sprite.setMainController(this);
                URL costume_path = getClass().getResource("images/scratch_cat2.gif");
                sprite.addCostume("costume", new Image(costume_path.toString()));
                frontStageController.showSprite(sprite);
                break;

            case "load":
                this.initialize(null, null);

                if (agent != null) {
                    initWindow("connect"); //協同編集中であればメンバーを表示
                }
                break;

            case "connect":
                //参加しているメンバー領域の表示
                VBox connectFront = new VBox();
                connectFront.getChildren().addAll(rootPane.getRight(), members);
                rootPane.setRight(connectFront);
                refreshMembers();
                members.setStyle("-fx-border-color: rgb(49, 89, 23)");
                roomEnter.setVisible(false);
                roomExit.setVisible(true);
                break;

            case "disconnect":
                //参加しているメンバー領域の非表示
                Node disconnectFront = rootPane.getRight();

                if (disconnectFront instanceof VBox) {
                    rootPane.setRight(((VBox) disconnectFront).getChildren().get(0));
                }

                roomEnter.setVisible(true);
                roomExit.setVisible(false);

                break;

            default:
                break;
        }
    }

    public void refreshMembers() {
        members.setItems(JointryAccount.getUsers());
    }

    public void windowClose() {
        if (agent != null) {
            agent.close();
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

    public Agent getAgent() {
        return this.agent;
    }

    private class MainMonitor extends DefaultMonitor {

        @Override
        public void onClose() {
            agent = null; //close済みなので参照を外す

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (syncTimer != null) {
                        syncTimer.cancel();
                        syncTimer = null;
                    }
                    initWindow("disconnect");
                }
            });
        }
    }

    private class SyncTask extends TimerTask {

        @Override
        public void run() {
            MainDialog.sendSynchronize();
        }
    }
}
