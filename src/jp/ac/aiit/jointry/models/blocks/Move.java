package jp.ac.aiit.jointry.models.blocks;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Move extends Statement {

    public Move() {
        super();
        rect.setFill(getColor());
    }

    public static Color getColor() {
        return Color.LEMONCHIFFON;
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("move");
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
        return new Label("いどうする");
    }
}
