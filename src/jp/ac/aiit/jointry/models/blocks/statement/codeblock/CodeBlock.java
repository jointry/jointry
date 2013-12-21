package jp.ac.aiit.jointry.models.blocks.statement.codeblock;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.expression.Condition;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;
import jp.ac.aiit.jointry.util.BlockUtil;

public abstract class CodeBlock extends Statement {

    public Set<Statement> childBlocks = new LinkedHashSet<>();
    public double hUpper = 80.0;
    public double hConcave = BASIC_HEIGHT;
    public double hLower = 30.0;
    public double wLeft = 80.0;
    protected double pHeight = hUpper + hConcave + hLower;
    protected Polygon p;
    public Connector connector;
    public Condition embryo;

    public CodeBlock() {
        super();

        p = new Polygon();
        p.setStroke(Color.GRAY);

        // override
        rightCon.detouch();
        rightCon.setWidth(BASIC_WIDTH - wLeft);
        rightCon.setHeight(10);
        AnchorPane.setTopAnchor(rightCon, hUpper);
        AnchorPane.setLeftAnchor(rightCon, wLeft);

        connector = new Connector();
        connector.detouch();
        connector.setWidth(200);
        connector.setHeight(50);
        connector.setHolder(myBlock);
        connector.setPosition(Connector.Position.CENTER);
        AnchorPane.setLeftAnchor(connector, 40.0);
        AnchorPane.setTopAnchor(connector, 20.0);

        Rectangle dip = new Rectangle();
        dip.setWidth(198);
        dip.setHeight(48);
        dip.setFill(Color.web("FAFAFA"));
        AnchorPane.setLeftAnchor(dip, 41.0);
        AnchorPane.setTopAnchor(dip, 21.0);
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setOffsetX(2);
        innerShadow.setOffsetY(2);
        innerShadow.setColor(Color.web("0x3b596d"));
        dip.setEffect(innerShadow);

        getChildren().addAll(connector, dip);
    }

    public void addEmbryo(Condition block) {
        this.embryo = block;
        block.mother = this;
    }

    public void accept(Condition c) {
        addEmbryo(c);
        c.move(getLayoutX() + 40, getLayoutY() + 20);
    }

    @Override
    public void move(double dx, double dy) {
        super.move(dx, dy);

        if (embryo != null) {
            embryo.toFront();
            embryo.move(dx + 40, dy + 20);
        }

        double prevBlockHeight = 0;
        if (!childBlocks.isEmpty()) {
            for (Block b : childBlocks) {
                b.move(dx + wLeft,
                       dy + hUpper + prevBlockHeight);
                b.toFront();
                prevBlockHeight += b.getHeight();
            }
        }
    }

    /**
     * ドラッグするブロックを先頭にする.
     */
    @Override
    public void initializeLink() {
        super.initializeLink();
        // 親のブロックを外す
        if (parentBlock != null) {
            List<Statement> blocks = this.fetchAllNextBlocks();
            blocks.add(this);
            parentBlock.childBlocks.removeAll(blocks);
            parentBlock.resize();
        }
        parentBlock = null;
    }

    public void addChild(Statement child) {
        childBlocks.add(child);
        child.parentBlock = this;
    }

    public final void resize() {
        hConcave = 0;
        for (Block b : childBlocks) {
            hConcave += b.getHeight();
        }
        if (hConcave < BASIC_HEIGHT) {
            hConcave = BASIC_HEIGHT;
        }

        pHeight = hUpper + hConcave + hLower;
        setHeight(pHeight);

        Double[] polygon = new Double[]{
            0.0, 0.0,
            BASIC_WIDTH, 0.0,
            BASIC_WIDTH, hUpper,
            wLeft, hUpper,
            wLeft, hUpper + hConcave,
            BASIC_WIDTH, hUpper + hConcave,
            BASIC_WIDTH, pHeight,
            0.0, pHeight
        };
        p.getPoints().clear();
        p.getPoints().addAll(polygon);

        // 念のため
        this.toBack();
    }

    @Override
    public Status getStatus() {
        Status status = new Status();

        status.put("id", this.getUUID());
        if (embryo != null) {
            status.put("embryo", embryo.getStatus());
        }

        for (Statement state : childBlocks) {
            if (state.prevBlock == null) {
                List<Status> list = BlockUtil.getAllStatus(state);
                status.put("childBlocks", list);
                break;
            }
        }

        return status;
    }

    @Override
    public void setStatus(Status status) {
        bChangeEnable = false; //一時的にリスナーを無効化

        this.setUUID((String) status.get("id"));
        for (Object key : status.keySet()) {
            if (key.equals("embryo")) {
                // 変数ブロック
                Condition emb = new Condition();
                emb.setSprite(getSprite());
                emb.setStatus(BlockUtil.convertMapToStatus(status.get(key)));

                addEmbryo(emb);
            } else if (key.equals("childBlocks")) {
                //包含するブロック
                ArrayList<Map> list = (ArrayList<Map>) status.get(key);

                Block preBlock = null;

                for (Map status_info : list) {
                    Block block = BlockUtil.create(status_info);
                    block.setSprite(getSprite());
                    Status childStatus = BlockUtil.convertMapToStatus(status_info.get(block.getClass().getSimpleName()));
                    block.setStatus(childStatus);

                    if (preBlock == null) {
                        preBlock = block;
                    } else {
                        ((Statement) preBlock).addLink((Statement) block);
                    }

                    //ブロックの包含接続
                    addChild((Statement) block);
                    resize();
                }
            }
        }

        bChangeEnable = true;
    }

    @Override
    public void show() {
        super.show();

        if (embryo != null) {
            embryo.show();
        }

        for (Statement state : childBlocks) {
            if (state.prevBlock == null) {
                state.show();
                break;
            }
        }
    }

    @Override
    public String intern() {
        StringBuilder sb = new StringBuilder();
        if (this.embryo != null) {
            sb.append(this.embryo.intern());
        }
        sb.append(" {\n");
        for (Statement p : childBlocks) {
            if (p.prevBlock == null) {
                sb.append(p.intern());
                break;
            }
        }
        sb.append("}\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }
}
