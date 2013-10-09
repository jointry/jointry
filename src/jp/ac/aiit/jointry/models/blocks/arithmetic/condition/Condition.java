package jp.ac.aiit.jointry.models.blocks.arithmetic.condition;

import javafx.event.EventHandler;
import javafx.scene.Node;
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
import jp.ac.aiit.jointry.models.blocks.arithmetic.Value;
import jp.ac.aiit.jointry.models.blocks.procedure.codeblock.If;

public abstract class Condition extends Block {

    protected final Rectangle rect;
    public If mother;
    public Condition myBlock;
    public Connector topCon;
    public Connector bottomCon;
    public Connector leftCon;
    public Connector rightCon;
    protected Value arg1;
    protected Value arg2;
    protected TextField tf1;
    protected TextField tf2;
    protected Label op;

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

    public final Label getLabel() {
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
            sb.append(tf1.getText());
        }
        sb.append(getOp());
        if (arg2 != null) {
            sb.append(arg2);
        } else {
            sb.append(tf2.getText());
        }
        return sb.toString();
    }

    public String getOp() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public String blockIntern() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" ");
        sb.append(arg1).append(",").append(arg2);
        return sb.toString();
    }
}
