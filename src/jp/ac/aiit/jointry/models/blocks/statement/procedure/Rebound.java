package jp.ac.aiit.jointry.models.blocks.statement.procedure;

import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.util.Environment;

public class Rebound extends Procedure {

    public Rebound() {
        super();
        rect.setFill(getColor());
    }

    public static Color getColor() {
        return Color.GREENYELLOW;
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("rebound 0\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }

    @Override
    public Map getStatus(Map blockMap) {
        blockMap.put("rebound", "0");
        return blockMap;
    }

    @Override
    public void setStatus(Environment env) {
        //ブロック未実装
    }

    public Label getLabel() {
        return new Label("はねかえる");
    }
}
