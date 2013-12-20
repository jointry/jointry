package jp.ac.aiit.jointry.models.blocks.statement.codeblock;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;

public class While extends CodeBlock {

    public While() {
        super();
        p.setFill(getColor());

        getChildren().addAll(p);

        // コネクタを全面にするために
        p.toBack();

        resize();
    }

    public static Color getColor() {
        return Color.web("B8B8D8");
    }

    @Override
    public void show() {
        super.show();

        for (Statement state : childBlocks) {
            if (state.prevBlock == null) {
                state.show();
                break;
            }
        }
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
