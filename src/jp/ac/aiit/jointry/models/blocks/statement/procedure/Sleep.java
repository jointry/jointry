package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Status;
import static jp.ac.aiit.jointry.models.blocks.statement.procedure.Continue.getColor;

public class Sleep extends Procedure {

    protected ComboBox cb;

    public Sleep() {
        super();
        rect.setFill(getColor());

        cb = new ComboBox();
        cb.setEditable(true);
        cb.setMaxWidth(75.0);
        AnchorPane.setTopAnchor(cb, 10.0);
        AnchorPane.setLeftAnchor(cb, 80.0);
        cb.setItems(FXCollections.observableArrayList(
                "1", "2", "3")); // TODO
        getChildren().add(cb);
    }

    public static Color getColor() {
        return Color.SALMON;
    }

    @Override
    public Label getLabel() {
        return new Label("やすむ");
    }

    @Override
    public String intern() {
        StringBuilder sb = new StringBuilder("sleep ");
        if (cb.getValue().equals("")) {
            sb.append("1");
        } else {
            sb.append(cb.getValue());
        }
        sb.append(";\n");

        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }

    @Override
    public Status getStatus() {
        Status status = new Status();

        status.put("id", this.getUUID());
        String arg = (String) cb.getValue();
        if (arg == null) {
            arg = "1";
        }

        status.put("sleep", arg);

        return status;
    }

    @Override
    public void setStatus(Status status) {
        bChangeEnable = false; //一時的にリスナーを無効化

        this.setUUID((String) status.get("id"));
        cb.setValue(status.get("sleep"));

        bChangeEnable = true;
    }
}
