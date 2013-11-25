package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import static jp.ac.aiit.jointry.models.blocks.statement.procedure.Move.getColor;
import jp.ac.aiit.jointry.util.Environment;

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
    public Map getStatus(Map blockMap) {
        String arg = (String) cb.getValue();
        if (arg == null) arg = "0";

        blockMap.put("costume", arg);

        return blockMap;
    }

    @Override
    public void setStatus(Environment env) {
        Map paramMap = env.getValues();
        cb.setValue(paramMap.get("costume"));
    }
}
