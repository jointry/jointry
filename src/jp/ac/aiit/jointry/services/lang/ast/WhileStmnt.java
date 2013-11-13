package jp.ac.aiit.jointry.services.lang.ast;

import java.util.List;
import jp.ac.aiit.jointry.services.lang.parser.Environment;

public class WhileStmnt extends ASTList {

    public WhileStmnt(List<ASTree> c) {
        super(c);
    }

    public ASTree condition() {
        return child(0);
    }

    public ASTree body() {
        return child(1);
    }

    public String toString() {
        return "(while " + condition() + " " + body() + ")";
    }

    public Object eval(Environment env) {
        Object result = 0;
        for (;;) {
            Object c = ((ASTree) condition()).eval(env);
            if (c instanceof Integer && ((Integer) c).intValue() == FALSE) {
                return 0;
            }
            result = ((ASTree) body()).eval(env);

            if (result instanceof BreakStmnt) return 0;
        }
    }
}
