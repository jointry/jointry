package jp.ac.aiit.jointry.models.blocks.statement.codeblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;
import jp.ac.aiit.jointry.util.BlockUtil;
import jp.ac.aiit.jointry.util.Environment;

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
    public Map blockIntern(Map blockMap) {
        blockMap.put("embryo", "index < 10");

        ArrayList<Map> list = new ArrayList();
        for (Statement p : childBlocks) {
            if (p.prevBlock == null) {
                p.blockIntern(list);
                break;
            }
        }
        blockMap.put("childBlocks", list);

        return blockMap;
    }

    @Override
    public void setParams(Environment env) {
        Map paramMap = env.getValues();

        ArrayList<Map> list = (ArrayList<Map>) paramMap.get("childBlocks");

        for (Map map : list) {
            Block block = BlockUtil.createBlock(map);
            env.setValues((HashMap) map.get(block.getClass().getSimpleName()));
            block.setParams(env);

            env.getSprite().getScriptPane().getChildren().add(block); //ブロックの表示

            //ブロックの包含接続
            addChild((Statement) block);
            resize();
        }
    }

    public Label getLabel() {
        return new Label("くりかえす");
    }
}
