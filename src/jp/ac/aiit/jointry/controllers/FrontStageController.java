package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.VariableLabel;
import jp.ac.aiit.jointry.models.blocks.expression.Variable;
import jp.ac.aiit.jointry.services.broker.app.JointryCommon;
import jp.ac.aiit.jointry.services.broker.app.SpriteDialog;
import jp.ac.aiit.jointry.util.StageUtil;

public class FrontStageController implements Initializable, JointryCommon {

    @FXML
    private AnchorPane stage;
    @FXML
    private VBox variables;
    @FXML
    private Slider speed_slider;
    private Sprite currentSprite;
    private MainController mainController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    protected void start(ActionEvent event) {
        this.mainController.getBackStageController().start();
    }

    public double getSpeed() {
        return (speed_slider.getMax() + 10) - speed_slider.getValue();
    }

    @FXML
    protected void stop(ActionEvent event) throws Exception {
        this.mainController.getBackStageController().stop();
    }

    @FXML
    protected void handlePaintBtnAct(ActionEvent event) throws Exception {
        Window owner = stage.getScene().getWindow(); //画面オーナー
        URL fxml = getClass().getResource("Paint.fxml"); //表示するfxml
        final StageUtil paintStage = new StageUtil(null, owner, fxml, null);

        //新規コスチューム追加
        paintStage.getStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                PaintController ctrl = (PaintController) paintStage.getController();

                if (ctrl.getResult() != null) {
                    Sprite sprite = new Sprite(ctrl.getResult());
                    sprite.setMainController(mainController);
                    showSprite(sprite);
                }
            }
        });

        paintStage.getStage().show();
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public List<Sprite> getSprites() {
        List<Sprite> sprites = new ArrayList<>();
        for (Node i : stage.getChildren()) {
            if (i instanceof Sprite) {
                sprites.add((Sprite) i);
            }
        }
        return sprites;
    }

    public Sprite getCurrentSprite() {
        return currentSprite;
    }

    public void setCurrentSprite(Sprite sprite) {
        currentSprite = sprite;
        mainController.getBackStageController().setCurrentSprite(sprite);
    }

    public void showSprite(Sprite sprite) {
        this.addSprite(sprite, true);
        this.setCurrentSprite(sprite);
    }

    public void addSprite(Sprite sprite, boolean sendMessage) {
        int number = 1;
        for (Node i : stage.getChildren()) {
            if (i instanceof Sprite) {
                if (((Sprite) i).getName().equals(sprite.getName())) {
                    return;
                }
                number++;
            }
        }
        sprite.setName("sprite" + number);
        sprite.setDragRange(stage);
        stage.getChildren().add(sprite);

        if (sendMessage) {
            SpriteDialog.sendAllMessage(M_SPRITE_CREATE, sprite);
        }

        //Spriteが持っている変数を登録
        for (Node node : sprite.getScriptPane().getChildrenUnmodifiable()) {
            if (node instanceof Variable) {
                Variable block = (Variable) node;
                mainController.getBlocksController().addVariable(block.getName(), sendMessage);
            }
        }
    }

    void addVariable(VariableLabel vl) {
        variables.getChildren().add(vl);
    }
}
