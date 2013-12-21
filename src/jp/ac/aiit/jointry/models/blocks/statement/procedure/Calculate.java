package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.expression.Variable;
import jp.ac.aiit.jointry.util.BlockUtil;

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
        AnchorPane.setTopAnchor(tf1, 10.0);
        AnchorPane.setLeftAnchor(tf1, 70.0);
        setChangeListener(tf1);

        cb = new ChoiceBox(FXCollections.observableArrayList(
                "+", "-", "×", "÷", "%"));
        AnchorPane.setTopAnchor(cb, 10.0);
        AnchorPane.setLeftAnchor(cb, 125.0);

        tf2 = new TextField();
        tf2.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf2, 10.0);
        AnchorPane.setLeftAnchor(tf2, 190.0);
        setChangeListener(tf2);

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
        variableCon.detouch();
        variableCon.setWidth(50);
        variableCon.setHeight(2);
        variableCon.setHolder(this);
        variableCon.setPosition(Connector.Position.LEFT);
        AnchorPane.setTopAnchor(variableCon, 10.0);
        AnchorPane.setLeftAnchor(variableCon, 10.0);
        variableCon.toFront();

        leftVariableCon = new Connector();
        leftVariableCon.detouch();
        leftVariableCon.setWidth(50);
        leftVariableCon.setHeight(2);
        leftVariableCon.setHolder(this);
        leftVariableCon.setPosition(Connector.Position.INSIDE_LEFT);
        AnchorPane.setTopAnchor(leftVariableCon, 10.0);
        AnchorPane.setLeftAnchor(leftVariableCon, 70.0);
        leftVariableCon.toFront();

        getChildren().addAll(variableCon, leftVariableCon);
    }

    public static Color getColor() {
        return Color.web("F6D7B3");
    }

    @Override
    public final Label getLabel() {
        return new Label("けいさん");
    }

    @Override
    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append(variable.intern());
        sb.append(" = (");

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
        sb.append(");\n");

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
        }

        if (leftVariable != null) {
            status.put("left", leftVariable.getStatus());
        } else if (arg1 != null) {
            status.put("left", arg1.getStatus());
        } else {
            status.put("left", tf1.getText());
        }

        if (arg2 != null) {
            status.put("right", arg2.getStatus());
        } else {
            status.put("right", tf2.getText());
        }

        return status;
    }

    @Override
    public void setStatus(Status status) {
        bChangeEnable = false; //一時的にリスナーを無効化

        this.setUUID((String) status.get("id"));

        for (Object key : status.keySet()) {
            if (key.equals("variable")) {
                //変数ブロック
                Variable val = (Variable) BlockUtil.create("Variable");
                val.setSprite(getSprite());
                val.setStatus(BlockUtil.convertMapToStatus(status.get(key)));

                setVariable(val);
            } else if (key.equals("left")) {
                Object value = status.get(key);

                if (value instanceof String) {
                    tf1.setText((String) value); //テキスト
                } else {
                    //変数ブロック
                    Variable val = (Variable) BlockUtil.create("Variable");
                    val.setSprite(getSprite());
                    val.setStatus(BlockUtil.convertMapToStatus(status.get(key)));

                    setLeftVariable(val);
                }
            } else if (key.equals("right")) {
                Object value = status.get(key);

                if (value instanceof String) {
                    tf2.setText((String) status.get(key)); //テキスト
                } else {
                    //変数ブロック
                    arg2 = (Variable) BlockUtil.create("Variable");
                    arg2.setSprite(getSprite());
                    arg2.setStatus(BlockUtil.convertMapToStatus(status.get(key)));
                }
            }
        }

        bChangeEnable = true;
    }

    @Override
    public void show() {
        super.show();

        if (variable != null) {
            variable.show();
        }

        if (leftVariable != null) {
            leftVariable.show();
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
        super.move(dx, dy);

        if (variable != null) {
            variable.toFront();
            variable.move(dx + 10, dy + 10);
        }
        if (leftVariable != null) {
            leftVariable.toFront();
            leftVariable.move(dx + 70, dy + 10);
        }
    }

    public void setLeftVariable(Variable v) {
        this.leftVariable = v;
        if (v != null) {
            v.mother = this;
        }
    }

    @Override
    public void toFront() {
        super.toFront();
        if (variable != null) {
            variable.toFront();
        }
        if (leftVariable != null) {
            leftVariable.toFront();
        }
    }
}
