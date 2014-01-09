package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Status;

public class Move extends Procedure {

    protected ComboBox cb_distance;

    public Move() {
        super();
        rect.setFill(getColor());

        cb_distance = new ComboBox();
        cb_distance.setEditable(true);
        cb_distance.setMaxWidth(75.0);
        AnchorPane.setTopAnchor(cb_distance, 10.0);
        AnchorPane.setLeftAnchor(cb_distance, 80.0);
        cb_distance.setItems(FXCollections.observableArrayList(
                "0", "10", "20", "30", "40", "50", "60",
                "-10", "-20", "-30", "-40", "-50", "-60"));

        getChildren().addAll(cb_distance);

        setChangeListener(cb_distance);
    }

    public static Color getColor() {
        return Color.web("E0FFB0");
    }

    @Override
    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("move");
        String arg = (String) cb_distance.getValue();
        if (arg == null) {
            arg = "0";
        }
        sb.append(" ");
        sb.append(arg);
        sb.append("\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }

    @Override
    public Status getStatus() {
        Status status = new Status();

        status.put("id", this.getUUID());
        String arg = (String) cb_distance.getValue();
        if (arg == null) {
            arg = "0";
        }

        status.put("move", arg);

        return status;
    }

    @Override
    public void setStatus(Status status) {
        bChangeEnable = false; //一時的にリスナーを無効化

        this.setUUID((String) status.get("id"));
        cb_distance.setValue(status.get("move"));

        bChangeEnable = true;
    }

    @Override
    public Label getLabel() {
        return new Label("いどうする");
    }
}
