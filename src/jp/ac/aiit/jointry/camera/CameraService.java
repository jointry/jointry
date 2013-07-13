/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.camera;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

import com.sleepingdumpling.jvideoinput.VideoFrame;
import com.sleepingdumpling.jvideoinput.VideoInput;
import com.sleepingdumpling.jvideoinput.VideoInputException;

/**
 *
 * @author kanemoto
 */
public class CameraService {

    private final VideoInput videoInput;

    public CameraService(int width, int height) throws VideoInputException {
        videoInput = new VideoInput(width, height);
    }

    public BufferedImage retrieve() {
        VideoFrame vf = videoInput.getNextFrame(null);
        return getRenderingBufferedImage(vf);
    }

    public void release() {
        videoInput.stopSession();
    }

    private BufferedImage getRenderingBufferedImage(VideoFrame videoFrame) {
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

    private void addAlphaChannel(byte[] rgbBytes, int bytesLen, int[] argbInts) {
        for (int i = 0, j = 0; i < bytesLen; i += 3, j++) {
            argbInts[j] = ((byte) 0xff) << 24 | // Alpha
                    (rgbBytes[i] << 16) & (0xff0000) | // Red
                    (rgbBytes[i + 1] << 8) & (0xff00) | // Green
                    (rgbBytes[i + 2]) & (0xff); // Blue
        }
    }
}
