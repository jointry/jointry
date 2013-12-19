package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Status;

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

    @Override
    public Status getStatus() {
        Status status = new Status();
        status.put("id", this.getUUID());
        status.put("rebound", "0");
        return status;
    }

    @Override
    public void setStatus(Status status) {
        //idだけ
        this.setUUID((String) status.get("id"));
    }

    public Label getLabel() {
        return new Label("はねかえる");
    }
}
