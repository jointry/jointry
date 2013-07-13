package jp.ac.aiit.jointry.blocks;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Move extends Block {

    public Move() {
        super();
        rect.setFill(getColor());
    }

    public static Color getColor() {
        return Color.YELLOW;
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
        return new Label("移動する");
    }
}
