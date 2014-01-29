package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Status;
import static jp.ac.aiit.jointry.models.blocks.statement.procedure.Calculate.getColor;

public class Continue extends Procedure {

    public Continue() {
        super();
        rect.setFill(getColor());
    }

    public static Color getColor() {
        return Color.web("CFE2F7");
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

    @Override
    public Status getStatus() {
        Status status = new Status();
        status.put("id", this.getUUID());
        status.put("continue", "");
        return status;
    }

    @Override
    public void setStatus(Status status) {
        changeable = false; //一時的にリスナーを無効化
        this.setUUID((String) status.get("id"));
        changeable = true;
    }
}
