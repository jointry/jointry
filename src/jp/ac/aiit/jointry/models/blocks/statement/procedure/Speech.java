package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.expression.Variable;
import jp.ac.aiit.jointry.util.BlockUtil;

public class Speech extends Procedure {

    private TextField tf;
    public Connector variableCon;
    public Variable variable;

    public Speech() {
        super();
        rect.setFill(getColor());

        this.tf = new TextField();
        tf.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf, 10.0);
        AnchorPane.setLeftAnchor(tf, 10.0);
        setChangeListener(tf);

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
        variableCon.setFill(Color.TRANSPARENT);
        variableCon.setWidth(50);
        variableCon.setHeight(2);
        variableCon.setHolder(this);
        variableCon.setPosition(Connector.Position.INSIDE_LEFT);
        AnchorPane.setTopAnchor(variableCon, 10.0);
        AnchorPane.setLeftAnchor(variableCon, 10.0);
        variableCon.toFront();

        getChildren().addAll(variableCon);
    }

    @Override
    public Label getLabel() {
        return new Label("はなす");
    }

    @Override
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

    @Override
    public Status getStatus() {
        Status status = new Status();

        status.put("id", this.getUUID());

        if (variable != null) {
            status.put("variable", variable.getStatus());
        } else {
            status.put("variable", tf.getText());
        }

        return status;
    }

    @Override
    public void setStatus(Status status) {
        bChangeEnable = false; //一時的にリスナーを無効化

        this.setUUID((String) status.get("id"));
        Object value = status.get("variable");

        if (value instanceof String) {
            tf.setText((String) value);
        } else {
            //変数ブロック
            Variable val = (Variable) BlockUtil.create("Variable");
            val.setStatus(BlockUtil.convertMapToStatus(value));

            setVariable(variable);
        }

        bChangeEnable = true;
    }

    @Override
    public void show() {
        super.show();

        if (variable != null) {
            variable.show();
        }
    }

    public void setVariable(Variable v) {
        this.variable = v;
        if (v != null) {
            v.mother = this;
        }
    }

    @Override
    public void move(double dx, double dy) {
        if (variable != null) {
            variable.toFront();
            variable.move(dx + 10, dy + 10);
        }
        super.move(dx, dy);
    }
}
