package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Rebound extends Procedure {

    public Rebound() {
        super();
        rect.setFill(getColor());
    }

    public static Color getColor() {
        return Color.GREENYELLOW;
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("rebound 0\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }

    public String blockIntern() {
        String arg = "";
        if (arg == null) {
            arg = "0";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" ").append(arg).append("\n");

        if (nextBlock != null) {
            sb.append(nextBlock.blockIntern());
        }

        return sb.toString();
    }

    public Label getLabel() {
        return new Label("はねかえる");
    }
}
