package jp.ac.aiit.jointry.models.blocks.procedure.statement;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Rotate extends Statement {

    public Rotate() {
        super();
        rect.setFill(getColor());
        cb.setItems(FXCollections.observableArrayList(
                "0", "30", "60", "90", "120", "150", "180"));
    }

    public static Color getColor() {
        return Color.ORANGERED;
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("rotate");
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

    public String blockIntern() {
        String arg = (String) cb.getValue();
        if (arg == null) arg = "0";

        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" ").append(arg).append("\n");

        if (nextBlock != null) sb.append(nextBlock.blockIntern());

        return sb.toString();
    }

    public Label getLabel() {
        return new Label("かいてんする");
    }
}
