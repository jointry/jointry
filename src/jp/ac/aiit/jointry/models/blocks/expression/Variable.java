package jp.ac.aiit.jointry.models.blocks.expression;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import static jp.ac.aiit.jointry.models.blocks.expression.Condition.getColor;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Assign;

/**
 * 名前と値があればいい
 */
public class Variable extends Expression {

    public Block mother;
    private final Rectangle rect;
    private String name;
    private StringProperty value = new SimpleStringProperty("");
    private Connector topCon;
    private Variable me;

    public Variable() {
        super();
        me = this;

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

                // 接続
                if (con.getHolder() instanceof Condition) {
                    Condition target = (Condition) con.getHolder();
                    if (con.getPosition() == Connector.Position.INSIDE_LEFT) {
                        target.setLeftVariable(me);
                        move(target.getLayoutX() + 10, target.getLayoutY() + 22);
                    } else if (con.getPosition() == Connector.Position.INSIDE_RIGHT) {
                        target.setRightVariable(me);
                        move(target.getLayoutX() + 140, target.getLayoutY() + 22);
                    }
                } else if (con.getHolder() instanceof Assign) {
                    Assign target = (Assign) con.getHolder();
                    if (con.getPosition() == Connector.Position.INSIDE_LEFT) {
                        target.setLeftVariable(me);
                        move(target.getLayoutX() + 10, target.getLayoutY() + 15);
                    } else if (con.getPosition() == Connector.Position.INSIDE_RIGHT) {
                        target.setRightVariable(me);
                        move(target.getLayoutX() + 90, target.getLayoutY() + 15);
                    }
                }
            }
        });

        rect = new Rectangle();
        rect.setWidth(50);
        rect.setHeight(25);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStroke(Color.GRAY);
        rect.setFill(getColor());
        AnchorPane.setTopAnchor(rect, 0.0);

        topCon = new Connector();
        topCon.setFill(Color.TRANSPARENT);
        topCon.setWidth(50);
        topCon.setHeight(10);
        topCon.setHolder(this);
        AnchorPane.setTopAnchor(topCon, 0.0);
        AnchorPane.setLeftAnchor(topCon, 0.0);

        getChildren().addAll(rect, topCon);
    }

    public void initializeLink() {
        // Condition:
        if (mother instanceof Condition) {
            Condition c = (Condition) mother;
            if (c.leftVariable == this) {
                c.setLeftVariable(null);
            }
            if (c.rightVariable == this) {
                c.setRightVariable(null);
            }

        } else if (mother instanceof Assign) {
            Assign m = (Assign) mother;
            if (m.leftVariable == this) {
                m.setLeftVariable(null);
            }
            if (m.rightVariable == this) {
                m.setRightVariable(null);
            }
        }
        mother = null;
    }

    public StringProperty getValueProperty() {
        return value;
    }

    public static Color getColor() {
        return Color.TOMATO;
    }

    public Label getLabel() {
        return new Label("へんすう");
    }

    public void setName(String name) {
        this.name = name;

        Label lb = new Label();
        lb.setText(name);
        AnchorPane.setTopAnchor(lb, 5.0);
        AnchorPane.setLeftAnchor(lb, 10.0);
        getChildren().add(lb);
    }

    public void setValue(String value) {
        this.value.setValue(value);
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
                if (node == this) {
                    continue;
                }

                // Inside Block
                if (node instanceof Condition) {
                    Condition target = (Condition) node;
                    for (Node n : target.getChildren()) {
                        if (n instanceof Connector) {
                            Connector c = (Connector) n;
                            c.setFill(Color.TRANSPARENT);
                            Shape intersect = null;

                            // 包含の接触
                            intersect = Shape.intersect(c, this.topCon);
                            if (intersect.getBoundsInLocal().getWidth() != -1) {
                                connector = c;
                                break;
                            }
                        }
                    }
                } else if (node instanceof Assign) {
                    Assign target = (Assign) node;
                    for (Node n : target.getChildren()) {
                        if (n instanceof Connector) {
                            Connector c = (Connector) n;
                            if (c.getPosition() == Connector.Position.INSIDE_LEFT
                                    || c.getPosition() == Connector.Position.INSIDE_RIGHT) {
                                c.setFill(Color.TRANSPARENT);
                                Shape intersect = null;
                                // 包含の接触
                                intersect = Shape.intersect(c, this.topCon);
                                if (intersect.getBoundsInLocal().getWidth() != -1) {
                                    connector = c;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (connector != null) {
            setConnector(connector);
        }
        return connector;
    }

    public String intern() {
        // TODO: this.value.setValue(null);
        return name;
    }
}
