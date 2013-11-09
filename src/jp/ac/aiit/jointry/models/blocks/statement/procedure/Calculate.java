package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.expression.Variable;

public class Calculate extends Procedure {

    protected Variable arg1;
    protected Variable arg2;
    protected TextField tf1;
    protected TextField tf2;
    protected ChoiceBox cb;
    public Connector variableCon;
    public Variable variable;
    public Connector leftVariableCon;
    public Variable leftVariable;

    public Calculate() {
        super();

        // Use Filter (not Handler) to fire first.
        addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // Move
                double dx = mouseEvent.getSceneX() + anchorX;
                double dy = mouseEvent.getSceneY() + anchorY;
                move(dx, dy);
            }
        });

        rect.setFill(getColor());

        tf1 = new TextField();
        tf1.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf1, 15.0);
        AnchorPane.setLeftAnchor(tf1, 70.0);

        cb = new ChoiceBox(FXCollections.observableArrayList(
                "+", "-", "×", "÷", "%"));
        AnchorPane.setTopAnchor(cb, 15.0);
        AnchorPane.setLeftAnchor(cb, 125.0);

        tf2 = new TextField();
        tf2.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf2, 15.0);
        AnchorPane.setLeftAnchor(tf2, 190.0);

        getChildren().addAll(cb, tf1, tf2);
        getChildren().remove(lb);

        // コネクタを全面に出すために
        rect.toBack();

        makeConnectors();
    }

    @Override
    protected void makeConnectors() {
        super.makeConnectors();

        // Variable
        variableCon = new Connector();
        variableCon.setFill(Color.RED);
        variableCon.setWidth(50);
        variableCon.setHeight(2);
        variableCon.setHolder(this);
        variableCon.setPosition(Connector.Position.LEFT);
        AnchorPane.setTopAnchor(variableCon, 15.0);
        AnchorPane.setLeftAnchor(variableCon, 10.0);
        variableCon.toFront();

        leftVariableCon = new Connector();
        leftVariableCon.setFill(Color.RED);
        leftVariableCon.setWidth(50);
        leftVariableCon.setHeight(2);
        leftVariableCon.setHolder(this);
        leftVariableCon.setPosition(Connector.Position.INSIDE_LEFT);
        AnchorPane.setTopAnchor(leftVariableCon, 15.0);
        AnchorPane.setLeftAnchor(leftVariableCon, 70.0);
        leftVariableCon.toFront();

        getChildren().addAll(variableCon, leftVariableCon);
    }

    public static Color getColor() {
        return Color.LIGHTSALMON;
    }

    public final Label getLabel() {
        return new Label("けいさん");
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append(variable.intern());
        sb.append(" = ");

        StringBuilder v = new StringBuilder();
        if (leftVariable != null) {
            v.append(leftVariable.intern());
        } else {
            if (arg1 != null) {
                v.append(arg1);
            } else {
                try {
                    // As number
                    v.append(Integer.parseInt(tf1.getText()));
                } catch (NumberFormatException nfe) {
                    // As String
                    v.append("\"");
                    v.append(tf1.getText());
                    v.append("\"");
                }
            }
        }

        v.append(" ");
        v.append(cb.getValue());
        v.append(" ");

        if (arg2 != null) {
            v.append(arg2);
        } else {
            try {
                // As number
                v.append(Integer.parseInt(tf2.getText()));
            } catch (NumberFormatException nfe) {
                // As String
                v.append("\"");
                v.append(tf2.getText());
                v.append("\"");
            }
        }

        sb.append(v.toString());
        sb.append(";\n");

        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }

        return sb.toString();
    }

    public void setVariable(Variable v) {
        this.variable = v;
        if (v != null) {
            v.mother = this;
        }
    }

    public void move(double dx, double dy) {
        super.move(dx, dy);
        if (variable != null) {
            variable.move(dx + 10, dy + 15);
            variable.toFront();
        }
        if (leftVariable != null) {
            leftVariable.move(dx + 70, dy + 15);
            leftVariable.toFront();
        }
    }

    public void setLeftVariable(Variable v) {
        this.leftVariable = v;
        if (v != null) {
            v.mother = this;
        }
    }
}
