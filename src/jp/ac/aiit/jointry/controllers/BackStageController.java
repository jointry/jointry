package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Costume;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.SpriteTask;
import jp.ac.aiit.jointry.models.blocks.Block;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_COSTUME_SYNC;
import jp.ac.aiit.jointry.services.broker.app.SpriteDialog;
import jp.ac.aiit.jointry.services.picture.camera.CameraApplication;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;
import jp.ac.aiit.jointry.services.picture.paint.views.PtCamera;
import jp.ac.aiit.jointry.util.StageUtil;
import jp.ac.aiit.jointry.util.StringUtil;

public class BackStageController {

    @FXML
    private ScrollPane costumeList;
    @FXML
    private Tab scriptTab;
    @FXML
    private Label codeArea;
    private MainController mainController;
    private final List<SpriteTask> spriteTasks = new ArrayList(); //停止用

    @FXML
    protected void handlePaintBtnAct(ActionEvent event) throws Exception {
        final PaintApplication app = new PaintApplication();
        Stage stage = app.start(null, costumeList.getScene().getWindow());

        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if (app.getResult() != null) {
                    Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
                    sprite.setSpriteCostume(sprite.addCostume("costume", app.getResult()));
                    showCostumes(sprite);

                    sendMessage();
                }
            }
        });
    }

    @FXML
    protected void handleCamBtnAct(ActionEvent event) throws Exception {
        final CameraApplication app = new CameraApplication();

        try {
            Stage stage = app.start(costumeList.getScene().getWindow());

            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    if (app.getResult() != null) {
                        Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
                        sprite.setSpriteCostume(sprite.addCostume("costume", app.getResult()));
                        showCostumes(sprite);

                        sendMessage();
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(PtCamera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showCostumes(Sprite sprite) {
        VBox vbox = new VBox();
        for (Costume costume : sprite.getCostumes()) {
            URL fxml = getClass().getResource("Costume.fxml"); //表示するfxml
            StageUtil costumeStage = new StageUtil(null, null, fxml, costume);

            CostumeCntroller controller = (CostumeCntroller) costumeStage.getController();
            controller.setMainController(mainController);
            vbox.getChildren().add(costumeStage.getParent());
        }
        costumeList.setContent(vbox);
    }

    @FXML
    protected void handleCostumeSelected(Event event) {
        Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
        setCurrentSprite(sprite);
    }

    private String codeFormat(String code) {
        StringBuilder result = new StringBuilder();

        //1行単位で処理
        int tabCount = 0;
        String tabs = "";
        for (String line : code.split("\n")) {
            if (line.endsWith("}")) {
                tabs = StringUtil.multply("\t", --tabCount);
            }

            result.append(tabs);
            result.append(line);
            result.append("\n");

            if (line.endsWith("{")) {
                tabs = StringUtil.multply("\t", ++tabCount);
            }
        }

        return result.toString();
    }

    public void setCurrentSprite(Sprite sprite) {
        showCostumes(sprite);
        showBlocks(sprite);
    }

    @FXML
    protected void handleCodeSelected(Event event) {
        codeArea.setText(null);
        Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
        double speed = mainController.getFrontStageController().getSpeed();

        SpriteTask task = new SpriteTask();
        task.setSprite(sprite);
        task.setSpeed(speed);

        codeArea.setText(codeFormat(task.getCode()));
    }

    public void start() {
        double speed = mainController.getFrontStageController().getSpeed();
        for (Sprite sprite : mainController.getFrontStageController().getSprites()) {
            SpriteTask task = new SpriteTask();
            task.setSprite(sprite);
            task.setSpeed(speed);
            spriteTasks.add(task);
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    public void stop() {
        for (SpriteTask task : spriteTasks) {
            task.finish();
        }

        spriteTasks.clear();
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    private void showBlocks(Sprite sprite) {
        //組み立てたブロックを表示
        scriptTab.setContent(sprite.getScriptPane());
    }

    public void addBlock(Block block) {
        AnchorPane ap = (AnchorPane) scriptTab.getContent();
        ap.getChildren().add(block);
    }

    private void sendMessage() {
        Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
        SpriteDialog.sendAllMessage(M_COSTUME_SYNC, sprite);
    }
}
