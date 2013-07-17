package jp.ac.aiit.jointry.blocks;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class While extends Block {

    protected double hUpper = 30.0;
    protected double hConcave = 50.0;
    protected double hLower = 30.0;
    protected double wLeft = 30.0;
    protected double pHeight = hUpper + hConcave + hLower;
    private Polygon p;

    public While() {
        super();

        p = new Polygon();
        resize();
        p.setFill(getColor());
        p.setStroke(Color.GRAY);

        getChildren().addAll(p);
    }

    public static Color getColor() {
        return Color.LIGHTGREEN;
    }

    @Override
    public void move(double dx, double dy) {
        super.move(dx, dy);

        if (childBlocks.isEmpty()) {
            return;
        }

        int i = 0;
        for (Block b : childBlocks) {
            b.move(dx + wLeft,
                    dy + hUpper + (b.getHeight() * i));
            i++;
        }
    }

    @Override
    protected void initializeLink() {
        super.initializeLink();

        if (parentBlock != null) {
            parentBlock.childBlocks.remove(this);
        }
        parentBlock = null;
    }

    public void addChild(Block block) {
        childBlocks.add(block);
        block.parentBlock = this;
    }

    public final void resize() {
        double innerBlockHeight = 0.0;
        for (Block b : childBlocks) {
            innerBlockHeight += b.getAllHeight();
        }
        if (hConcave < innerBlockHeight) {
            hConcave = innerBlockHeight;
        }

        pHeight = hUpper + hConcave + hLower;
        setHeight(pHeight);

        Double[] polygon = new Double[]{
            0.0, 0.0,
            100.0, 0.0,
            100.0, hUpper,
            wLeft, hUpper,
            wLeft, hUpper + hConcave,
            100.0, hUpper + hConcave,
            100.0, pHeight,
            0.0, pHeight
        };
        p.getPoints().clear();
        p.getPoints().addAll(polygon);
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("index = 0\n");
        sb.append("while index < 10 {\n");
        for (Block b : childBlocks) {
            sb.append(b.intern());
            sb.append("\n");
        }
        sb.append("index = index + 1\n");
        sb.append("}\n");
        sb.append("index = 0\n");
        return sb.toString();
    }

    public Label getLabel() {
        return new Label("くりかえす");
    }
}
