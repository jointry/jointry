package jp.ac.aiit.jointry.models.blocks.statement.codeblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.expression.Condition;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;
import jp.ac.aiit.jointry.util.BlockUtil;

public class If extends CodeBlock {

    public Condition embryo;
    public Connector connector;

    public If() {
        super();
        this.hUpper = 80.0;

        p = new Polygon();
        resize();
        p.setFill(getColor());
        p.setStroke(Color.GRAY);

        connector = new Connector();
        connector.setFill(Color.TRANSPARENT);
        connector.setWidth(50);
        connector.setHeight(10);
        connector.setHolder(myBlock);
        connector.setPosition(Connector.Position.CENTER);
        AnchorPane.setLeftAnchor(connector, 50.0);
        AnchorPane.setTopAnchor(connector, 20.0);

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
        sb.append("if ");
        if (this.embryo != null) {
            sb.append(this.embryo.intern());
        }
        sb.append(" {\n");
        for (Statement p : childBlocks) {
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

    @Override
    public Map getStatus() {
        Map<String, Object> status = new HashMap();

        if (embryo != null) {
            status.put("embryo", embryo.getStatus());
        }

        for (Statement state : childBlocks) {
            if (state.prevBlock == null) {
                List<Map> list = BlockUtil.getAllStatus(state);
                status.put("childBlocks", list);
                break;
            }
        }

        return status;
    }

    @Override
    public void setStatus(Status status) {
        for (Object key : status.keySet()) {
            if (key.equals("embryo")) {
                //変数ブロック
                Condition emb = new Condition();
                emb.setStatus((Status) status.get(key));

                addEmbryo(emb);
            } else if (key.equals("childBlocks")) {
                //包含するブロック
                ArrayList<Map> list = (ArrayList<Map>) status.get(key);

                Block preBlock = null;

                for (Map map : list) {
                    Block block = BlockUtil.createBlock(map);
                    block.setStatus((Status) map.get(block.getClass().getSimpleName()));

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
        }
    }

    @Override
    public void outputBlock(Sprite sprite) {
        super.outputBlock(sprite);

        if (embryo != null) {
            embryo.outputBlock(sprite);
        }

        for (Statement state : childBlocks) {
            state.outputBlock(sprite);
            break;
        }
    }
}
