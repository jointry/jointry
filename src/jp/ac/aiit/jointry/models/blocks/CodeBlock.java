package jp.ac.aiit.jointry.models.blocks;

import java.util.LinkedHashSet;
import java.util.Set;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;

public class CodeBlock extends Block {

    protected double hUpper = 30.0;
    protected double hConcave = 50.0;
    protected double hLower = 30.0;
    protected double wLeft = 30.0;
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

    public void addChild(Block child) {
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
