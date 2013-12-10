package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Sprite;
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
        AnchorPane.setTopAnchor(tf1, 15.0);
        AnchorPane.setLeftAnchor(tf1, 70.0);
        setChangeListener(tf1);

        cb = new ChoiceBox(FXCollections.observableArrayList(
                "+", "-", "×", "÷", "%"));
        AnchorPane.setTopAnchor(cb, 15.0);
        AnchorPane.setLeftAnchor(cb, 125.0);

        tf2 = new TextField();
        tf2.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf2, 15.0);
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
        variableCon.setFill(Color.TRANSPARENT);
        variableCon.setWidth(50);
        variableCon.setHeight(2);
        variableCon.setHolder(this);
        variableCon.setPosition(Connector.Position.LEFT);
        AnchorPane.setTopAnchor(variableCon, 15.0);
        AnchorPane.setLeftAnchor(variableCon, 10.0);
        variableCon.toFront();

        leftVariableCon = new Connector();
        leftVariableCon.setFill(Color.TRANSPARENT);
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
    public Map getStatus() {
        Map<String, Object> status = new HashMap();

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
        for (Object key : status.keySet()) {
            if (key.equals("variable")) {
                //変数ブロック
                Variable val = (Variable) BlockUtil.createBlock("Variable");
                val.setStatus((Status) status.get(key));

                setVariable(val);
            } else if (key.equals("left")) {
                Object value = status.get(key);

                if (value instanceof String) {
                    tf1.setText((String) value); //テキスト
                } else {
                    //変数ブロック
                    Variable val = (Variable) BlockUtil.createBlock("Variable");
                    val.setStatus((Status) status.get(key));

                    setLeftVariable(variable);
                }
            } else if (key.equals("right")) {
                Object value = status.get(key);

                if (value instanceof String) {
                    tf2.setText((String) status.get(key)); //テキスト
                } else {
                    //変数ブロック
                    arg2 = (Variable) BlockUtil.createBlock("Variable");
                    arg2.setStatus((Status) status.get(key));
                }
            }
        }
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
