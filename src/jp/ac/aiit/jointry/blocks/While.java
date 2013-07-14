package jp.ac.aiit.jointry.blocks;

import java.util.LinkedHashSet;
import java.util.Set;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class While extends Block {

    private double hUpper = 40.0;
    private double hConcave = 80.0;
    private double hLower = 40.0;
    private double height = hUpper + hConcave + hLower;

    public While() {
        super();

        // Visible Block
        Polygon p = new Polygon();
        Double[] ds = resize(10.0);
        p.getPoints().addAll(ds);
        p.setFill(getColor());
        p.setOnMouseDragged(getLinkEvent());

        // Invisible Block
        AnchorPane invisibleBlock = new AnchorPane();
        invisibleBlock.setMinWidth(70);
        double blockHeight = 30;
        invisibleBlock.setMinHeight(blockHeight);
        AnchorPane.setTopAnchor(invisibleBlock, height / 2 - blockHeight / 2); //centering
        AnchorPane.setRightAnchor(invisibleBlock, 0.0);

        // add children
        getChildren().addAll(p, invisibleBlock);
    }

    public static Color getColor() {
        return Color.PURPLE;
    }

    public void addChild(Block block) {
        childBlocks.add(block);
        block.parentBlock = this;
    }

    public final Double[] resize(double innerBlockHeight) {
        if (hConcave < innerBlockHeight) {
            hConcave = innerBlockHeight;
        }
        height = hUpper + hConcave + hLower;

        Double[] polygon = new Double[]{
            0.0, 0.0,
            100.0, 0.0,
            100.0, hUpper,
            30.0, hUpper,
            30.0, hUpper + hConcave,
            100.0, hUpper + hConcave,
            100.0, height,
            0.0, height
        };
        return polygon;
    }

    public String intern() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Label getLabel() {
        return new Label("WHILE");
    }
}
