package jp.ac.aiit.jointry.models.blocks;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
    protected CodeBlock parentBlock;
    protected Connector topCon;
    protected Connector bottomCon;
    protected Connector leftCon;
    protected Connector rightCon;
    protected Connector con;
    protected Set<Block> childBlocks;

    public Block() {
        myBlock = this;
        setLayoutX(0);
        setLayoutY(0);

        this.topCon = new Connector();
        this.bottomCon = new Connector();
        this.leftCon = new Connector();
        this.rightCon = new Connector();
        this.childBlocks = new LinkedHashSet<>();

        // TODO: サイズはPaneと連動させないといけない。Observerかなあ？
        topCon.setFill(Color.TRANSPARENT);
        topCon.setWidth(250);
        topCon.setHeight(10);
        topCon.setHolder(myBlock);
        topCon.setPosition(Connector.Position.TOP);
        AnchorPane.setTopAnchor(topCon, 0.0);

        bottomCon.setFill(Color.TRANSPARENT);
        bottomCon.setWidth(250);
        bottomCon.setHeight(10);
        bottomCon.setHolder(myBlock);
        bottomCon.setPosition(Connector.Position.BOTTOM);
        AnchorPane.setBottomAnchor(bottomCon, 0.0);

        leftCon.setFill(Color.TRANSPARENT);
        leftCon.setWidth(10);
        leftCon.setHeight(50);
        leftCon.setHolder(myBlock);
        leftCon.setPosition(Connector.Position.LEFT);
        AnchorPane.setLeftAnchor(leftCon, 0.0);

        rightCon.setFill(Color.TRANSPARENT);
        rightCon.setWidth(10);
        rightCon.setHeight(50);
        rightCon.setHolder(myBlock);
        rightCon.setPosition(Connector.Position.RIGHT);
        AnchorPane.setRightAnchor(rightCon, 0.0);

        getChildren().addAll(topCon, bottomCon, leftCon, rightCon);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                toFront();
                anchorX = getLayoutX() - mouseEvent.getSceneX();
                anchorY = getLayoutY() - mouseEvent.getSceneY();
                setCursor(Cursor.MOVE);

                System.out.println("prev:" + prevBlock);
                System.out.println("next:" + nextBlock);
                System.out.println("parent:" + parentBlock);
                System.out.println("children:" + childBlocks);
                System.out.println("----------------------");
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

                // 上下の接続
                if (con.getPosition() == Connector.Position.BOTTOM) {
                    Block target = (Block) con.getHolder();
                    if (target != nextBlock) {
                        con.setFill(Color.GOLD);
                        target.addLink(myBlock);
                        myBlock.move(target.getLayoutX(),
                                target.getLayoutY() + target.getHeight());
                        con.setFill(Color.GOLD);
                    }
                }

                // 包含の接続
                if (con.getPosition() == Connector.Position.RIGHT) {
                    if (con.getHolder() instanceof CodeBlock) {
                        CodeBlock target = (CodeBlock) con.getHolder();
                        target.addChild(myBlock);

                        // 子供要素
                        Block next = myBlock.nextBlock;
                        while (next != null) {
                            target.addChild(next);
                            next = next.nextBlock;
                        }

                        myBlock.move(
                                target.getLayoutX() + target.wLeft,
                                target.getLayoutY() + target.hUpper);
                        con.setFill(Color.GOLD);
                        target.resize();
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
                if (myBlock.con != null) {
                    myBlock.con.setFill(Color.TRANSPARENT);
                }
                myBlock.con = null;

                // refresh
                /*
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
                 if (!(node instanceof CodeBlock)) {
                 CodeBlock block = (CodeBlock) node;
                 block.resize();
                 }
                 }
                 }
                 */
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
                        con.setFill(Color.TRANSPARENT);
                        Shape intersect = null;

                        // 上下の接触
                        intersect = Shape.intersect(con, myBlock.topCon);
                        if (intersect.getBoundsInLocal().getWidth() != -1) {
                            connector = con;
                            break;
                        }
                        // 包含の接触
                        intersect = Shape.intersect(con, myBlock.leftCon);
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

        // 包含関係も考慮する
        Block prevTop = fetchPrevTopBlock();
        if (prevTop.parentBlock != null) {
            prevTop.parentBlock.addChild(next);
            for (Block b : next.fetchAllNextBlocks()) {
                prevTop.parentBlock.addChild(b);
            }
            prevTop.parentBlock.resize();
        }
    }

    public void move(double dx, double dy) {
        setLayoutX(dx);
        setLayoutY(dy);
        if (nextBlock != null) {
            nextBlock.move(dx, dy + getHeight());
        }
    }

    public double fetchAllHeight() {
        double height = getHeight();
        if (nextBlock != null) {
            height += nextBlock.fetchAllHeight();
        }
        return height;
    }

    public List<Block> fetchAllNextBlocks() {
        List<Block> list = new ArrayList<>();
        Block nb = this.nextBlock;
        while (nb != null) {
            list.add(nb);
            nb = nb.nextBlock;
        }
        return list;
    }

    public Block fetchPrevTopBlock() {
        if (prevBlock == null) {
            return this;
        } else {
            return prevBlock.fetchPrevTopBlock();
        }
    }

    /**
     * ドラッグするブロックを先頭にする.
     */
    protected void initializeLink() {
        // 前のブロックを外す
        if (prevBlock != null) {
            prevBlock.nextBlock = null;
        }
        prevBlock = null;

        // 親のブロックを外す
        if (parentBlock != null) {
            List<Block> blocks = this.fetchAllNextBlocks();
            blocks.add(this);
            parentBlock.childBlocks.removeAll(blocks);
            parentBlock.resize();
        }
        parentBlock = null;
    }

    public boolean isTopLevelBlock() {
        return (prevBlock == null && parentBlock == null);
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
