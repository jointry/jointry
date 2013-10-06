package jp.ac.aiit.jointry.models.blocks.procedure.statement;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Rebound extends Statement {

    public Rebound() {
        super();
        rect.setFill(getColor());
        getChildren().remove(cb);
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
        String arg = (String) cb.getValue();
        if (arg == null) arg = "0";

        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" ").append(arg).append("\n");

        if (nextBlock != null) sb.append(nextBlock.blockIntern());

        return sb.toString();
    }

    public Label getLabel() {
        return new Label("はねかえる");
    }
}
