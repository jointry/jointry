package jp.ac.aiit.jointry.services.paint;

import java.awt.Point;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * ■で書く.
 */
public class PtPencil extends PaintTool {

    private RadioButton thinLine = null;

    @Override
    public void paint(Canvas canvas, Point pS, Point pE, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(color); //色設定

        if (thinLine == null) {
            //デフォルトで細い線を書く
            gc.strokeLine(pS.x, pS.y, pE.x, pE.y);
            return;
        }

        if (thinLine.isSelected()) {
            gc.strokeLine(pS.x, pS.y, pE.x, pE.y);
        } else {
            boldLine(gc, pS, pE);
        }
    }

    @Override
    public Node getOptionPane() {
        VBox vbox = new VBox();
        ToggleGroup bg = new ToggleGroup();

        thinLine = addSelector(vbox, bg, "細い線", true);
        addSelector(vbox, bg, "太い線", false);

        return vbox;
    }

    private void boldLine(GraphicsContext gc, Point pS, Point pE) {
        for (int posIndex = -1; posIndex <= 1; posIndex++) {
            gc.strokeLine(pS.x + posIndex, pS.y, pE.x + posIndex, pE.y);
        }

        gc.strokeLine(pS.x, pS.y - 1, pE.x, pE.y - 1);
        gc.strokeLine(pS.x, pS.y + 1, pE.x, pE.y + 1);
    }

    private RadioButton addSelector(VBox vbox, ToggleGroup bg, String name, boolean on) {
        //ラジオボタン作成
        RadioButton rButton = new RadioButton(name);
        rButton.setSelected(on);
        rButton.setToggleGroup(bg);

        vbox.getChildren().add(rButton);
        return rButton;
    }

    public PtPencil(String resource, String tip) {
        super(resource, tip);
    }
}
