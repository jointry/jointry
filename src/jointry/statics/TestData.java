/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jointry.statics;

import java.awt.image.BufferedImage;

/**
 * アーキテクトとデータのやり取りが決まるまでの暫定クラス
 * 
 * @author kanemoto
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
