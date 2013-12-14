package jp.ac.aiit.jointry.models.blocks.statement.codeblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import jp.ac.aiit.jointry.models.Status;
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

    @Override
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
    public Status getStatus() {
        Status status = new Status();

        status.put("embryo", "index < 10");

        for (Statement p : childBlocks) {
            if (p.prevBlock == null) {
                List<Status> list = BlockUtil.getAllStatus(p);
                status.put("childBlocks", list);
                break;
            }
        }

        return status;
    }

    @Override
    public void setStatus(Status status) {
        bChangeEnable = false; //一時的にリスナーを無効化

        ArrayList<Map> list = (ArrayList<Map>) status.get("childBlocks");

        Block preBlock = null;
        for (Map status_info : list) {
            Block block = BlockUtil.create(status_info);
            block.setSprite(getSprite());
            Status childStatus = BlockUtil.convertMapToStatus(status_info.get(block.getClass().getSimpleName()));
            block.setStatus(childStatus);

            if (preBlock == null) {
                preBlock = block;
            } else {
                ((Statement) preBlock).addLink((Statement) block);
            }

            //ブロックの包含接続
            addChild((Statement) block);
            resize();
        }

        bChangeEnable = true;
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
}
