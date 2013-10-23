package jp.ac.aiit.jointry.models.blocks.expression;

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
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.procedure.codeblock.If;

public class Condition extends Expression {

    protected final Rectangle rect;
    public If mother;
    public Condition myBlock;
    public Connector topCon;
    public Connector bottomCon;
    public Connector leftCon;
    public Connector rightCon;
    protected Variable arg1;
    protected Variable arg2;
    protected TextField tf1;
    protected TextField tf2;
    protected ComboBox cb;

    public Condition() {
        super();
        this.myBlock = this;

        makeConnectors();

        // Use Filter (not Handler) to fire first.
        addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                initializeLink();

                // Move
                double dx = mouseEvent.getSceneX() + anchorX;
                double dy = mouseEvent.getSceneY() + anchorY;
                move(dx, dy);

                if (getCollision() == null) {
                    return;
                }

                // 内部の接続
                If target = (If) con.getHolder();
                target.addEmbryo(myBlock);
                move(target.getLayoutX() + 50, target.getLayoutY() + 10); // TODO
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

        Label lb = getLabel();
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

        cb = new ComboBox();
        cb.setItems(FXCollections.observableArrayList(
                "おなじ", "おおきい", "いじょう", "いか", "ちいさい"));

        AnchorPane.setTopAnchor(cb, 22.0);
        AnchorPane.setLeftAnchor(cb, 68.0);
        cb.setPrefSize(60, 20);
        getChildren().addAll(cb);

        // コネクタを全面に出すために
        rect.toBack();
    }

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
                if (node == myBlock) {
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
        leftCon.setFill(Color.TRANSPARENT);
        leftCon.setWidth(10);
        leftCon.setHeight(50);
        leftCon.setHolder(myBlock);
        leftCon.setPosition(Connector.Position.LEFT);
        AnchorPane.setLeftAnchor(leftCon, 0.0);
        getChildren().addAll(leftCon);
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
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
        sb.append(getOp());
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

    public String getOp() {
        String op = (String) cb.getValue();
        if (op.equals("おなじ")) {
            return " == ";
        } else if (op.equals("おおきい")) {
            return " > ";
        } else if (op.equals("いじょう")) {
            return " >= ";
        } else if (op.equals("いか")) {
            return " <= ";
        } else if (op.equals("ちいさい")) {
            return " < ";
        } else {
            return "";
        }
    }

    public String blockIntern() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" ");
        sb.append(arg1).append(",").append(arg2);
        return sb.toString();
    }
}
