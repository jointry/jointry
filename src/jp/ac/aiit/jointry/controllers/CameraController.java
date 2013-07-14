package jp.ac.aiit.jointry.controllers;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import com.sleepingdumpling.jvideoinput.VideoInputException;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import jp.ac.aiit.jointry.services.CameraService;
import jp.ac.aiit.jointry.statics.TestData;

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
    private BufferedImage displayImage = null;

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

        TestData data = new TestData();
        data.setCameraFile(displayImage);
    }

    @FXML
    protected void handleStopAct(ActionEvent event) throws Exception {
        stop();
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
        protected Void call() throws Exception {

            CameraService camServ = null;

            try {
                camServ = new CameraService(450, 350);

                while (bCapture) {
                    //カメラからの画像を取り続ける
                    displayImage = camServ.retrieve();
                }
            } catch (VideoInputException ex) {
                return null;
            } finally {
                if (camServ != null) {
                    camServ.release();
                }
            }

            return null;
        }

        protected void stop() {
            bCapture = false;
        }
    }
}
