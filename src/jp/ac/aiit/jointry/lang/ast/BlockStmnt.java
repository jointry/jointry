package jp.ac.aiit.jointry.lang.ast;

import java.util.List;
import jp.ac.aiit.jointry.lang.parser.Environment;

public class BlockStmnt extends ASTList {

    public BlockStmnt(List<ASTree> c) {
        super(c);
    }

    public Object eval(Environment env) {
        Object result = 0;
        for (ASTree t : this) {
            if (!(t instanceof NullStmnt)) {
                result = ((ASTree) t).eval(env);
                
                if(result instanceof BreakStmnt || result instanceof ContinueStmnt)
                    break;
            }
        }
        return result;
    }
}
