package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;

public abstract class Procedure extends Statement {

    protected Rectangle rect;
    protected Label lb;

    public Procedure() {
        super();

        rect = new Rectangle();
        rect.setWidth(250);
        rect.setHeight(50);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStroke(Color.GRAY);
        AnchorPane.setTopAnchor(rect, 0.0);

        lb = getLabel();
        AnchorPane.setTopAnchor(lb, 15.0);
        AnchorPane.setLeftAnchor(lb, 170.0);

        getChildren().addAll(rect, lb);

        // コネクタを全面に出すために
        rect.toBack();
    }
}
