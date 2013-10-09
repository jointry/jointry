package jp.ac.aiit.jointry.controllers;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import com.sleepingdumpling.jvideoinput.VideoInputException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import jp.ac.aiit.jointry.services.camera.CameraService;

public class CameraController implements Initializable {

    /**
     * カメラ表示領域.
     */
    @FXML
    private ImageView camview;
    /**
     * カメラから読み込んだ画像イメージ.
     */
    private CameraRetrieve retrieveTask;
    private CameraDisplay displayTask;
    private BufferedImage displayImage;
    private Image result;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        retrieveTask = new CameraRetrieve();
        displayTask = new CameraDisplay();

        new Thread(retrieveTask).start();
        displayTask.start();
    }

    @FXML
    protected void handleStartAct(ActionEvent event) throws Exception {
        stop();
        result = camview.getImage();

        windowClose();
    }

    @FXML
    protected void handleStopAct(ActionEvent event) throws Exception {
        stop();
        result = camview.getImage();

        windowClose();
    }
    
    public Image getResult() {
        return result;
    }

    private void windowClose() {
        Stage stage = (Stage) camview.getScene().getWindow();
        stage.close();
    }

    private void stop() {
        displayTask.stop();
        retrieveTask.stop();
    }

    private class CameraDisplay extends AnimationTimer {

        @Override
        public void handle(long now) {
            if (displayImage != null) {
                WritableImage buf = SwingFXUtils.toFXImage(displayImage, null);
                camview.setImage(buf);
            }
        }
    }

    private class CameraRetrieve extends Task<Void> {

        private boolean bCapture = true;

        @Override
        protected Void call() {
            CameraService camServ = null;

            try {
                camServ = new CameraService(450, 350);

                while (bCapture)
                    displayImage = camServ.retrieve(); //カメラからの画像を取り続ける
            } catch (VideoInputException ex) {
                Logger.getLogger(CameraRetrieve.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (camServ != null) camServ.release();
            }

            return null;
        }

        private void stop() {
            bCapture = false;
        }
    }
}
