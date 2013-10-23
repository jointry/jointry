package jp.ac.aiit.jointry.models.blocks.procedure.codeblock;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.expression.Condition;
import jp.ac.aiit.jointry.models.blocks.procedure.Procedure;
import static jp.ac.aiit.jointry.models.blocks.procedure.codeblock.While.getColor;

public class If extends CodeBlock {

    public Condition embryo;

    // TODO: dummy
    public If() {
        super();
        this.hUpper = 80.0;

        p = new Polygon();
        resize();
        p.setFill(getColor());
        p.setStroke(Color.GRAY);

        Connector connector = new Connector();
        connector.setFill(Color.RED);
        connector.setWidth(50);
        connector.setHeight(10);
        connector.setHolder(myBlock);
        connector.setPosition(Connector.Position.CENTER);
        AnchorPane.setLeftAnchor(connector, 50.0);

        Label tl1 = new Label("もし");
        Label tl2 = new Label("ならば");
        AnchorPane.setRightAnchor(tl2, 20.0);
        getChildren().addAll(p, tl1, tl2, connector);

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

    @Override
    public void move(double dx, double dy) {
        super.move(dx, dy);

        if (embryo != null) {
            embryo.toFront();
            embryo.move(dx + 50, dy + 20);
        }
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("if");
        if (this.embryo != null) {
            sb.append(this.embryo.intern());
        }
        sb.append("{\n");
        for (Procedure p : childBlocks) {
            if (p.prevBlock == null) {
                sb.append("\t");
                sb.append(p.intern());
                sb.append("\n");
                break;
            }
        }
        sb.append("}\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }

    public String blockIntern() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" index < 10\n");

        for (Procedure p : childBlocks) {
            if (p.prevBlock == null) {
                sb.append(p.blockIntern());
                break;
            }
        }

        if (nextBlock != null) {
            sb.append(nextBlock.blockIntern());
        }

        return sb.toString();
    }
}
