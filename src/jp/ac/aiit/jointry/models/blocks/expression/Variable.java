package jp.ac.aiit.jointry.models.blocks.expression;

import jp.ac.aiit.jointry.services.broker.core.DInfo;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import jp.ac.aiit.jointry.controllers.ResizePane;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Assign;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Calculate;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Speech;
import jp.ac.aiit.jointry.services.broker.app.BlockDialog;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_VALUE_POS;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_LEFT_VALUE;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_RIGHT_VALUE;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_VALUE;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_ADDVARIABLE;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_MOVE;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.D_BLOCK;

/**
 * 名前と値があればいい
 */
public class Variable extends Expression {

    public Block mother;
    private final Rectangle rect;
    private String name;
    private StringProperty value = new SimpleStringProperty("");
    private final Connector topCon;
    private Variable myBlock;

    public Variable() {
        super();
        myBlock = this;

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

                DInfo dinfo = new DInfo(D_BLOCK);

                // 接続
                if (con.getHolder() instanceof Condition) {
                    Condition target = (Condition) con.getHolder();
                    if (con.getPosition() == Connector.Position.INSIDE_LEFT) {
                        target.setLeftVariable(myBlock);
                        move(target.getLayoutX() + 10, target.getLayoutY() + 22);
                        dinfo.set(K_VALUE_POS, K_LEFT_VALUE);
                    } else if (con.getPosition() == Connector.Position.INSIDE_RIGHT) {
                        target.setRightVariable(myBlock);
                        move(target.getLayoutX() + 140, target.getLayoutY() + 22);
                        dinfo.set(K_VALUE_POS, K_RIGHT_VALUE);
                    }
                } else if (con.getHolder() instanceof Assign) {
                    Assign target = (Assign) con.getHolder();
                    if (con.getPosition() == Connector.Position.INSIDE_LEFT) {
                        target.setLeftVariable(myBlock);
                        move(target.getLayoutX() + 10, target.getLayoutY() + 10);
                        dinfo.set(K_VALUE_POS, K_LEFT_VALUE);
                    } else if (con.getPosition() == Connector.Position.INSIDE_RIGHT) {
                        target.setRightVariable(myBlock);
                        move(target.getLayoutX() + 90, target.getLayoutY() + 10);
                        dinfo.set(K_VALUE_POS, K_RIGHT_VALUE);
                    }
                } else if (con.getHolder() instanceof Calculate) {
                    Calculate target = (Calculate) con.getHolder();
                    if (con.getPosition() == Connector.Position.LEFT) {
                        target.setVariable(myBlock);
                        move(target.getLayoutX() + 10, target.getLayoutY() + 10);
                        dinfo.set(K_VALUE_POS, K_VALUE);
                    } else if (con.getPosition() == Connector.Position.INSIDE_LEFT) {
                        target.setLeftVariable(myBlock);
                        move(target.getLayoutX() + 70, target.getLayoutY() + 10);
                        dinfo.set(K_VALUE_POS, K_LEFT_VALUE);
                    }
                } else if (con.getHolder() instanceof Speech) {
                    Speech target = (Speech) con.getHolder();
                    if (con.getPosition() == Connector.Position.INSIDE_LEFT) {
                        target.setVariable(myBlock);
                        move(target.getLayoutX() + 10, target.getLayoutY() + 10);
                        dinfo.set(K_VALUE_POS, K_LEFT_VALUE);
                    }
                }

                BlockDialog.sendMessage(M_BLOCK_ADDVARIABLE, myBlock, dinfo);
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
        topCon.detouch();
        topCon.setWidth(50);
        topCon.setHeight(10);
        topCon.setHolder(this);
        AnchorPane.setTopAnchor(topCon, 0.0);
        AnchorPane.setLeftAnchor(topCon, 0.0);

        getChildren().addAll(rect, topCon);
    }

    @Override
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
        } else if (mother instanceof Calculate) {
            Calculate m = (Calculate) mother;
            if (m.variable == this) {
                m.setVariable(null);
            }
            if (m.leftVariable == this) {
                m.setLeftVariable(null);
            }
        } else if (mother instanceof Speech) {
            Speech m = (Speech) mother;
            if (m.variable == this) {
                m.setVariable(null);
            }
        }
        mother = null;
    }

    public StringProperty getValueProperty() {
        return value;
    }

    public static Color getColor() {
        return Color.web("FFA70B");
    }

    @Override
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

    public void setValue(String v) {
        this.value.setValue(v);
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
                if (node == this) {
                    continue;
                }

                // Inside Block
                if (node instanceof Condition) {
                    Condition target = (Condition) node;
                    for (Node n : target.getChildren()) {
                        if (n instanceof Connector) {
                            Connector c = (Connector) n;
                            c.detouch();
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
                                c.detouch();
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
                } else if (node instanceof Calculate) {
                    Calculate target = (Calculate) node;
                    for (Node n : target.getChildren()) {
                        if (n instanceof Connector) {
                            Connector c = (Connector) n;
                            if (c.getPosition() == Connector.Position.LEFT
                                || c.getPosition() == Connector.Position.INSIDE_LEFT) {
                                c.detouch();
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
                } else if (node instanceof Speech) {
                    Speech target = (Speech) node;
                    for (Node n : target.getChildren()) {
                        if (n instanceof Connector) {
                            Connector c = (Connector) n;
                            if (c.getPosition() == Connector.Position.INSIDE_LEFT) {
                                c.detouch();
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

    @Override
    public String intern() {
        return name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Status getStatus() {
        Status status = new Status();
        status.put("id", this.getUUID());
        status.put("name", name);

        return status;
    }

    @Override
    public void setStatus(Status status) {
        changeable = false; //一時的にリスナーを無効化

        this.setUUID((String) status.get("id"));
        setName((String) status.get("name"));

        changeable = true;
    }

    @Override
    public void show() {
        getSprite().getScriptPane().getChildren().add(this);
    }

    @Override
    public void remove() {
        super.remove();
        initializeLink();
    }

    @Override
    public boolean hasMother() {
        return (mother != null);
    }
}
