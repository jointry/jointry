package jp.ac.aiit.jointry.services.picture.paint.views;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;

public class PtBrush extends PaintTool {

    @Override
    public void paint(Canvas canvas, Point2D start, Point2D end) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(PaintApplication.getModel().getColor());
        gc.setStroke(PaintApplication.getModel().getColor());

        int size = getOptionController().getSelectTool().getPenSize();
        Point2D sBounds = calcBounds(start, Math.abs(size), size);

        switch (getOptionController().getSelectTool().getShape()) {
            case "square":
                gc.fillRect(sBounds.getX(), sBounds.getY(), size, size);
                break;

            case "circle":
                gc.fillOval(sBounds.getX(), sBounds.getY(), size, size);
                break;

            case "line":
                Point2D eBounds = calcBounds(start, -Math.abs(size), -size);
                gc.strokeLine(sBounds.getX(), sBounds.getY(), eBounds.getX(), eBounds.getY());
                break;

            default:
                break;
        }
    }

    /**
     * 中心点と大きさから矩形領域の頂点を求める.
     */
    private Point2D calcBounds(Point2D point, int z, int r) {
        return new Point2D(point.getX() - z / 2, point.getY() - r / 2);
    }
}
