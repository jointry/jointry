package jp.ac.aiit.jointry.services.picture.paint.util;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageUtil {

    //画像はキャンバスの大きさで保存されるため、
    //絵が描いてある範囲だけを指定して保存する
    public static Image justResize(Canvas canvas) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        PixelReader reader = canvas.snapshot(params, null).getPixelReader();

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        int startX = getStartX(reader, canvasWidth, canvasHeight);
        int startY = getStartY(reader, canvasWidth, canvasHeight);
        int endX = getEndX(reader, canvasWidth, canvasHeight);
        int endY = getEndY(reader, canvasWidth, canvasHeight);

        //何も描かれていないキャンバス
        if (startX == 0 && startY == 0 && endX == 0 && endY == 0) {
            return null;
        }

        return new WritableImage(reader, startX, startY, endX - startX, endY - startY);
    }

    private static int getStartX(PixelReader reader, double canvasWidth, double canvasHeight) {
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

    private static int getStartY(PixelReader reader, double canvasWidth, double canvasHeight) {
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

    private static int getEndX(PixelReader reader, double canvasWidth, double canvasHeight) {
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

    private static int getEndY(PixelReader reader, double canvasWidth, double canvasHeight) {
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
