package jp.ac.aiit.jointry.models.blocks.arithmetic.condition;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class Eq extends Condition {

    public Eq() {
        super();
        op = new Label(" おなじ ");
        AnchorPane.setTopAnchor(op, 25.0);
        AnchorPane.setLeftAnchor(op, 80.0);
        getChildren().addAll(op);
    }

    @Override
    public String getOp() {
        return " == ";
    }
}
