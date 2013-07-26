package jp.ac.aiit.jointry.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Costume;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.statics.TestData;

public class CostumeCntroller implements Initializable {

    @FXML
    private ImageView image;
    @FXML
    private TextField title;
    @FXML
    private Label number;
    private MainController mainController;

    @FXML
    protected void handleImageSelected(MouseEvent event) {
        mainController.getFrontStageController()
                .getCurrentSprite().setImage(image.getImage());
    }

    @FXML
    protected void handleCopyButtonAction(ActionEvent event) {
        Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
        sprite.copyCostume(Integer.valueOf(number.getText()));
        mainController.getBackStageController().showCostumes(sprite);
    }

    @FXML
    protected void handleEditButtonAction(ActionEvent event) throws Exception {
        TestData data = new TestData();
        data.put("editImage", image.getImage());

        Stage paintStage = createStage("Paint.fxml", null);

        //新規コスチューム追加
        paintStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                TestData<Image> data = new TestData();
                Image img = data.get("paintImage");
                if (img == null) {
                    return;
                }

                Sprite sprite = mainController.getFrontStageController()
                        .getCurrentSprite();
                sprite.updateCostume(Integer.valueOf(number.getText()), img);

                image.setImage(img);
            }
        });

        paintStage.show();
        data.put("editImage", null); //後始末
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setInfo(Costume costume) {
        this.title.setText(costume.getTitle());
        this.image.setImage(costume.getImage());
        this.number.setText(Integer.toString(costume.getNumber()));
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
        stage.initOwner((Stage) image.getScene().getWindow());

        //UI読み込み
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage.setScene(new Scene(root));

        return stage;
    }
}
