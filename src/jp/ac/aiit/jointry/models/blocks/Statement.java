package jp.ac.aiit.jointry.models.blocks;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Statement extends Block {

    protected TextField tf;
    protected final Rectangle rect;

    public Statement() {
        super();

        rect = new Rectangle();
        rect.setWidth(250);
        rect.setHeight(50);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStroke(Color.GRAY);

        tf = new TextField();
        tf.setMaxWidth(50.0);
        tf.setText("0");

        AnchorPane.setTopAnchor(rect, 0.0);
        AnchorPane.setTopAnchor(tf, 10.0);
        AnchorPane.setLeftAnchor(tf, 80.0);
        Label lb = getLabel();
        AnchorPane.setTopAnchor(lb, 10.0);
        AnchorPane.setLeftAnchor(lb, 150.0);

        getChildren().addAll(rect, tf, lb);
    }
}
