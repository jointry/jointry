package jp.ac.aiit.jointry.services.broker.app;

import broker.core.DInfo;
import javafx.scene.Node;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.expression.Condition;
import jp.ac.aiit.jointry.models.blocks.expression.Variable;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.CodeBlock;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Assign;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Calculate;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Speech;
import jp.ac.aiit.jointry.util.BlockUtil;
import jp.ac.aiit.jointry.util.JsonUtil;

public class BlockDialog extends JointryDialogBase {

    @Override
    public void onAnswer(int event, DInfo dinfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onQuery(int event, DInfo dinfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onNotify(int event, DInfo dinfo) {
        final Sprite sprite = getTargetSprite(dinfo);
        if (sprite == null) {
            return; //該当なし
        }

        //新規block作成
        switch (event) {
            case M_BLOCK_CREATE:
                Block newBlock = BlockUtil.create(dinfo.get(K_BLOCK_CLASS_NAME));
                if (newBlock instanceof Statement) {
                    mBlockCreate(sprite, dinfo); //statement以外はstatementにくっついたとき
                }
                return;
            case M_BLOCK_VARIABLE_CREATE:
                mBlockVariableCreate(dinfo);
                return;
        }

        //応用blockの操作
        Block myBlock = getTargetBlock(sprite, dinfo.get(K_BLOCK_ID));
        if (myBlock == null) {

            switch (event) {
                case M_BLOCK_ADDEMBRYO:
                    myBlock = mBlockCreate(sprite, dinfo);
                    mBlockAddEmbryo(sprite, myBlock, dinfo);
                    break;
                case M_BLOCK_ADDVARIABLE:
                    myBlock = mBlockCreate(sprite, dinfo);
                    mBlockAddVariable(sprite, myBlock, dinfo);
                    break;
            }

            return;
        }

        //基本blockの操作
        switch (event) {
            case M_BLOCK_REMOVE:
                mBlockRemove(myBlock, dinfo);
                break;
            case M_BLOCK_MOVE:
                mBlockMove(myBlock, dinfo);
                break;
            case M_BLOCK_ADDLINK:
                mBlockAddLink(sprite, myBlock, dinfo);
                break;
            case M_BLOCK_ADDCHILD:
                mBlockAddChild(sprite, myBlock, dinfo);
                break;
            case M_BLOCK_CHANGE_STATE:
                mBlockChangeState(myBlock, dinfo);
                break;
            default:
                break;
        }
    }

    private Block mBlockCreate(Sprite sprite, DInfo dinfo) {
        Block newBlock = BlockUtil.create(dinfo.get(K_BLOCK_CLASS_NAME));

        newBlock.setUUID(dinfo.get(K_BLOCK_ID));
        sprite.getScriptPane().getChildren().add(newBlock);

        return newBlock;
    }

    private void mBlockVariableCreate(final DInfo dinfo) {
        mainController.getBlocksController().addVariable(dinfo.get(K_BLOCK_LABEL_NAME));
    }

    private void mBlockRemove(Block myBlock, DInfo dinfo) {
        myBlock.remove();
    }

    private void mBlockMove(Block myBlock, DInfo dinfo) {
        myBlock.toFront(); //クライアント状態に関わらず前面に
        myBlock.initializeLink();
        myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
    }

    private void mBlockAddLink(final Sprite sprite, final Block myBlock, final DInfo dinfo) {
        Statement prevBlock = (Statement) getTargetBlock(sprite, dinfo.get(K_PREV_BLOCK_ID));

        if (!myBlock.getUUID().equals(prevBlock.getUUID())) {
            prevBlock.addLink((Statement) myBlock);
            myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
        }
    }

    private void mBlockAddChild(Sprite sprite, Block myBlock, DInfo dinfo) {
        CodeBlock parentBlock = (CodeBlock) getTargetBlock(sprite, dinfo.get(K_PARENT_BLOCK_ID));

        parentBlock.addChild((Statement) myBlock);

        Statement next = ((Statement) myBlock).nextBlock;
        while (next != null) {
            parentBlock.addChild(next);
            next = next.nextBlock;
        }

        parentBlock.move(parentBlock.getLayoutX(), parentBlock.getLayoutY());
        parentBlock.resize();
    }

    private void mBlockAddEmbryo(Sprite sprite, Block myBlock, DInfo dinfo) {
        CodeBlock mothorBlock = (CodeBlock) getTargetBlock(sprite, dinfo.get(K_PARENT_BLOCK_ID));

        mothorBlock.addEmbryo((Condition) myBlock);
        myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
    }

    private void mBlockAddVariable(Sprite sprite, Block myBlock, DInfo dinfo) {
        Block mothorBlocks = getTargetBlock(sprite, dinfo.get(K_PARENT_BLOCK_ID));

        if (mothorBlocks instanceof Condition) {
            switch (dinfo.get(K_VALUE_POS)) {
                case K_LEFT_VALUE:
                    ((Condition) mothorBlocks).setLeftVariable((Variable) myBlock);
                    break;
                case K_RIGHT_VALUE:
                    ((Condition) mothorBlocks).setRightVariable((Variable) myBlock);
                    break;
            }

            myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
        } else if (mothorBlocks instanceof Assign) {
            switch (dinfo.get(K_VALUE_POS)) {
                case K_LEFT_VALUE:
                    ((Assign) mothorBlocks).setLeftVariable((Variable) myBlock);
                    myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
                    break;
                case K_RIGHT_VALUE:
                    ((Assign) mothorBlocks).setRightVariable((Variable) myBlock);
                    break;
            }

            myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
        } else if (mothorBlocks instanceof Calculate) {
            switch (dinfo.get(K_VALUE_POS)) {
                case K_VALUE:
                    ((Calculate) mothorBlocks).setVariable((Variable) myBlock);
                    break;
                case K_LEFT_VALUE:
                    ((Calculate) mothorBlocks).setLeftVariable((Variable) myBlock);
                    break;
            }

            myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
        } else if (mothorBlocks instanceof Speech) {
            if (dinfo.get(K_VALUE_POS).equals(K_LEFT_VALUE)) {
                ((Speech) mothorBlocks).setVariable((Variable) myBlock);
            }

            myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
        }
    }

    private void mBlockChangeState(Block myBlock, DInfo dinfo) {
        myBlock.setStatus(JsonUtil.parseJSONString(dinfo.get(K_BLOCK_STATUS)));
    }

    public static void sendMessage(int event, String value) {
        if (mainController.getAgent() != null) {
            DInfo dinfo = new DInfo(D_BLOCK);

            dinfo.set(K_METHOD, event);
            dinfo.set(K_SPRITE_NAME, mainController.getFrontStageController().getCurrentSprite().getName());
            dinfo.set(K_BLOCK_LABEL_NAME, value);

            mainController.getAgent().sendNotify(dinfo);
        }
    }

    public static void sendMessage(int event, Block block) {
        BlockDialog.sendMessage(event, block, null);
    }

    public static void sendMessage(int event, Block block, DInfo dinfo) {
        if (mainController.getAgent() != null) {
            if (dinfo == null) {
                dinfo = new DInfo(D_BLOCK);
            }

            dinfo.set(K_METHOD, event);
            dinfo.set(K_SPRITE_NAME, mainController.getFrontStageController().getCurrentSprite().getName());
            dinfo.set(K_BLOCK_CLASS_NAME, block.getClass().getSimpleName());
            dinfo.set(K_BLOCK_ID, block.getUUID());
            dinfo.set(K_X1, (int) block.getLayoutX());
            dinfo.set(K_Y1, (int) block.getLayoutY());

            if (block instanceof Statement) {
                setStatementBlock(dinfo, (Statement) block);
            } else if (block instanceof Condition) {
                setConditionBlock(dinfo, (Condition) block);
            } else if (block instanceof Variable) {
                setVariableBlock(dinfo, (Variable) block);
            }

            dinfo.set(K_BLOCK_STATUS, JsonUtil.convertObjectToJsonString(block.getStatus()));

            mainController.getAgent().sendNotify(dinfo);
        }
    }

    private static void setStatementBlock(DInfo dinfo, Statement statement) {
        if (statement.parentBlock != null) {
            dinfo.set(K_PARENT_BLOCK_ID, statement.parentBlock.getUUID());
        }

        if (statement.prevBlock != null) {
            dinfo.set(K_PREV_BLOCK_ID, statement.prevBlock.getUUID());
        }
    }

    private static void setConditionBlock(DInfo dinfo, Condition condition) {
        if (condition.mother != null) {
            dinfo.set(K_PARENT_BLOCK_ID, condition.mother.getUUID());
        }
    }

    private static void setVariableBlock(DInfo dinfo, Variable variable) {
        if (variable.mother != null) {
            dinfo.set(K_PARENT_BLOCK_ID, variable.mother.getUUID());
        }

        dinfo.set(K_BLOCK_LABEL_NAME, variable.getName());
    }

    private Block getTargetBlock(Sprite sprite, String id) {
        if (mainController == null) {
            return null;
        }

        for (Node node : sprite.getScriptPane().getChildrenUnmodifiable()) {
            Block block = (Block) node;

            if (block.getUUID().equals(id)) {
                return block;
            }
        }
        return null;
    }
}
