package jp.ac.aiit.jointry.models.blocks.statement.codeblock;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;

public class While extends CodeBlock {

    public While() {
        super();
        p.setFill(getColor());

        Label tl1 = new Label("もし");
        Label tl2 = new Label("ならばずっと");
        AnchorPane.setRightAnchor(tl2, 20.0);
        getChildren().addAll(p, tl1, tl2);

        // コネクタを全面にするために
        p.toBack();

        resize();
    }

    public static Color getColor() {
        return Color.web("B8B8D8");
    }

    @Override
    public Label getLabel() {
        return new Label("くりかえす");
    }

    @Override
    public String intern() {
        StringBuilder sb = new StringBuilder("while ");
        sb.append(super.intern());
        return sb.toString();
    }
}
