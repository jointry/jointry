package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.VariableLabel;
import jp.ac.aiit.jointry.services.broker.app.JointryCommon;
import broker.core.DInfo;
import jp.ac.aiit.jointry.services.broker.app.SpriteEventHook;
import jp.ac.aiit.jointry.util.StageUtil;

public class FrontStageController implements Initializable, JointryCommon {

    @FXML
    private AnchorPane stage;
    @FXML
    private VBox variables;
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
                    Sprite sprite = new Sprite(ctrl.getResult(), mainController);
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
        this.addSprite(sprite);
        this.setCurrentSprite(sprite);
    }

    public void addSprite(Sprite sprite) {
        sprite.setDragRange(stage);

        int number = 1;
        for (Node i : stage.getChildren()) {
            if (i instanceof Sprite) number++;
        }
        sprite.setName("sprite" + number);
        stage.getChildren().add(sprite);
        new SpriteEventHook(sprite).enableHook();

        if (mainController.getAgent() != null) {
            DInfo dinfo = new DInfo(D_SPRITE);
            dinfo.set(KC_METHOD, VM_SPRITE_MOVE);
            dinfo.set(KC_SPRITE_NAME, sprite.getName());
            dinfo.set(KC_X1, (int) sprite.getX());
            dinfo.set(KC_Y1, (int) sprite.getY());

            mainController.getAgent().sendNotify(dinfo);


            mainController.getAgent().notifyViewImage(sprite.getName(),
                    SwingFXUtils.fromFXImage(sprite.snapshot(null, null), null));
        }
    }

    void addVariable(VariableLabel vl) {
        variables.getChildren().add(vl);
    }
}
