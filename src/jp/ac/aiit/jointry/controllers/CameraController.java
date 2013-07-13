package jp.ac.aiit.jointry.controllers;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.net.URL;
import java.util.ResourceBundle;

import com.sleepingdumpling.jvideoinput.VideoFrame;
import com.sleepingdumpling.jvideoinput.VideoInput;
import com.sleepingdumpling.jvideoinput.VideoInputException;

import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import jp.ac.aiit.jointry.statics.TestData;

/**
 *
 * @author kanemoto
 */
public class CameraController implements Initializable {

    /**
     * カメラ表示領域.
     */
    @FXML
    private ImageView camview;
    /**
     * カメラから読み込んだ画像イメージ.
     */
    private BufferedImage displayImage = null;
    private AnimationTimer aTimer;
    private boolean bCapture;

    @FXML
    protected void handleStartAct(ActionEvent event) throws Exception {
        //カメラ画像を取得するためのスレッド
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                bCapture = true;
                retrieveAndDisplay(450, 350);

                return null;
            }
        };

        //描画を行うためのスレッド
        aTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };

        new Thread(task).start();
        aTimer.start();
    }

    @FXML
    protected void handleStopAct(ActionEvent event) throws Exception {
        aTimer.stop();
        bCapture = false;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    private void update() {

        //AffineTransform xform = AffineTransform.getTranslateInstance(displayImage.getWidth(), 0);
        //xform.scale(-1, 1);

        if (displayImage != null) {
            WritableImage buf = SwingFXUtils.toFXImage(displayImage, null);
            camview.setImage(buf);
        }
    }

    public BufferedImage getRenderingBufferedImage(VideoFrame videoFrame) {
        GraphicsConfiguration gc = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        BufferedImage img = gc.createCompatibleImage(videoFrame.getWidth(), videoFrame.getHeight(),
                Transparency.TRANSLUCENT);
        if (img.getType() == BufferedImage.TYPE_INT_ARGB
                || img.getType() == BufferedImage.TYPE_INT_ARGB_PRE
                || img.getType() == BufferedImage.TYPE_INT_RGB) {
            WritableRaster raster = img.getRaster();
            DataBufferInt dataBuffer = (DataBufferInt) raster.getDataBuffer();

            byte[] data = videoFrame.getRawData();
            addAlphaChannel(data, data.length, dataBuffer.getData());
            return img; //convert the data ourselves, the performance is much better
        } else {
            return videoFrame.getBufferedImage(); //much slower when drawing it on the screen.
        }
    }

    private void retrieveAndDisplay(int width, int height) {
        try {
            final VideoInput videoInput = new VideoInput(width, height);

            while (bCapture) {
                try {
                    VideoFrame vf = videoInput.getNextFrame(null);
                    if (vf != null) {
                        this.displayImage = getRenderingBufferedImage(vf);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            videoInput.stopSession();
        } catch (VideoInputException ex) {
            ex.printStackTrace();
        }

        TestData data = new TestData();
        data.setCameraFile(displayImage);
    }

    private void addAlphaChannel(byte[] rgbBytes, int bytesLen, int[] argbInts) {
        for (int i = 0, j = 0; i < bytesLen; i += 3, j++) {
            argbInts[j] = ((byte) 0xff) << 24 | // Alpha
                    (rgbBytes[i] << 16) & (0xff0000) | // Red
                    (rgbBytes[i + 1] << 8) & (0xff00) | // Green
                    (rgbBytes[i + 2]) & (0xff); // Blue
        }
    }
}
