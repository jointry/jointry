package jp.ac.aiit.jointry.models.blocks.procedure.statement;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jp.ac.aiit.jointry.models.blocks.procedure.Procedure;

public class Statement extends Procedure {

    protected ComboBox cb;
    protected final Rectangle rect;

    public Statement() {
        super();

        rect = new Rectangle();
        rect.setWidth(250);
        rect.setHeight(50);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStroke(Color.GRAY);

        cb = new ComboBox();
        cb.setEditable(true);
        cb.setMaxWidth(75.0);

        AnchorPane.setTopAnchor(rect, 0.0);
        AnchorPane.setTopAnchor(cb, 10.0);
        AnchorPane.setLeftAnchor(cb, 80.0);
        Label lb = getLabel();
        AnchorPane.setTopAnchor(lb, 10.0);
        AnchorPane.setLeftAnchor(lb, 150.0);

        getChildren().addAll(rect, cb, lb);

        // コネクタを全面に出すために
        rect.toBack();
    }
}
