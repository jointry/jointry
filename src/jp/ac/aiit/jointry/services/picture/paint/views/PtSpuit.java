package jp.ac.aiit.jointry.services.picture.paint.views;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;


public class PtSpuit extends PaintTool {

    @Override
    public void paint(Canvas canvas, Point2D start, Point2D end) {
        WritableImage snapshot = canvas.snapshot(null, null);
        PixelReader reader = snapshot.getPixelReader();

        PaintApplication.getModel().setColor(reader.getColor((int) start.getX(), (int) start.getY()));
    }
}
