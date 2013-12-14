package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import static jp.ac.aiit.jointry.models.blocks.statement.procedure.Calculate.getColor;

public class Continue extends Procedure {

    public Continue() {
        super();
        rect.setFill(getColor());
    }

    public static Color getColor() {
        return Color.BLUEVIOLET;
    }

    public Label getLabel() {
        return new Label("つづける");
    }

    @Override
    public String intern() {
        StringBuilder sb = new StringBuilder("continue;\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }
}
