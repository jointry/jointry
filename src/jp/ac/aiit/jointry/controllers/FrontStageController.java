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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.VariableLabel;
import jp.ac.aiit.jointry.util.StageUtil;

public class FrontStageController implements Initializable {

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
    }

    public void showSprite(Sprite sprite) {
        sprite.setDragRange(stage);
        stage.getChildren().add(sprite);

        setCurrentSprite(sprite);
    }

    void addVariable(VariableLabel vl) {
        variables.getChildren().add(vl);
    }
}
