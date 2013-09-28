package jp.ac.aiit.jointry.controllers;

import jp.ac.aiit.jointry.lang.parser.LangReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.SequentialTransition;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import jp.ac.aiit.jointry.lang.ast.ASTree;
import jp.ac.aiit.jointry.lang.ast.NullStmnt;
import jp.ac.aiit.jointry.lang.parser.JoinTryParser;
import jp.ac.aiit.jointry.lang.parser.Lexer;
import jp.ac.aiit.jointry.lang.parser.ParseException;
import jp.ac.aiit.jointry.lang.parser.Token;
import jp.ac.aiit.jointry.lang.parser.Environment;
import jp.ac.aiit.jointry.models.Costume;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.SpriteTask;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.statics.TestData;

public class BackStageController implements Initializable {

    @FXML
    private ScrollPane costumeList;
    @FXML
    private Tab scriptTab;
    private MainController mainController;
    private Environment env;

    @FXML
    protected void handlePaintBtnAct(ActionEvent event) throws Exception {
        Stage paintStage = createStage("Paint.fxml", null);

        //新規コスチューム追加
        paintStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                TestData<Image> data = new TestData();
                Image image = data.get("paintImage");
                if (image == null) {
                    return;
                }
                Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
                sprite.createCostume(image);
                showCostumes(sprite);
            }
        });

        paintStage.show();
    }

    public void showCostumes(Sprite sprite) {
        VBox vbox = new VBox();
        for (Costume costume : sprite.getCostumes()) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Costume.fxml"));
            Parent parent = null;
            try {
                parent = (Parent) fxmlLoader.load();
            } catch (IOException ex) {
                Logger.getLogger(BackStageController.class.getName()).log(Level.SEVERE, null, ex);
            }

            CostumeCntroller controller = (CostumeCntroller) fxmlLoader.getController();
            controller.setInfo(costume);
            controller.setMainController(mainController);
            vbox.getChildren().add(parent);
        }
        costumeList.setContent(vbox);
    }

    @FXML
    protected void handleCamBtnAct(ActionEvent event) throws Exception {

        Stage cameraStage = createStage("Camera.fxml", new Stage());

        //新規コスチューム追加
        cameraStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                TestData<Image> data = new TestData();
                Image image = data.get("cameraImage");
                if (image == null) {
                    return;
                }
                Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
                sprite.createCostume(image);
                showCostumes(sprite);
            }
        });

        cameraStage.show();
    }

    @FXML
    protected void handleCostumeSelected(Event event) {
        Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
        setCurrentSprite(sprite);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setCurrentSprite(Sprite sprite) {
        showCostumes(sprite);
        showBlocks(sprite);
    }

    public void start() {
        for (Sprite sprite : mainController.getFrontStageController().getSprites()) {
            SpriteTask task = new SpriteTask();
            task.setSprite(sprite);
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    public void stop() {
        env.getSequentialTransition().stop();
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    private Stage createStage(String fxml, Stage stage) throws IOException {
        if (stage == null) {
            stage = new Stage(StageStyle.TRANSPARENT);
        }

        //オーナー設定
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner((Stage) costumeList.getScene().getWindow());

        //UI読み込み
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage.setScene(new Scene(root));

        return stage;
    }

    private void showBlocks(Sprite sprite) {
        //組み立てたブロックを表示
        scriptTab.setContent(sprite.getScriptPane());
    }
}
