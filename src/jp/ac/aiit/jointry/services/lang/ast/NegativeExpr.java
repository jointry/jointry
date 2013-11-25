package jp.ac.aiit.jointry.services.lang.ast;

import java.util.List;
import jp.ac.aiit.jointry.util.Environment;
import jp.ac.aiit.jointry.util.JoinTryException;

public class NegativeExpr extends ASTList {

    public NegativeExpr(List<ASTree> c) {
        super(c);
    }

    public ASTree operand() {
        return child(0);
    }

    public String toString() {
        return "-" + operand();
    }

    public Object eval(Environment env) {
        Object v = ((ASTree) operand()).eval(env);
        if (v instanceof Integer) {
            return Integer.valueOf(-((Integer) v).intValue());
        }
        throw new JoinTryException("bad type for -", this);
    }
}
