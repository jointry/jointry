package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import jp.ac.aiit.jointry.services.camera.VideoCam;

public class CameraController implements Initializable {

    /**
     * カメラ表示領域.
     */
    @FXML
    private ImageView camview;

    @FXML
    protected void handleStartAct(ActionEvent event) throws Exception {
        windowClose();
    }

    @FXML
    protected void handleStopAct(ActionEvent event) throws Exception {
        windowClose();
    }
    private Image result;

    public Image getResult() {
        return result;
    }
    VideoCam videoCam;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        videoCam = new VideoCam(camview);
        videoCam.open(450, 350);
    }

    private void windowClose() {
        videoCam.close();
        result = camview.getImage();
        Stage stage = (Stage) camview.getScene().getWindow();
        stage.close();
    }
}
