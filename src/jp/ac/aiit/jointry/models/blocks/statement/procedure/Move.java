package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Status;

public class Move extends Procedure {

    protected ComboBox cb;

    public Move() {
        super();
        rect.setFill(getColor());
        cb = new ComboBox();
        cb.setEditable(true);
        cb.setMaxWidth(75.0);
        AnchorPane.setTopAnchor(cb, 10.0);
        AnchorPane.setLeftAnchor(cb, 80.0);
        cb.setItems(FXCollections.observableArrayList(
                "0", "10", "20", "30", "40", "50", "60"));
        getChildren().add(cb);
        setChangeListener(cb);
    }

    public static Color getColor() {
        return Color.LEMONCHIFFON;
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("move");
        //String arg = tf.getText();
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

    @Override
    public Map getStatus() {
        Map<String, Object> status = new HashMap();

        String arg = (String) cb.getValue();
        if (arg == null) {
            arg = "0";
        }

        status.put("move", arg);

        return status;
    }

    @Override
    public void setStatus(Status status) {
        cb.setValue(status.get("move"));
    }

    public Label getLabel() {
        return new Label("いどうする");
    }
}
