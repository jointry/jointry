package jp.ac.aiit.jointry.models.blocks.expression;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.If;
import jp.ac.aiit.jointry.services.broker.app.BlockDialog;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.VM_BLOCK_ADDEMBRYO;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.VM_BLOCK_MOVE;
import jp.ac.aiit.jointry.util.BlockUtil;

public class Condition extends Expression {

    protected final Rectangle rect;
    public If mother;
    public Condition me;
    public Connector topCon;
    public Connector bottomCon;
    public Connector leftCon;
    public Connector rightCon;
    public Connector leftVariableCon;
    public Connector rightVariableCon;
    protected TextField tf1;
    protected TextField tf2;
    protected ComboBox cb;
    public Variable leftVariable;
    public Variable rightVariable;
    private Map<String, String> operation = new HashMap<>();

    public Condition() {
        super();
        me = this;
        operation.put("おなじ", " == ");
        operation.put("いじょう", " >= ");
        operation.put("いか", " <= ");
        operation.put("ちいさい", " < ");

        // Use Filter (not Handler) to fire first.
        addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                initializeLink();

                // Move
                double dx = mouseEvent.getSceneX() + anchorX;
                double dy = mouseEvent.getSceneY() + anchorY;
                move(dx, dy);

                BlockDialog.sendMessage(VM_BLOCK_MOVE, me);

                if (getCollision() == null) {
                    return;
                }

                // 内部の接続
                If target = (If) con.getHolder();
                target.addEmbryo(me);
                move(target.getLayoutX() + 50, target.getLayoutY() + 20);

                BlockDialog.sendMessage(VM_BLOCK_ADDEMBRYO, me);
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

        Label lb = this.getLabel();
        AnchorPane.setTopAnchor(lb, 5.0);
        AnchorPane.setLeftAnchor(lb, 10.0);

        tf1 = new TextField();
        tf1.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf1, 22.0);
        AnchorPane.setLeftAnchor(tf1, 10.0);
        tf2 = new TextField();
        tf2.setPrefWidth(50.0);
        AnchorPane.setTopAnchor(tf2, 22.0);
        AnchorPane.setRightAnchor(tf2, 10.0);

        getChildren().addAll(rect, lb, tf1, tf2);
        setChangeListener(tf1);
        setChangeListener(tf2);

        cb = new ComboBox();
        cb.setItems(FXCollections.observableArrayList(operation.keySet()));

        AnchorPane.setTopAnchor(cb, 22.0);
        AnchorPane.setLeftAnchor(cb, 68.0);
        cb.setPrefSize(60, 20);
        getChildren().addAll(cb);
        setChangeListener(cb);

        // コネクタを全面に出すために
        rect.toBack();

