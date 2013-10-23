package jp.ac.aiit.jointry.models.blocks.procedure.statement;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jp.ac.aiit.jointry.models.blocks.expression.Variable;

public class Calculate extends Statement {

    protected final Rectangle rect;
    protected Variable arg1;
    protected Variable arg2;
    protected TextField tf1;
    protected TextField tf2;
    protected ChoiceBox cb;

    public Calculate() {
        this.cb = new ChoiceBox(FXCollections.observableArrayList(
                "+", "-", "×", "÷", "%"));

        // Use Filter (not Handler) to fire first.
        addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // Move
                double dx = mouseEvent.getSceneX() + anchorX;
                double dy = mouseEvent.getSceneY() + anchorY;
                move(dx, dy);
                /*
                 if (getCollision() == null) {
                 return;
                 }

                 // 内部の接続
                 If target = (If) con.getHolder();
                 target.addEmbryo(myBlock);
                 move(target.getLayoutX() + 50, target.getLayoutY() + 10); // TODO
                 */
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

        AnchorPane.setTopAnchor(cb, 5.0);
        AnchorPane.setLeftAnchor(cb, 10.0);

        tf1 = new TextField();
        tf1.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf1, 22.0);
        AnchorPane.setLeftAnchor(tf1, 10.0);
        tf2 = new TextField();
        tf2.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf2, 22.0);
        AnchorPane.setRightAnchor(tf2, 10.0);

        getChildren().addAll(rect, cb, tf1, tf2);

        // コネクタを全面に出すために
        rect.toBack();
    }

    public static Color getColor() {
        return Color.SLATEGREY;
    }

    public final Label getLabel() {
        return new Label("けいさん");
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("Calculate ");
        if (arg1 != null) {
            sb.append(arg1);
        } else {
            try {
                // As number
                sb.append(Integer.parseInt(tf1.getText()));
            } catch (NumberFormatException nfe) {
                // As String
                sb.append("\"");
                sb.append(tf1.getText());
                sb.append("\"");
            }
        }

        if (arg2 != null) {
            sb.append(arg2);
        } else {
            try {
                // As number
                sb.append(Integer.parseInt(tf2.getText()));
            } catch (NumberFormatException nfe) {
                // As String
                sb.append("\"");
                sb.append(tf2.getText());
                sb.append("\"");
            }
        }
        return sb.toString();
    }
}
