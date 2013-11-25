package jp.ac.aiit.jointry.models.blocks.expression;

import java.util.Map;
import jp.ac.aiit.jointry.models.blocks.Block;

public abstract class Expression extends Block {
    abstract protected Map getStatus();
}
