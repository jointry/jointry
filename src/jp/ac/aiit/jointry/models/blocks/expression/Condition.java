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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import jp.ac.aiit.jointry.controllers.ResizePane;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.CodeBlock;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.If;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.While;
import jp.ac.aiit.jointry.services.broker.app.BlockDialog;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_ADDEMBRYO;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_MOVE;
import jp.ac.aiit.jointry.util.BlockUtil;

public class Condition extends Expression {

    protected final Rectangle rect;
    public CodeBlock mother;
    public Condition myBlock;
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
        myBlock = this;

        operation.put("==", " == ");
        operation.put("!=", " != ");
        operation.put("<", " < ");
        operation.put(">", " >");
        operation.put(">=", " >= ");
        operation.put("<=", " <= ");

        // Use Filter (not Handler) to fire first.
        addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                initializeLink();

                // Move
                double dx = mouseEvent.getSceneX() + anchorX;
                double dy = mouseEvent.getSceneY() + anchorY;
                move(dx, dy);

                BlockDialog.sendMessage(M_BLOCK_MOVE, myBlock);

                if (getCollision() == null) {
                    return;
                }

                // 内部の接続
                CodeBlock target = (CodeBlock) con.getHolder();
                target.accept(myBlock);
                BlockDialog.sendMessage(M_BLOCK_ADDEMBRYO, myBlock);
            }
        });

        rect = new Rectangle();
        rect.setWidth(200);
        rect.setHeight(BASIC_HEIGHT + 10);
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
        AnchorPane.setLeftAnchor(cb, 66.0);
        cb.setPrefSize(70, 20);
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
        return Color.web("E4CD9E");
    }

    @Override
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
            if (!"scriptContent".equals(tab.getContent().getId())) {
                continue;
            }

            // Inside scriptPane
            ResizePane pane = (ResizePane) tab.getContent();
            Pane scriptPane = (Pane) pane.getContent();
            for (Node node : scriptPane.getChildren()) {
                if (node == myBlock) {
                    continue;
                }
                if (!(node instanceof If) && !(node instanceof While)) {
                    continue;
                }

                // Inside Block
                Block target = (Block) node;
                for (Node n : target.getChildren()) {
                    if (n instanceof Connector) {
                        Connector c = (Connector) n;
                        c.detouch();
                        Shape intersect = null;

                        // 内部の接触
                        intersect = Shape.intersect(c, myBlock.leftCon);
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
        leftCon.detouch();
        leftCon.setWidth(10);
        leftCon.setHeight(50);
        leftCon.setHolder(myBlock);
        leftCon.setPosition(Connector.Position.LEFT);
        AnchorPane.setLeftAnchor(leftCon, 0.0);

        // Variable
        this.leftVariableCon = new Connector();
        leftVariableCon.detouch();
        leftVariableCon.setWidth(50);
        leftVariableCon.setHeight(2);
        leftVariableCon.setHolder(myBlock);
        leftVariableCon.setPosition(Connector.Position.INSIDE_LEFT);
        AnchorPane.setTopAnchor(leftVariableCon, 22.0);
        AnchorPane.setLeftAnchor(leftVariableCon, 10.0);

        // Variable
        this.rightVariableCon = new Connector();
        rightVariableCon.detouch();
        rightVariableCon.setWidth(50);
        rightVariableCon.setHeight(2);
        rightVariableCon.setHolder(myBlock);
        rightVariableCon.setPosition(Connector.Position.INSIDE_RIGHT);
        AnchorPane.setTopAnchor(rightVariableCon, 22.0);
        AnchorPane.setRightAnchor(rightVariableCon, 10.0);

        getChildren().addAll(leftCon, leftVariableCon, rightVariableCon);
    }

    @Override
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
    public Status getStatus() {
        Status status = new Status();

        status.put("id", this.getUUID());

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
    public void setStatus(Status status) {
        changeable = false; //一時的にリスナーを無効化

        this.setUUID((String) status.get("id"));
        for (Object key : status.keySet()) {
            if (key.equals("left")) {
                Object value = status.get(key);

                if (value instanceof String) {
                    tf1.setText((String) value); //テキスト
                } else {
                    //変数ブロック
                    Variable variable = (Variable) BlockUtil.create("Variable");
                    variable.setSprite(getSprite());
                    variable.setStatus(BlockUtil.convertMapToStatus(value));

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
        getSprite().getScriptPane().getChildren().add(this);

        if (leftVariable != null) {
            leftVariable.show();
        }

        if (rightVariable != null) {
            rightVariable.show();
        }
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
        super.move(dx, dy);
        if (leftVariable != null) {
            leftVariable.toFront();
            leftVariable.move(dx + 10, dy + 22);
        }
        if (rightVariable != null) {
            rightVariable.toFront();
            rightVariable.move(dx + 140, dy + 22);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (mother != null) {
            mother.embryo = null;
        }
    }

    @Override
    public boolean hasMother() {
        return (mother != null);
    }
}
