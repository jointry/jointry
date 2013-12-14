package jp.ac.aiit.jointry.services.lang.ast;

import java.util.List;
import jp.ac.aiit.jointry.services.lang.parser.Environment;

public class ContinueStmnt extends ASTList {

    public ContinueStmnt(List<ASTree> c) {
        super(c);
    }

    @Override
    public Object eval(Environment env) {
        return this;
    }
}
