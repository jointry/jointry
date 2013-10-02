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

    public Label getLabel() {
        return new Label("はねかえる");
    }
}
