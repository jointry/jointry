package jp.ac.aiit.jointry.services.picture.camera;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

import com.sleepingdumpling.jvideoinput.VideoInput;
import com.sleepingdumpling.jvideoinput.VideoFrame;
import com.sleepingdumpling.jvideoinput.VideoInputException;

/**
 * VideoCamはビデオカメラの画像をImageViewに表示する。
 * <p>このクラスを使用するプログラムはVideoInput/VideoFrameを使用する。 コンパイル時と実行時には以下のJarファイルが必要である。
 * <ul>
 * <li> jvideoinput-1.0.1.jar --- VideoInput/VideoFrameを含む
 * <li> bridj-0.6.2.jar	--- 実行時に必要なクラスを含む
 * </ul>
 * <pre>
 * USAGE:
 *	ImageView camview = new ImageView();
 *	VideoCam videoCam = new VideoCam(camview);
 *	videoCam.open(320, 240);
 *	Canvas canvas = new Canvas();
 *	GraphicsContext gc = canvas.getGraphicsContext2D();
 *	gc.drawImage(camview.getImage(), 0, 0);
 *	videoCam.close();
 * </pre>
 */
public class VideoCam {

    private ImageView camview;
    private int width = 320;
    private int height = 240;

    public VideoCam(ImageView camview) {
        this.camview = camview;
    }
    private CameraTask cameraTask;
    private CameraCapture cameraCapture;
    private BufferedImage cameraShot;

    public void open(int width, int height) {
        this.width = width;
        this.height = height;
        open();
    }

    public void open() {
        cameraTask = new CameraTask();
        cameraCapture = new CameraCapture();
        new Thread(cameraTask).start();
        cameraCapture.start();
    }

    public void close() {
        cameraCapture.stop();
        cameraTask.stop();
    }

    private class CameraCapture extends AnimationTimer {

        @Override
        public void handle(long now) {
            if (cameraShot != null) {
                WritableImage buf = SwingFXUtils.toFXImage(cameraShot, null);
                camview.setImage(buf);
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                }
            }
        }
    }

    private class CameraTask extends Task<Void> {

        private boolean running = true;

        @Override
        protected Void call() {
            VideoInput vin = null;
            try {
                vin = new VideoInput(width, height);
                while (running) {
                    cameraShot = getVideoImage(vin.getNextFrame(null));
                    //cameraShot = vin.getNextFrame(null).getBufferedImage();
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                    }
                }
            } catch (VideoInputException e) {
                log(e);
            } catch (Throwable e) {
                log(e);
            } finally {
                if (vin != null) {
                    vin.stopSession();
                }
            }
            return null;
        }

        private void stop() {
            running = false;
        }
    }

    private BufferedImage getVideoImage(VideoFrame vf) {
        GraphicsConfiguration gc = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        BufferedImage img = gc.createCompatibleImage(vf.getWidth(),
                                                     vf.getHeight(),
                                                     Transparency.TRANSLUCENT);
        if (img.getType() == BufferedImage.TYPE_INT_ARGB
            || img.getType() == BufferedImage.TYPE_INT_ARGB_PRE
            || img.getType() == BufferedImage.TYPE_INT_RGB) {
            // much faster version
            WritableRaster raster = img.getRaster();
            DataBufferInt dataBuffer = (DataBufferInt) raster.getDataBuffer();
            addAlpha(vf.getRawData(), dataBuffer.getData());
            return img;
        } else {
            return vf.getBufferedImage();
        }
    }

    private void addAlpha(byte[] vfdata, int[] argb) {
        for (int i = 0, j = 0; i < vfdata.length; j++) {
            argb[j] = // Alpha | R | G | B
            0xff000000 | // Alpha
                    (vfdata[i++] << 16) & 0xff0000 | // Red
                    (vfdata[i++] << 8) & 0xff00 | // Green
                    vfdata[i++] & 0xff;		// Blue
        }
    }

    void log(Throwable e) {
        Logger.getLogger("VideoCam").log(Level.SEVERE, null, e);
    }
}
