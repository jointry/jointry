package jp.ac.aiit.jointry.models.blocks;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import static jp.ac.aiit.jointry.models.blocks.While.getColor;

public class If extends CodeBlock {

    // TODO: dummy
    public If() {
        super();
        p = new Polygon();
        resize();
        p.setFill(getColor());
        p.setStroke(Color.GRAY);

        getChildren().addAll(p);

        // コネクタを全面にするために
        p.toBack();
    }

    public static Color getColor() {
        return Color.BURLYWOOD;
    }

    public Label getLabel() {
        return new Label("せんたく");
    }
}
