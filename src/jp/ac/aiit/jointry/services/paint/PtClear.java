package jp.ac.aiit.jointry.services.paint;

import java.awt.Point;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PtClear extends PaintTool {

    public PtClear(String resource, String tip) {
        super(resource, tip);
    }

    @Override
    public void paint(Canvas canvas, Point pS, Point pE, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.TRANSPARENT);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
