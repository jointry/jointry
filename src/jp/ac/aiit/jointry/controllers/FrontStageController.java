package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.statics.TestData;

public class FrontStageController implements Initializable {

    @FXML
    private AnchorPane stage;
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
        //Paintツール画面
        Stage paintStage = new Stage(StageStyle.TRANSPARENT);

        //オーナー設定
        paintStage.initModality(Modality.APPLICATION_MODAL);
        paintStage.initOwner((Stage) stage.getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("Paint.fxml"));
        Parent root = (Parent) fxmlLoader.load();

        //ペイント書き終えた
        paintStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                TestData<Image> data = new TestData();
                if (data.get("paintImage") != null) {
                    Sprite sprite = new Sprite(data.get("paintImage"), mainController);
                    showSprite(sprite);
                }
            }
        });

        // 新しいウインドウを表示
        paintStage.setScene(new Scene(root));
        paintStage.show();
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
}
