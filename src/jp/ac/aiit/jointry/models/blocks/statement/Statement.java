package jp.ac.aiit.jointry.models.blocks.statement;

import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.CodeBlock;
import jp.ac.aiit.jointry.services.broker.app.BlockDialog;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_MOVE;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_ADDLINK;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_ADDCHILD;

public abstract class Statement extends Block {

    public Statement myBlock;
    public Statement prevBlock;
    public Statement nextBlock;
    public CodeBlock parentBlock;
    public Connector topCon;
    public Connector bottomCon;
    public Connector leftCon;
    public Connector rightCon;

    public Statement() {
        super();
        this.myBlock = this;

        makeConnectors();

        addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                initializeLink();

                // 上下の接続
                if (con.getPosition() == Connector.Position.BOTTOM) {
                    Statement target = (Statement) con.getHolder();
                    if (target != nextBlock) {
                        target.addLink((Statement) myBlock);
                        move(target.getLayoutX(),
                             target.getLayoutY() + target.getHeight());
                        con.touch();

                        BlockDialog.sendMessage(M_BLOCK_ADDLINK, myBlock);
                    }
                }

                // 包含の接続
                if (con.getPosition() == Connector.Position.RIGHT) {
                    if (con.getHolder() instanceof CodeBlock) {
                        CodeBlock target = (CodeBlock) con.getHolder();
                        target.addChild(myBlock);

                        // 子供要素
                        Statement next = myBlock.nextBlock;
                        while (next != null) {
                            target.addChild(next);
                            next = next.nextBlock;
                        }

                        move(target.getLayoutX() + target.wLeft,
                             target.getLayoutY() + target.hUpper);
                        con.touch();
                        target.resize();

                        BlockDialog.sendMessage(M_BLOCK_ADDCHILD, myBlock);
                    }
                    return;
                }
            }
        });

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

                // 上下の接続（確認）
                if (con.getPosition() == Connector.Position.BOTTOM) {
                    Statement target = (Statement) con.getHolder();
                    if (target != nextBlock) {
                        move(target.getLayoutX(),
                             target.getLayoutY() + target.getHeight());
                        con.touch();
                    }
                }

                // 包含の接続
                if (con.getPosition() == Connector.Position.RIGHT) {
                    if (con.getHolder() instanceof CodeBlock) {
                        CodeBlock target = (CodeBlock) con.getHolder();
                        move(target.getLayoutX() + target.wLeft,
                             target.getLayoutY() + target.hUpper);
                        con.touch();
                    }
                    return;
                }
            }
        });
    }

    /**
     * ドラッグするブロックを先頭にする.
     */
    @Override
    public void initializeLink() {
        // 前のブロックを外す
        if (prevBlock != null) {
            prevBlock.nextBlock = null;
        }
        prevBlock = null;

        // 親のブロックを外す
        if (parentBlock != null) {
            List<Statement> blocks = this.fetchAllNextBlocks();
            blocks.add(this);
            parentBlock.childBlocks.removeAll(blocks);
            parentBlock.resize();
        }
        parentBlock = null;
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
                if (!(node instanceof Statement)) {
                    continue;
                }

                // Inside Block
                Statement target = (Statement) node;
                for (Node n : target.getChildren()) {
                    if (n instanceof Connector) {
                        Connector c = (Connector) n;
                        c.detouch();
                        Shape intersect = null;

                        // 上下の接触
                        intersect = Shape.intersect(c, myBlock.topCon);
                        if (intersect.getBoundsInLocal().getWidth() != -1) {
                            connector = c;
                            break;
                        }
                        // 包含の接触
                        intersect = Shape.intersect(c, myBlock.leftCon);
                        if (intersect.getBoundsInLocal().getWidth() != -1) {
                            connector = c;
                            break;
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
    public void move(double dx, double dy) {
        super.move(dx, dy);
        if (nextBlock != null) {
            nextBlock.move(dx, dy + getHeight());
        }
    }

    @Override
    public double fetchAllHeight() {
        double height = super.fetchAllHeight();
        if (nextBlock != null) {
            height += nextBlock.fetchAllHeight();
        }
        return height;
    }

    /**
     * 縦方向にブロックを接続する.
     *
     * @param next 次のブロック
     */
    public void addLink(Statement next) {
        Statement tmp = this.nextBlock;
        this.nextBlock = next;
        next.prevBlock = this;
        if (tmp != null) {
            next.nextBlock = tmp;
            tmp.prevBlock = next;
        }

        // 包含関係も考慮する
        Statement prevTop = fetchPrevTopBlock();
        if (prevTop.parentBlock != null) {
            prevTop.parentBlock.addChild(next);
            for (Statement p : next.fetchAllNextBlocks()) {
                prevTop.parentBlock.addChild(p);
            }
            prevTop.parentBlock.resize();
        }
    }

    /**
     * 縦方向の最上部にあるブロックを取得する.
     *
     * @return
     */
    public Statement fetchPrevTopBlock() {
        if (prevBlock == null) {
            return this;
        } else {
            return prevBlock.fetchPrevTopBlock();
        }
    }

    public List<Statement> fetchAllNextBlocks() {
        List<Statement> list = new ArrayList<>();
        Statement nb = this.nextBlock;
        while (nb != null) {
            list.add(nb);
            nb = nb.nextBlock;
        }
        return list;
    }

    public boolean isTopLevelBlock() {
        return (prevBlock == null && parentBlock == null);
    }

    @Override
    public void show() {
        getSprite().getScriptPane().getChildren().add(this);

        if (nextBlock != null) {
            nextBlock.show();
        }
    }

    protected void makeConnectors() {
        this.topCon = new Connector();
        this.bottomCon = new Connector();
        this.leftCon = new Connector();
        this.rightCon = new Connector();

        double connector_width = BASIC_WIDTH; //- 125;
        double connector_margin = (BASIC_WIDTH - connector_width) / 2.0;

        // TODO: サイズはPaneと連動させないといけない。Observerかなあ？
        topCon.setFill(Color.TRANSPARENT);
        topCon.setWidth(connector_width);
        topCon.setHeight(10);
        topCon.setHolder(this);
        topCon.setPosition(Connector.Position.TOP);
        AnchorPane.setTopAnchor(topCon, 0.0);
        AnchorPane.setLeftAnchor(topCon, connector_margin);

        bottomCon.setFill(Color.TRANSPARENT);
        bottomCon.setWidth(connector_width);
        bottomCon.setHeight(10);
        bottomCon.setHolder(this);
        bottomCon.setPosition(Connector.Position.BOTTOM);
        AnchorPane.setBottomAnchor(bottomCon, 0.0);
        AnchorPane.setLeftAnchor(bottomCon, connector_margin);

        leftCon.setFill(Color.TRANSPARENT);
        leftCon.setWidth(10);
        leftCon.setHeight(BASIC_HEIGHT);
        leftCon.setHolder(this);
        leftCon.setPosition(Connector.Position.LEFT);
        AnchorPane.setLeftAnchor(leftCon, 0.0);

        rightCon.setFill(Color.TRANSPARENT);
        rightCon.setWidth(10);
        rightCon.setHeight(BASIC_HEIGHT);
        rightCon.setHolder(this);
        rightCon.setPosition(Connector.Position.RIGHT);
        AnchorPane.setRightAnchor(rightCon, 0.0);

        getChildren().addAll(topCon, bottomCon, leftCon, rightCon);
    }

    @Override
    public void remove() {
        super.remove();
        initializeLink();
    }
}
