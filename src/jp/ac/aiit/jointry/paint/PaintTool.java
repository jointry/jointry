package jp.ac.aiit.jointry.paint;

import java.awt.Point;
import java.net.URL;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public abstract class PaintTool extends Button implements IOptionPane {

    public abstract void paint(Canvas canvas, Point pS, Point pE, Color color);

    public PaintTool(String resource, String tip) {
        setPrefSize(50, 50);
        setTooltip(new Tooltip(tip));

        if (resource != null) {
            URL url = getClass().getResource("images/" + resource + ".png");
            this.setGraphic(new ImageView(new Image(url.toString())));
        }
    }

    @Override
    public Node getOptionPane() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
