package jp.ac.aiit.jointry.models.blocks.arithmetic;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jp.ac.aiit.jointry.models.blocks.Block;
import static jp.ac.aiit.jointry.models.blocks.arithmetic.condition.Condition.getColor;

/**
 * 名前と値があればいい
 */
public class Variable extends Block {

    private final Rectangle rect;

    public Variable(String name) {
        super();

        rect = new Rectangle();
        rect.setWidth(200);
        rect.setHeight(50);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStroke(Color.GRAY);
        rect.setFill(getColor());
        AnchorPane.setTopAnchor(rect, 0.0);

        Label lb = new Label(name);
        AnchorPane.setTopAnchor(lb, 5.0);
        AnchorPane.setLeftAnchor(lb, 10.0);

        getChildren().addAll(rect, lb);
    }
}
