package jp.ac.aiit.jointry.models.blocks.procedure.codeblock;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.arithmetic.Condition;
import static jp.ac.aiit.jointry.models.blocks.procedure.codeblock.While.getColor;

public class If extends CodeBlock {

    private Block embryo;

    // TODO: dummy
    public If() {
        super();
        this.hUpper = 80.0;

        p = new Polygon();
        resize();
        p.setFill(getColor());
        p.setStroke(Color.GRAY);

        //TODO: コネクションを追加する！
        Connector connector = new Connector();
        connector.setFill(Color.RED);
        connector.setWidth(10);
        connector.setHeight(10);
        connector.setHolder(myBlock);
        connector.setPosition(Connector.Position.TOP);
        AnchorPane.setTopAnchor(connector, 0.0);
        getChildren().addAll(connector);

        Label tl1 = new Label("もし");
        Label tl2 = new Label("ならば");
        AnchorPane.setRightAnchor(tl2, 100.0);
        getChildren().addAll(p, tl1, tl2);

        // コネクタを全面にするために
        p.toBack();
    }

    public static Color getColor() {
        return Color.BURLYWOOD;
    }

    public Label getLabel() {
        return new Label("せんたく");
    }

    public void addEmbryo(Condition block) {
        this.embryo = block;
        block.mother = this;
    }
}
