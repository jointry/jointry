package jp.ac.aiit.jointry.models.blocks.expression;

import javafx.scene.control.Label;
import jp.ac.aiit.jointry.models.Status;

public class Rebound extends Condition {

    public Rebound() {
        getChildren().removeAll(super.cb, tf1, tf2);
    }

    @Override
    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("rebound == 1");
        return sb.toString();
    }

    @Override
    public Label getLabel() {
        return new Label("端にあたった");
    }

    @Override
    public Status getStatus() {
        Status status = new Status();
        status.put("id", this.getUUID());

        return status;
    }

    @Override
    public void setStatus(Status status) {

        this.setUUID((String) status.get("id"));

    }

    @Override
    public void show() {
        getSprite().getScriptPane().getChildren().add(this);
    }

    @Override
    public void move(double dx, double dy) {
        super.move(dx, dy);
    }
}