        makeConnectors();
    }

    @Override
    public void initializeLink() {
        if (mother != null) {
            mother.embryo = null;
            mother = null;
        }
    }

    public static Color getColor() {
        return Color.LIGHTGREEN;
    }

    public Label getLabel() {
        return new Label("じょうけん");
    }

    public Connector getCollision() {
        Connector connector = null;
        BorderPane root = (BorderPane) getScene().getRoot();
        TabPane tabs = (TabPane) root.getCenter();

        for (Tab tab : tabs.getTabs()) {
            if (tab == null) {
                continue;
            }
            if (!"scriptPane".equals(tab.getContent().getId())) {
                continue;
            }

            // Inside scriptPane
            AnchorPane scriptPane = (AnchorPane) tab.getContent();
            for (Node node : scriptPane.getChildren()) {
                if (node == me) {
                    continue;
                }
                if (!(node instanceof If)) {
                    continue;
                }

                // Inside Block
                Block target = (Block) node;
                for (Node n : target.getChildren()) {
                    if (n instanceof Connector) {
                        Connector c = (Connector) n;
                        c.setFill(Color.TRANSPARENT);
                        Shape intersect = null;

                        // 内部の接触
                        intersect = Shape.intersect(c, me.leftCon);
                        if (intersect.getBoundsInLocal().getWidth() != -1) {
                            if (c.getPosition() == Connector.Position.CENTER) {
                                connector = c;
                                break;
                            }
                        }
                    }
                }
            }
        }
        setConnector(connector);
        return connector;
    }

    private void makeConnectors() {
        // Connectors
        this.leftCon = new Connector();
        leftCon.setFill(Color.TRANSPARENT);
        leftCon.setWidth(10);
        leftCon.setHeight(50);
        leftCon.setHolder(me);
        leftCon.setPosition(Connector.Position.LEFT);
        AnchorPane.setLeftAnchor(leftCon, 0.0);

        // Variable
        this.leftVariableCon = new Connector();
        leftVariableCon.setFill(Color.TRANSPARENT);
        leftVariableCon.setWidth(50);
        leftVariableCon.setHeight(2);
        leftVariableCon.setHolder(me);
        leftVariableCon.setPosition(Connector.Position.INSIDE_LEFT);
        AnchorPane.setTopAnchor(leftVariableCon, 22.0);
        AnchorPane.setLeftAnchor(leftVariableCon, 10.0);

        // Variable
        this.rightVariableCon = new Connector();
        rightVariableCon.setFill(Color.TRANSPARENT);
        rightVariableCon.setWidth(50);
        rightVariableCon.setHeight(2);
        rightVariableCon.setHolder(me);
        rightVariableCon.setPosition(Connector.Position.INSIDE_RIGHT);
        AnchorPane.setTopAnchor(rightVariableCon, 22.0);
        AnchorPane.setRightAnchor(rightVariableCon, 10.0);

        getChildren().addAll(leftCon, leftVariableCon, rightVariableCon);
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();

        // left
        if (leftVariable != null) {
            sb.append(leftVariable.intern());
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

        // op
        sb.append(getOperation());

        // right
        if (rightVariable != null) {
            sb.append(rightVariable.intern());
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

    public String getOperation() {
        String op = (String) cb.getValue();
        return operation.get(op);
    }

    @Override
    public Map getStatus() {
        Map<String, Object> status = new HashMap();

        //left
        if (leftVariable != null) {
            status.put("left", leftVariable.getStatus());
        } else {
            status.put("left", tf1.getText());
        }

        //op
        status.put("op", cb.getValue());

        //right
        if (rightVariable != null) {
            status.put("right", rightVariable.getStatus());
        } else {
            status.put("right", tf2.getText());
        }

        return status;
    }

    @Override
    public void setStatus(Map status) {
        for (Object key : status.keySet()) {
            if (key.equals("left")) {
                Object value = status.get(key);

                if (value instanceof String) {
                    tf1.setText((String) value); //テキスト
                } else {
                    //変数ブロック
                    Variable variable = (Variable) BlockUtil.createBlock("Variable");
                    variable.setStatus((HashMap) value);

                    setLeftVariable(variable);
                }
            } else if (key.equals("op")) {
                cb.setValue(status.get(key));
            } else if (key.equals("right")) {
                Object value = status.get(key);

                if (value instanceof String) {
                    //テキスト
                    tf2.setText((String) value);
                } else {
                    //変数ブロック
                    Variable variable = (Variable) BlockUtil.createBlock("Variable");
                    variable.setStatus((HashMap) value);

                    setRightVariable(variable);
                }
            }
        }
    }

    @Override
    public void outputBlock(Sprite sprite) {
        sprite.getScriptPane().getChildren().add(this);

        if (leftVariable != null)
            leftVariable.outputBlock(sprite);

        if (rightVariable != null)
            rightVariable.outputBlock(sprite);
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

    public void move(double dx, double dy) {
        super.move(dx, dy);
        if (leftVariable != null) {
            leftVariable.move(dx + 10, dy + 22);
            leftVariable.toFront();
        }
        if (rightVariable != null) {
            rightVariable.move(dx + 140, dy + 22);
            rightVariable.toFront();
        }
    }
}
