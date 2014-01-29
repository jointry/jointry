package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Status;

public class Flip extends Procedure {

    private ComboBox cb = new ComboBox();

    public Flip() {
        super();
        rect.setFill(getColor());
    }

    public static Color getColor() {
        return Color.web("99FF99");
    }

    @Override
    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("flip\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }

    @Override
    public Status getStatus() {
        Status status = new Status();
        status.put("id", this.getUUID());
        status.put("flip", null);
        return status;
    }

    @Override
    public void setStatus(Status status) {
        changeable = false; //一時的にリスナーを無効化
        this.setUUID((String) status.get("id"));
        changeable = true;
    }

    @Override
    public Label getLabel() {
        return new Label("むきをかえる");
    }
}
