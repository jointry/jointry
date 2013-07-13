package jp.ac.aiit.jointry.statics;

import java.awt.image.BufferedImage;

/**
 * TODO: データのやり取りが決まるまでの暫定クラス
 */
public class TestData {

    private static BufferedImage buf;

    public BufferedImage getCameraFile() {
        return buf;
    }

    public void setCameraFile(BufferedImage buf) {
        this.buf = buf;
    }
}
