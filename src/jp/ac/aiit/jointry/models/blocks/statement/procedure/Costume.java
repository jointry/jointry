package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Status;

public class Costume extends Procedure {

    protected ComboBox cb;

    // TODO: dummy
    public Costume() {
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
        setChangeListener(cb);
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
        sb.append(" " + arg + ";\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }

    @Override
    public Status getStatus() {
        Status status = new Status();

        String arg = (String) cb.getValue();
        if (arg == null) {
            arg = "0";
        }

        status.put("costume", arg);

        return status;
    }

    @Override
    public void setStatus(Status status) {
        bChangeEnable = false; //一時的にリスナーを無効化

        cb.setValue(status.get("costume"));

        bChangeEnable = true;
    }
}
