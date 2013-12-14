package jp.ac.aiit.jointry.services.broker.app;

import broker.core.DInfo;
import java.util.ArrayList;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.Node;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.expression.Condition;
import jp.ac.aiit.jointry.models.blocks.expression.Variable;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.CodeBlock;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.If;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Assign;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Calculate;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Speech;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_BLOCK_STATUS;
import jp.ac.aiit.jointry.util.BlockUtil;
import jp.ac.aiit.jointry.util.JsonUtil;

public class BlockDialog extends JointryDialogBase {

    @Override
    public void onNotify(final DInfo dinfo) {
        final Sprite sprite = getTargetSprite(dinfo);
        if (sprite == null) {
            return; //該当なし
        }
        int event = getEvent(dinfo); //対象イベント取得

        //新規block作成
        switch (event) {
            case M_BLOCK_CREATE:
                vmBlockCreate(sprite, dinfo);
                break;
            case M_BLOCK_VARIABLE_CREATE:
                vmBlockVariableCreate(dinfo);
                break;
            default:
                break;
        }

        //既存blockの操作
        final Block myBlock = getTargetBlock(sprite, dinfo.get(K_BLOCK_ID));
        if (myBlock == null) {
            return; //該当なし
        }
        switch (event) {
            case M_BLOCK_REMOVE:
                vmBlockRemove(myBlock, dinfo);
                break;
            case M_BLOCK_MOVE:
                vmBlockMove(myBlock, dinfo);
                break;
            case M_BLOCK_ADDLINK:
                vmBlockAddLink(sprite, myBlock, dinfo);
                break;
            case M_BLOCK_ADDCHILD:
                vmBlockAddChild(sprite, myBlock, dinfo);
                break;
            case M_BLOCK_ADDEMBRYO:
                vmBlockAddEmbryo(sprite, myBlock, dinfo);
                break;
            case M_BLOCK_ADDVARIABLE:
                vmBlockAddVariable(sprite, myBlock, dinfo);
                break;
            case M_BLOCK_CHANGE_STATE:
                vmBlockChangeState(myBlock, dinfo);
                break;
            default:
                break;
        }
    }

    private void vmBlockCreate(final Sprite sprite, final DInfo dinfo) {
        final Block newBlock = BlockUtil.create(dinfo.get(K_BLOCK_CLASS_NAME));
        if (newBlock == null) {
            return;
        }

        newBlock.setUUID(dinfo.get(K_BLOCK_ID));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //JavaFXのthreadでしかやれない
                sprite.getScriptPane().getChildren().add(newBlock);
            }
        });
    }

    private void vmBlockVariableCreate(final DInfo dinfo) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainController.getBlocksController().addVariable(dinfo.get(K_BLOCK_LABEL_NAME));
            }
        });
    }

    private void vmBlockRemove(final Block myBlock, final DInfo dinfo) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                myBlock.remove();
            }
        });
    }

    private void vmBlockMove(final Block myBlock, final DInfo dinfo) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //JavaFXのthreadでしかやれない
                myBlock.toFront(); //クライアント状態に関わらず前面に
                myBlock.initializeLink();
                myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
            }
        });
    }

    private void vmBlockAddLink(final Sprite sprite, final Block myBlock, final DInfo dinfo) {
        final Statement prevBlock = (Statement) getTargetBlock(sprite, dinfo.get(K_PREV_BLOCK_ID));
        if (prevBlock == null) {
            return;
        }

        if (!myBlock.getUUID().equals(prevBlock.getUUID())) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    prevBlock.addLink((Statement) myBlock);
                    myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
                }
            });
        }
    }

    private void vmBlockAddChild(final Sprite sprite, final Block myBlock, final DInfo dinfo) {
        final CodeBlock parentBlock = (CodeBlock) getTargetBlock(sprite, dinfo.get(K_PARENT_BLOCK_ID));
        if (parentBlock == null) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                parentBlock.addChild((Statement) myBlock);

                Statement next = ((Statement) myBlock).nextBlock;
                while (next != null) {
                    parentBlock.addChild(next);
                    next = next.nextBlock;
                }

                parentBlock.move(parentBlock.getLayoutX(), parentBlock.getLayoutY());
                parentBlock.resize();
            }
        });
    }

    private void vmBlockAddEmbryo(final Sprite sprite, final Block myBlock, final DInfo dinfo) {
        final If mothorBlock = (If) getTargetBlock(sprite, dinfo.get(K_PARENT_BLOCK_ID));
        if (mothorBlock == null) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mothorBlock.addEmbryo((Condition) myBlock);
                myBlock.move(dinfo.getInt(K_X1), dinfo.getInt(K_Y1));
            }
        });
    }

    private void vmBlockAddVariable(final Sprite sprite, final Block myBlock, final DInfo dinfo) {
        final Block mothorBlocks = getTargetBlock(sprite, dinfo.get(K_PARENT_BLOCK_ID));
        if (mothorBlocks == null) {
            return;
        }

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

    private void vmBlockChangeState(final Block myBlock, final DInfo dinfo) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                myBlock.setStatus(JsonUtil.parseJSONString(dinfo.get(K_BLOCK_STATUS)));
            }
        });
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

            ArrayList<Map> list = new ArrayList();
            list.add(block.getStatus());
            dinfo.set(K_BLOCK_STATUS, JsonUtil.convertObjectToJsonString(list));

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
