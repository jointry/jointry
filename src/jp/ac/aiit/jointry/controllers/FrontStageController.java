package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
    protected void handleExecuteBtnAct(ActionEvent event) {
        this.mainController.getBackStageController().execute();
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
                    addNewSprite(data.get("paintImage"));
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

    public Sprite getCurrentSprite() {
        return currentSprite;
    }

    public void setCurrentSprite(Sprite sprite) {
        currentSprite = sprite;
    }

    public void addNewSprite(Image image) {
        Sprite sprite = new Sprite(image, mainController);
        sprite.setDragRange(stage);
        stage.getChildren().add(sprite);

        setCurrentSprite(sprite);
    }
}
