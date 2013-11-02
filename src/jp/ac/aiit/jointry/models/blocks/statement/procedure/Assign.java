package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import static jp.ac.aiit.jointry.models.blocks.statement.procedure.Calculate.getColor;

/**
 * 左：変数（Variable）<br />
 * 右：変数 or テキストフィールド or 演算
 */
public class Assign extends Procedure {

    public Assign() {
        super();
        rect.setFill(getColor());
    }

    public static Color getColor() {
        return Color.YELLOW;
    }

    public final Label getLabel() {
        return new Label("だいにゅう");
    }

}
