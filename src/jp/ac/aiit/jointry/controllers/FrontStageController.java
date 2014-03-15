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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.VariableLabel;
import jp.ac.aiit.jointry.models.blocks.expression.Variable;
import jp.ac.aiit.jointry.services.broker.app.JointryCommon;
import jp.ac.aiit.jointry.services.broker.app.MainDialog;
import jp.ac.aiit.jointry.services.broker.app.SpriteDialog;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;

public class FrontStageController implements Initializable, JointryCommon {
    
    @FXML
    private AnchorPane stage;
    @FXML
    private VBox variables;
    @FXML
    private Slider speed_slider;
    @FXML
    private Button sync;
    private Sprite currentSprite;
    private MainController mainController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    @FXML
    public void start(ActionEvent event) {
        this.mainController.getBackStageController().start();
        MainDialog.sendEvent(mainController.getAgent(), M_MAIN_SCRIPT_EXECUTE);
    }
    
    public double getSpeed() {
        return (speed_slider.getMax() + 10) - speed_slider.getValue();
    }
    
    @FXML
    public void stop(ActionEvent event) throws Exception {
        this.mainController.getBackStageController().stop();
        MainDialog.sendEvent(mainController.getAgent(), M_MAIN_SCRIPT_STOP);
    }
    
    @FXML
    protected void handlePaintBtnAct(ActionEvent event) throws Exception {
        final PaintApplication app = new PaintApplication();
        Stage paintStage = app.start(null, stage.getScene().getWindow());
        
        paintStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if (app.getResult() != null) {
                    Sprite sprite = new Sprite(app.getResult());
                    sprite.setMainController(mainController);
                    showSprite(sprite);
                }
            }
        });
    }
    
    @FXML
    protected void sync(ActionEvent event) {
        MainDialog.sendSynchronize();
    }
    
    @FXML
    protected void reset(ActionEvent event) {
        currentSprite.setTranslateX(0.0);
        currentSprite.setTranslateY(0.0);
        currentSprite.setRotate(0.0);
        currentSprite.setScaleX(1.0);
        currentSprite.clearSpeechBubble();
        SpriteDialog.sendSimpleMessage(M_SPRITE_RESET, currentSprite);
    }

    public void setSyncVisible(boolean visible) {
        sync.setVisible(visible);
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
    
    @FXML
    void keyboard(ActionEvent event) {
        CheckBox chk = (CheckBox) event.getSource();
        if (chk.isSelected()) {
            mainController.getJointryMain().setEnableKeyboard(true);
        } else {
            mainController.getJointryMain().setEnableKeyboard(false);
        }
    }
}
