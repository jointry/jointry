package jp.ac.aiit.jointry.lang.ast;

import java.util.List;
import jp.ac.aiit.jointry.lang.parser.env.Environment;
import jp.ac.aiit.jointry.lang.parser.JoinTryException;

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
            return new Integer(-((Integer) v).intValue());
        }
        throw new JoinTryException("bad type for -", this);
    }
}
