/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.paint;

import java.awt.Point;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author kanemoto
 */
public class PtSave extends PaintTool {

    private Image image;

    public PtSave(String resource, String tip) {
        super(resource, tip);
    }

    public Image getImage() {
        return image;
    }

    @Override
    public void paint(Canvas canvas, Point pS, Point pE, Color color) {
        //単純な透過処理
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage orgImage = canvas.snapshot(params, null);

        PixelReader reader = orgImage.getPixelReader();

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        int startX = getStartX(reader, canvasWidth, canvasHeight);
        int startY = getStartY(reader, canvasWidth, canvasHeight);
        int endX = getEndX(reader, canvasWidth, canvasHeight);
        int endY = getEndY(reader, canvasWidth, canvasHeight);

        System.out.println("StartX :" + startX);
        System.out.println("StartY :" + startY);
        System.out.println("endX :" + endX);
        System.out.println("endY :" + endY);

        //何も描かれていないキャンバス
        if (startX == 0 && startY == 0 && endX == 0 && endY == 0) {
            System.out.println("test");
            image = null;
            return;
        }

        image = new WritableImage(reader, startX, startY, endX - startX, endY - startY);
    }

    private int getStartX(PixelReader reader, double canvasWidth, double canvasHeight) {
        //始点x座標
        for (int x = 0; x < canvasWidth; x++) {
            for (int y = 0; y < canvasHeight; y++) {
                if (!reader.getColor(x, y).equals(Color.TRANSPARENT)) {
                    return x;
                }
            }
        }

        return 0;
    }

    private int getStartY(PixelReader reader, double canvasWidth, double canvasHeight) {
        //始点y座標
        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                if (!reader.getColor(x, y).equals(Color.TRANSPARENT)) {
                    return y;
                }
            }
        }

        return 0;
    }

    private int getEndX(PixelReader reader, double canvasWidth, double canvasHeight) {
        int endX = 0;

        //終点x座標
        for (int x = 0; x < canvasWidth; x++) {
            for (int y = 0; y < canvasHeight; y++) {
                if (!reader.getColor(x, y).equals(Color.TRANSPARENT)) {
                    endX = x;
                }
            }
        }

        return endX;
    }

    private int getEndY(PixelReader reader, double canvasWidth, double canvasHeight) {
        int endY = 0;

        //終点y座標
        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                if (!reader.getColor(x, y).equals(Color.TRANSPARENT)) {
                    endY = y;
                }
            }
        }

        return endY;
    }
}
