package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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

}
