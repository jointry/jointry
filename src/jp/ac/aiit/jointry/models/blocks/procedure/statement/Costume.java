package jp.ac.aiit.jointry.models.blocks.procedure.statement;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import static jp.ac.aiit.jointry.models.blocks.procedure.statement.Move.getColor;

public class Costume extends Statement {

    // TODO: dummy
    public Costume() {
        super();
        rect.setFill(getColor());
        cb.setItems(FXCollections.observableArrayList(
                "1", "2", "3")); // TODO
    }

    public static Color getColor() {
        return Color.AQUA;
    }

    public Label getLabel() {
        return new Label("コスチューム");
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("costume");
        String arg = (String) cb.getValue();
        if (arg == null) {
            arg = "0";
        }
        sb.append(" " + arg + "\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }
}
