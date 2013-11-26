package jp.ac.aiit.jointry.models.blocks.statement.codeblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;
import jp.ac.aiit.jointry.util.BlockUtil;

public class While extends CodeBlock {

    public While() {
        super();

        p = new Polygon();
        resize();
        p.setFill(getColor());
        p.setStroke(Color.GRAY);

        getChildren().addAll(p);

        // コネクタを全面にするために
        p.toBack();
    }

    public static Color getColor() {
        return Color.LIGHTGREEN;
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("index = 0\n");
        sb.append("while index < 10 {\n");
        for (Statement p : childBlocks) {
            if (p.prevBlock == null) {
                sb.append(p.intern());
                sb.append("\n");
                break;
            }
        }
        sb.append("index = index + 1; \n");
        sb.append("}\n");
        sb.append("index = 0; \n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }

    @Override
    public Map getStatus() {
        Map<String, Object> status = new HashMap();

        status.put("embryo", "index < 10");

        for (Statement p : childBlocks) {
            if (p.prevBlock == null) {
                List<Map> list = BlockUtil.getAllStatus(p);
                status.put("childBlocks", list);
                break;
            }
        }

        return status;
    }

    @Override
    public void setStatus(Map status) {
        ArrayList<Map> list = (ArrayList<Map>) status.get("childBlocks");

        Block preBlock = null;
        for (Map map : list) {
            Block block = BlockUtil.createBlock(map);
            block.setStatus((HashMap) map.get(block.getClass().getSimpleName()));

            if (preBlock == null) {
                preBlock = block;
            } else {
                ((Statement) preBlock).addLink((Statement) block);
            }

            //ブロックの包含接続
            addChild((Statement) block);
            resize();
        }
    }

    @Override
    public void outputBlock(Sprite sprite) {
        super.outputBlock(sprite);

        for (Statement state : childBlocks) {
            state.outputBlock(sprite);
            break;
        }
    }

    public Label getLabel() {
        return new Label("くりかえす");
    }
}
