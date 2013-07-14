package jp.ac.aiit.jointry.blocks;

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
        tCon.setWidth(300);
        tCon.setHeight(10);
        tCon.setHolder(this);
        tCon.setPosition(Connector.Position.TOP);
        AnchorPane.setTopAnchor(tCon, 0.0);

        bCon.setFill(Color.TRANSPARENT);
        bCon.setWidth(300);
        bCon.setHeight(10);
        bCon.setHolder(this);
        bCon.setPosition(Connector.Position.BOTTOM);
        AnchorPane.setBottomAnchor(bCon, 0.0);

        lCon.setFill(Color.TRANSPARENT);
        lCon.setWidth(10);
        lCon.setHeight(60);
        lCon.setHolder(this);
        lCon.setPosition(Connector.Position.LEFT);
        AnchorPane.setLeftAnchor(lCon, 0.0);

        rCon.setFill(Color.TRANSPARENT);
        rCon.setWidth(10);
        rCon.setHeight(60);
        rCon.setHolder(this);
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

                // 上下の接続
                if (con.getPosition() == Connector.Position.BOTTOM) {
                    Block target = (Block) con.getHolder();
                    if (target != nextBlock) {
                        target.setLink(myBlock);
                        myBlock.move(target.getLayoutX(),
                                target.getLayoutY() + target.getHeight());
                    }
                    return;
                }

                // TODO: 包含の接続
                if (con.getPosition() == Connector.Position.RIGHT) {
                    Block target = (Block) con.getHolder();
                    if (!childBlocks.contains(target)) {
                        //target.setLink(myBlock);
                        //myBlock.move(target.getLayoutX(),
                        //        target.getLayoutY() + target.getHeight());
                    }
                    return;
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
                setCursor(Cursor.HAND);
            }
        });
    }

    protected EventHandler getLinkEvent() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                /*
                 if (con.getPosition() == Connector.Position.RIGHT) {
                 While target = (While) con.getHolder();
                 if (target != parentBlock) {
                 target.addChild(myBlock);
                 myBlock.move(target.getLayoutX() + target.getWidth(),
                 target.getLayoutY());
                 }
                 return;
                 }
                 */

                /*
                 if (target instanceof While) {
                 if (target != parentBlock) {
                 target.addChild(myBlock);
                 // TODO: move somewhere
                 myBlock.move(target.getLayoutX() + target.getWidth(),
                 target.getLayoutY());
                 }
                 return;
                 }
                 */
            }
        };
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
                        // 上下の接続
                        intersect = Shape.intersect(con, myBlock.tCon);
                        if (intersect.getBoundsInLocal().getWidth() != -1) {
                            connector = con;
                            break;
                        }

                        // 親子の接続
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

    protected void setLink(Block next) {
        this.nextBlock = next;
        next.prevBlock = this;
    }

    public void move(double dx, double dy) {
        setLayoutX(dx);
        setLayoutY(dy);

        if (nextBlock != null) {
            nextBlock.move(dx, dy + getHeight());
        }
        /*
         if (childBlocks.size() != 0) {
         for (Block b : childBlocks) {
         b.move(dx + getWidth(), dy);
         }
         }
         */
    }

    protected void initializeLink() {
        if (prevBlock != null) {
            prevBlock.nextBlock = null;
        }
        prevBlock = null;
        parentBlock = null;
        childBlocks.clear();
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
