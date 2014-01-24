package jp.ac.aiit.jointry.services.picture.paint.views;

import java.util.Random;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;

public class PtAirbrush extends PaintTool {

    private static final int AMOUNT_OF_LIQUID = 5;
    private static Random rand = new Random();

    @Override
    public void paint(Canvas canvas, Point2D start, Point2D end) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(PaintApplication.getModel().getColor());
        gc.setStroke(PaintApplication.getModel().getColor());

        int r = getOptionController().selectTool.getPenSize();
        int n = r * AMOUNT_OF_LIQUID; // エアブラシのドット量
        int airbrushRR = r * r;

        for (int i = 0; i < n; i++) {
            int x, y;
            // XY方向に一定距離で散布する。
            // エアブラシの半径内に収まっていなければ再散布する。
            do {
                x = rand.nextInt(2 * r + 1) - r;
                y = rand.nextInt(2 * r + 1) - r;
            } while (x * x + y * y > airbrushRR);
            gc.fillRect(start.getX() + x, start.getY() + y, 1, 1);
        }
    }
}
