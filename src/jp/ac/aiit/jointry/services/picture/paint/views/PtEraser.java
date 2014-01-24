package jp.ac.aiit.jointry.services.picture.paint.views;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;

public class PtEraser extends PaintTool {

    private final EraserOptionController optionController = new EraserOptionController();

    @Override
    public void paint(Canvas canvas, Point2D start, Point2D end) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (optionController.isColor()) {
            gc.setFill(PaintApplication.getModel().getColor());
        } else {
            gc.setFill(Color.WHITE);
        }

        int size = getOptionController().selectTool.getPenSize();
        gc.fillRect(start.getX(), start.getY(), size, size);
    }

    @Override
    protected OptionController getOptionController() {
        return optionController;
    }

    private class EraserOptionController extends OptionController {

        @FXML
        private CheckBox colorEraser;

        private boolean isColor() {
            return colorEraser.isSelected();
        }
    }
}
