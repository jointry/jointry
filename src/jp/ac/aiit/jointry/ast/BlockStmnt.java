package jp.ac.aiit.jointry.ast;

import java.util.List;
import jp.ac.aiit.jointry.parser.env.Environment;

public class BlockStmnt extends ASTList {

    public BlockStmnt(List<ASTree> c) {
        super(c);
    }

    public Object eval(Environment env) {
        Object result = 0;
        for (ASTree t : this) {
            if (!(t instanceof NullStmnt)) {
                result = ((ASTree) t).eval(env);
            }
        }
        return result;
    }
}
