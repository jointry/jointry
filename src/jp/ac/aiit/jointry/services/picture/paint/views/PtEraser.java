package jp.ac.aiit.jointry.services.picture.paint.views;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;
import jp.ac.aiit.jointry.services.picture.paint.ctrl.EraserOptionController;

public class PtEraser extends PaintTool {

    @Override
    public void paint(Canvas canvas, Point2D start, Point2D end) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (((EraserOptionController) getOptionController()).isColor()) {
            gc.setFill(PaintApplication.getModel().getColor());
        } else {
            gc.setFill(Color.WHITE);
        }

        int size = getOptionController().getSelectTool().getPenSize();
        gc.fillRect(start.getX(), start.getY(), size, size);
    }
}
