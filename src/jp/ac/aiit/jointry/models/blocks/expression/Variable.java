package jp.ac.aiit.jointry.models.blocks.expression;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import static jp.ac.aiit.jointry.models.blocks.expression.Condition.getColor;
import jp.ac.aiit.jointry.models.blocks.procedure.codeblock.If;

/**
 * 名前と値があればいい
 */
public class Variable extends Expression {

    private Rectangle rect;
    private String name;
    private StringProperty value = new SimpleStringProperty("");

    public Variable() {
        super();

        // Use Filter (not Handler) to fire first.
        addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // initializeLink();

                // Move
                double dx = mouseEvent.getSceneX() + anchorX;
                double dy = mouseEvent.getSceneY() + anchorY;
                move(dx, dy);

                //if (getCollision() == null) {
                //    return;
                //}
                // 内部の接続
                //If target = (If) con.getHolder();
                //target.addEmbryo(myBlock);
                //move(target.getLayoutX() + 50, target.getLayoutY() + 10); // TODO
            }
        });

        rect = new Rectangle();
        rect.setWidth(200);
        rect.setHeight(50);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStroke(Color.GRAY);
        rect.setFill(getColor());
        AnchorPane.setTopAnchor(rect, 0.0);

        getChildren().addAll(rect);
    }

    public StringProperty getValueProperty() {
        return value;
    }

    public static Color getColor() {
        return Color.TOMATO;
    }

    public Label getLabel() {
        return new Label("へんすう");
    }

    public void setName(String name) {
        this.name = name;

        Label lb = new Label();
        lb.setText(name);
        AnchorPane.setTopAnchor(lb, 5.0);
        AnchorPane.setLeftAnchor(lb, 10.0);
        getChildren().add(lb);
    }

    public void setValue(String value) {
        this.value.setValue(value);
    }
}
