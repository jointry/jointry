package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.util.Environment;

public class Rotate extends Procedure {

    protected ComboBox cb;

    public Rotate() {
        super();
        rect.setFill(getColor());
        cb = new ComboBox();
        cb.setEditable(true);
        cb.setMaxWidth(75.0);
        AnchorPane.setTopAnchor(cb, 10.0);
        AnchorPane.setLeftAnchor(cb, 80.0);
        cb.setItems(FXCollections.observableArrayList(
                "0", "30", "60", "90", "120", "150", "180"));
        getChildren().add(cb);
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

    @Override
    public Map getStatus(Map blockMap) {
        String arg = (String) cb.getValue();
        if (arg == null) arg = "0";

        blockMap.put("rotate", arg);

        return blockMap;
    }

    @Override
    public void setStatus(Environment env) {
        Map paramMap = env.getValues();
        cb.setValue(paramMap.get("rotate"));
    }

    public Label getLabel() {
        return new Label("かいてんする");
    }
}
