package jp.ac.aiit.jointry.models.blocks.statement.codeblock;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class If extends CodeBlock {

    public If() {
        super();
        p.setFill(getColor());

        Label tl1 = new Label("もし");
        Label tl2 = new Label("ならば");
        AnchorPane.setRightAnchor(tl2, 20.0);
        getChildren().addAll(p, tl1, tl2);

        // コネクタを全面にするために
        p.toBack();

        resize();
    }

    public static Color getColor() {
        return Color.web("F0B0B8");
    }

    @Override
    public Label getLabel() {
        return new Label("せんたく");
    }

    @Override
    public String intern() {
        StringBuilder sb = new StringBuilder("if ");
        sb.append(super.intern());
        return sb.toString();
    }

}
