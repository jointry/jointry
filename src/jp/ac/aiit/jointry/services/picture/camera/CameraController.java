package jp.ac.aiit.jointry.services.picture.camera;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CameraController implements Initializable, EventHandler<WindowEvent> {

    /**
     * カメラ表示領域.
     */
    @FXML
    private ImageView camview;
    private Image result;
    private VideoCam videoCam;

    @FXML
    protected void handleStartAct(ActionEvent event) throws Exception {
        result = camview.getImage();
        windowClose();
    }

    @FXML
    protected void handleStopAct(ActionEvent event) throws Exception {
        windowClose();
    }

    @Override
    public void handle(WindowEvent t) {
        windowClose();
    }

    public Image getResult() {
        return result;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        videoCam = new VideoCam(camview);
        videoCam.open(450, 350);
    }

    private void windowClose() {
        videoCam.close();
        Stage stage = (Stage) camview.getScene().getWindow();
        stage.close();
    }
}
