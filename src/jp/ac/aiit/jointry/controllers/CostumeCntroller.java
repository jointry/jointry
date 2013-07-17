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
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.statics.TestData;

public class CostumeCntroller implements Initializable {

    @FXML
    private ImageView images;
    @FXML
    private TextField title;
    private FrontStageController ctrl;

    @FXML
    protected void handleImageSelected(MouseEvent event) {
        ctrl.getSprite().setImage(images.getImage());
    }

    @FXML
    protected void handleEditButtonAction(ActionEvent event) throws Exception {
        TestData data = new TestData();
        data.put("editImage", images.getImage());

        Stage paintStage = createStage("Paint.fxml", null);

        //新規コスチューム追加
        paintStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                TestData<Image> data = new TestData();
                if (data.get("paintImage") != null) {
                    images.setImage(data.get("paintImage"));
                    ctrl.getSprite().setImage(images.getImage());
                }
            }
        });

        paintStage.show();
        data.put("editImage", null); //後始末
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setInfo(String title, Image image) {
        this.title.setText(title);
        images.setImage(image);
    }

    public void setController(FrontStageController ctrl) {
        this.ctrl = ctrl;
    }

    private Stage createStage(String fxml, Stage stage) throws IOException {
        if (stage == null) {
            stage = new Stage(StageStyle.TRANSPARENT);
        }

        //オーナー設定
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner((Stage) images.getScene().getWindow());

        //UI読み込み
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage.setScene(new Scene(root));

        return stage;
    }
}
