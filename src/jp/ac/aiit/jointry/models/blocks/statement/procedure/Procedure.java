package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Procedure extends jp.ac.aiit.jointry.models.blocks.statement.Statement {

    protected Rectangle rect;

    public Procedure() {
        super();

        rect = new Rectangle();
        rect.setWidth(250);
        rect.setHeight(50);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStroke(Color.GRAY);
        AnchorPane.setTopAnchor(rect, 0.0);

        Label lb = getLabel();
        AnchorPane.setTopAnchor(lb, 10.0);
        AnchorPane.setLeftAnchor(lb, 150.0);

        getChildren().addAll(rect, lb);

        // コネクタを全面に出すために
        rect.toBack();
    }
}
