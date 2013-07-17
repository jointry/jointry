package jp.ac.aiit.jointry.models.blocks;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Rotate extends Statement {

    public Rotate() {
        super();
        rect.setFill(getColor());
    }

    public static Color getColor() {
        return Color.ORANGERED;
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("rotate");
        String arg = tf.getText();
        if (arg == null) {
            arg = "0";
        }
        sb.append(" " + arg + "\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }

    public Label getLabel() {
        return new Label("かいてんする");
    }
}
