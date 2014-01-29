package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.expression.Variable;
import static jp.ac.aiit.jointry.models.blocks.statement.procedure.Calculate.getColor;
import jp.ac.aiit.jointry.util.BlockUtil;

/**
 * 左：変数（Variable）<br />
 * 右：変数 or テキストフィールド or 演算
 */
public class Assign extends Procedure {

    private final Assign me;
    private final TextField tf2;
    private final Label lb1;
    private final Label lb2;
    public Connector leftVariableCon;
    public Connector rightVariableCon;
    public Variable leftVariable;
    public Variable rightVariable;

    public Assign() {
        super();
        me = this;

        rect.setFill(getColor());

        lb1 = new Label("に");
        AnchorPane.setTopAnchor(lb1, 15.0);
        AnchorPane.setLeftAnchor(lb1, 70.0);

        tf2 = new TextField();
        tf2.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf2, 10.0);
        AnchorPane.setLeftAnchor(tf2, 90.0);
        setChangeListener(tf2);

        lb2 = new Label("を");
        AnchorPane.setTopAnchor(lb2, 15.0);
        AnchorPane.setLeftAnchor(lb2, 150.0);

        getChildren().addAll(lb1, tf2, lb2);

        // コネクタを全面に出すために
        rect.toBack();

        this.makeConnectors();
    }

    public static Color getColor() {
        return Color.web("F8BB3D");
    }

    public final Label getLabel() {
        return new Label("だいにゅう");
    }

    @Override
    protected void makeConnectors() {
        super.makeConnectors();

        // Variable
        this.leftVariableCon = new Connector();
        leftVariableCon.detouch();
        leftVariableCon.setWidth(50);
        leftVariableCon.setHeight(2);
        leftVariableCon.setHolder(this);
        leftVariableCon.setPosition(Connector.Position.INSIDE_LEFT);
        AnchorPane.setTopAnchor(leftVariableCon, 10.0);
        AnchorPane.setLeftAnchor(leftVariableCon, 10.0);
        leftVariableCon.toFront();

        // Variable
        this.rightVariableCon = new Connector();
        rightVariableCon.detouch();
        rightVariableCon.setWidth(50);
        rightVariableCon.setHeight(2);
        rightVariableCon.setHolder(this);
        rightVariableCon.setPosition(Connector.Position.INSIDE_RIGHT);
        AnchorPane.setTopAnchor(rightVariableCon, 10.0);
        AnchorPane.setLeftAnchor(rightVariableCon, 90.0);
        rightVariableCon.toFront();

        getChildren().addAll(leftVariableCon, rightVariableCon);
    }

    public void setLeftVariable(Variable v) {
        this.leftVariable = v;
        if (v != null) {
            v.mother = this;
        }
    }

    public void setRightVariable(Variable v) {
        this.rightVariable = v;
        if (v != null) {
            v.mother = this;
        }
    }

    @Override
    public void move(double dx, double dy) {
        if (leftVariable != null) {
            leftVariable.move(dx + 10, dy + 10);
            leftVariable.toFront();
        }
        if (rightVariable != null) {
            rightVariable.move(dx + 90, dy + 10);
            rightVariable.toFront();
        }
        super.move(dx, dy);
    }

    @Override
    public String intern() {
        if (leftVariable == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(leftVariable.intern());
        sb.append(" = ");

        // right
        StringBuilder v = new StringBuilder();
        if (rightVariable != null) {
            v.append(rightVariable.intern());
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
        sb.append("\n");

        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }

        return sb.toString();
    }

    @Override
    public Status getStatus() {
        Status status = new Status();

        status.put("id", this.getUUID());
        if (leftVariable != null) {
            status.put("left", leftVariable.getStatus());
        }

        if (rightVariable != null) {
            status.put("right", rightVariable.getStatus());
        } else {
            status.put("right", tf2.getText());
        }

        return status;
    }

    @Override
    public void setStatus(Status status) {
        changeable = false; //一時的にリスナーを無効化

        this.setUUID((String) status.get("id"));
        for (Object key : status.keySet()) {
            if (key.equals("left")) {
                Variable variable = (Variable) BlockUtil.create("Variable");
                variable.setSprite(getSprite());
                variable.setStatus(BlockUtil.convertMapToStatus(status.get(key)));

                setLeftVariable(variable);
            } else if (key.equals("right")) {
                Object value = status.get(key); //テキスト or ブロックの判断

                if (value instanceof String) {
                    tf2.setText((String) value); //テキスト
                } else {
                    //変数ブロック
                    Variable variable = (Variable) BlockUtil.create("Variable");
                    variable.setSprite(getSprite());
                    variable.setStatus(BlockUtil.convertMapToStatus(value));

                    setRightVariable(variable);
                }
            }
        }

        changeable = true;
    }

    @Override
    public void show() {
        super.show();

        if (leftVariable != null) {
            leftVariable.show();
        }

        if (rightVariable != null) {
            rightVariable.show();
        }
    }

    @Override
    public void toFront() {
        super.toFront();
        if (leftVariable != null) {
            leftVariable.toFront();
        }
        if (rightVariable != null) {
            rightVariable.toFront();
        }
    }
}
