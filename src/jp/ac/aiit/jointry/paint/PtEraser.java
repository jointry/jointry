package jp.ac.aiit.jointry.paint;

import java.awt.Point;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.paint.ListSelectOption.ToolOption;

public class PtEraser extends PaintTool {

    private CheckBox isColorEraser = new CheckBox("カラ消し");
    private EraserPanel panel;

    public PtEraser(String name, String tip) {
        super(name, tip);
    }

    @Override
    public void paint(Canvas canvas, Point pS, Point pE, Color color) {
        int penSize = panel.getValue();

        if (isColorEraser.isSelected()) {
            eraseColor(canvas, pE.x - penSize / 2, pE.y - penSize / 2, penSize, color);
        } else {
            brushLine(canvas, Color.WHITE, pS, pE, penSize);
        }
    }

    private void brushLine(Canvas canvas, Color color, Point p1, Point p2, int penSize) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(color);
        gc.fillRect(p2.x - penSize / 2, p2.y - penSize / 2, penSize, penSize);

        if (!p1.equals(p2)) {
            int dx = p2.x - p1.x;
            int dy = p2.y - p1.y;
            double delta = Math.sqrt(dx * dx + dy * dy);
            for (int i = 0; i < delta; i++) {
                int x = p1.x + (int) (dx * i / delta);
                int y = p1.y + (int) (dy * i / delta);
                gc.fillRect(x - penSize / 2, y - penSize / 2, penSize, penSize);
            }
        }
    }

    private void eraseColor(Canvas canvas, int px, int py, int penSize, Color color) {
        int x1 = Math.max(px, 0);
        int x2 = Math.min(px + penSize, (int) canvas.getWidth());
        int y1 = Math.max(py, 0);
        int y2 = Math.min(py + penSize, (int) canvas.getHeight());

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(color);
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                gc.strokeLine(x, y, x, y);
            }
        }
    }

    @Override
    public Node getOptionPane() {
        VBox vbox = new VBox();
        vbox.getChildren().add(isColorEraser);

        panel = new EraserPanel();
        vbox.getChildren().add(panel.getPane());

        return vbox;
    }

    class EraserPanel extends ListSelectOption {

        EraserPanel() {
            addOption(new ToolOption("eraser_1", 4), true);
            addOption(new ToolOption("eraser_2", 6));
            addOption(new ToolOption("eraser_3", 8));
            addOption(new ToolOption("eraser_4", 10));
            addOption(new ToolOption("eraser_5", 12));
        }
    }
}
