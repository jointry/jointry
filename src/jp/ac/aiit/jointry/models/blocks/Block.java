package jp.ac.aiit.jointry.models.blocks;

import java.util.LinkedHashSet;
import java.util.Set;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public abstract class Block extends AnchorPane {

    protected double anchorX;
    protected double anchorY;
    protected Block myBlock;
    protected Block prevBlock;
    protected Block nextBlock;
    protected Block parentBlock;
    protected Set<Block> childBlocks;
    protected Connector tCon;
    protected Connector bCon;
    protected Connector lCon;
    protected Connector rCon;
    protected While targetWhile;
    protected Connector con;

    public Block() {
        myBlock = this;
        setLayoutX(0);
        setLayoutY(0);
        this.childBlocks = new LinkedHashSet<>();

        this.tCon = new Connector();
        this.bCon = new Connector();
        this.lCon = new Connector();
        this.rCon = new Connector();

        // TODO: サイズはPaneと連動させないといけない。Observerかなあ？
        tCon.setFill(Color.TRANSPARENT);
        tCon.setWidth(250);
        tCon.setHeight(10);
        tCon.setHolder(myBlock);
        tCon.setPosition(Connector.Position.TOP);
        AnchorPane.setTopAnchor(tCon, 0.0);

        bCon.setFill(Color.TRANSPARENT);
        bCon.setWidth(250);
        bCon.setHeight(10);
        bCon.setHolder(myBlock);
        bCon.setPosition(Connector.Position.BOTTOM);
        AnchorPane.setBottomAnchor(bCon, 0.0);

        lCon.setFill(Color.TRANSPARENT);
        lCon.setWidth(10);
        lCon.setHeight(50);
        lCon.setHolder(myBlock);
        lCon.setPosition(Connector.Position.LEFT);
        AnchorPane.setLeftAnchor(lCon, 0.0);

        rCon.setFill(Color.TRANSPARENT);
        rCon.setWidth(10);
        rCon.setHeight(50);
        rCon.setHolder(myBlock);
        rCon.setPosition(Connector.Position.RIGHT);
        AnchorPane.setRightAnchor(rCon, 0.0);

        getChildren().addAll(tCon, bCon, lCon, rCon);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                toFront();
                anchorX = getLayoutX() - mouseEvent.getSceneX();
                anchorY = getLayoutY() - mouseEvent.getSceneY();
                setCursor(Cursor.MOVE);
            }
        });

        // Use Filter (not Handler) to fire first.
        addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // Initialize
                myBlock.initializeLink();

                // Move
                double dx = mouseEvent.getSceneX() + anchorX;
                double dy = mouseEvent.getSceneY() + anchorY;
                myBlock.move(dx, dy);

                Connector con = getCollision();
                if (con == null) {
                    return;
                }
                myBlock.con = con;

                // 包含の接続
                if (con.getPosition() == Connector.Position.RIGHT) {
                    if (con.getHolder() instanceof While) {
                        con.setFill(Color.GOLD);
                        While target = (While) con.getHolder();
                        target.addChild(myBlock);
                        int power = target.childBlocks.size() - 1;
                        myBlock.move(
                                target.getLayoutX() + target.wLeft,
                                target.getLayoutY() + target.hUpper
                                + myBlock.getHeight() * power);
                        targetWhile = target;
                    }
                    return;
                }

                // 上下の接続
                if (con.getPosition() == Connector.Position.BOTTOM) {
                    Block target = (Block) con.getHolder();
                    if (target != nextBlock) {
                        con.setFill(Color.GOLD);
                        target.addLink(myBlock);
                        myBlock.move(target.getLayoutX(),
                                target.getLayoutY() + target.getHeight());
                    }
                }
            }
        });

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.HAND);
            }
        });

        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (myBlock.targetWhile != null) {
                    myBlock.targetWhile.resize();
                    myBlock.targetWhile = null;
                }
                setCursor(Cursor.HAND);

                if (myBlock.con != null) {
                    myBlock.con.setFill(Color.TRANSPARENT);
                }
                myBlock.con = null;
            }
        });
    }

    protected Connector getCollision() {
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
                if (!(node instanceof Block)) {
                    continue;
                }

                // Inside Block
                Block target = (Block) node;
                for (Node n : target.getChildren()) {
                    if (n instanceof Connector) {
                        Connector con = (Connector) n;
                        Shape intersect = null;
                        // 上下の接触
                        intersect = Shape.intersect(con, myBlock.tCon);
                        if (intersect.getBoundsInLocal().getWidth() != -1) {
                            connector = con;
                            break;
                        }

                        // 包含の接触
                        intersect = Shape.intersect(con, myBlock.lCon);
                        if (intersect.getBoundsInLocal().getWidth() != -1) {
                            connector = con;
                            break;
                        }
                    }
                }
            }
        }
        return connector;
    }

    protected void addLink(Block next) {
        Block tmp = this.nextBlock;
        this.nextBlock = next;
        next.prevBlock = this;
        if (tmp != null) {
            next.nextBlock = tmp;
            tmp.prevBlock = next;
        }
    }

    public void move(double dx, double dy) {
        setLayoutX(dx);
        setLayoutY(dy);
        if (nextBlock != null) {
            nextBlock.move(dx, dy + getHeight());
        }
    }

    public double getAllHeight() {
        double height = getHeight();
        if (nextBlock != null) {
            height += nextBlock.getAllHeight();
        }
        return height;
    }

    protected void initializeLink() {
        if (prevBlock != null) {
            prevBlock.nextBlock = null;
        }
        prevBlock = null;
    }

    public boolean existPrevBlock() {
        return prevBlock != null;
    }

    public String intern() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Label getLabel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addChild(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
