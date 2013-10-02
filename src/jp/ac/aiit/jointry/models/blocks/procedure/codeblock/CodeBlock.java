package jp.ac.aiit.jointry.models.blocks.procedure.codeblock;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.procedure.Procedure;

public class CodeBlock extends Procedure {

    public Set<Block> childBlocks = new LinkedHashSet<>();
    public double hUpper = 30.0;
    public double hConcave = 50.0;
    public double hLower = 30.0;
    public double wLeft = 60.0;
    protected double pHeight = hUpper + hConcave + hLower;
    protected Polygon p;

    public CodeBlock() {
        super();
    }

    @Override
    public void move(double dx, double dy) {
        super.move(dx, dy);

        double prevBlockHeight = 0;
        if (!childBlocks.isEmpty()) {
            for (Block b : childBlocks) {
                b.move(dx + wLeft,
                        dy + hUpper + prevBlockHeight);
                prevBlockHeight += b.getHeight();
            }
        }
    }

    /**
     * ドラッグするブロックを先頭にする.
     */
    public void initializeLink() {
        super.initializeLink();
        // 親のブロックを外す
        if (parentBlock != null) {
            List<Procedure> blocks = this.fetchAllNextBlocks();
            blocks.add(this);
            parentBlock.childBlocks.removeAll(blocks);
            parentBlock.resize();
        }
        parentBlock = null;
    }

    public void addChild(Procedure child) {
        childBlocks.add(child);
        child.parentBlock = this;
    }

    public final void resize() {
        hConcave = 0;
        for (Block b : childBlocks) {
            hConcave += b.getHeight();
        }
        if (hConcave < 50) {
            hConcave = 50;
        }

        pHeight = hUpper + hConcave + hLower;
        setHeight(pHeight);

        Double[] polygon = new Double[]{
            0.0, 0.0,
            250.0, 0.0,
            250.0, hUpper,
            wLeft, hUpper,
            wLeft, hUpper + hConcave,
            250.0, hUpper + hConcave,
            250.0, pHeight,
            0.0, pHeight
        };
        p.getPoints().clear();
        p.getPoints().addAll(polygon);

        // 念のため
        this.toBack();
    }
}
