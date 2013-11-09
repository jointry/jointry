package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.expression.Variable;

public class Speech extends Procedure {

    private TextField tf;
    public Connector variableCon;
    public Variable variable;

    public Speech() {
        super();
        rect.setFill(getColor());

        this.tf = new TextField();
        tf.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf, 15.0);
        AnchorPane.setLeftAnchor(tf, 10.0);

        getChildren().add(tf);
        makeConnectors();
    }

    public static Color getColor() {
        return Color.LAVENDER;
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
        variableCon.setPosition(Connector.Position.INSIDE_LEFT);
        AnchorPane.setTopAnchor(variableCon, 15.0);
        AnchorPane.setLeftAnchor(variableCon, 10.0);
        variableCon.toFront();

        getChildren().addAll(variableCon);
    }

    public Label getLabel() {
        return new Label("はなす");
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();

        String content = "";
        if (variable != null) {
            content = variable.intern();
        } else {
            content = " \"" + tf.getText() + "\"";
        }

        sb.append("speech ").append(content).append(";\n");

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
    }
}
