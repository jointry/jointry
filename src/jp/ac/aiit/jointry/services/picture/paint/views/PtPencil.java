package jp.ac.aiit.jointry.services.picture.paint.views;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;

public class PtPencil extends PaintTool {

    @Override
    public void paint(Canvas canvas, Point2D start, Point2D end) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setStroke(PaintApplication.getModel().getColor());
        gc.setLineWidth(getOptionController().getSelectTool().getPenSize());
        gc.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
    }
}
