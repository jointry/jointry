package jp.ac.aiit.jointry.lang.ast;

import jp.ac.aiit.jointry.lang.parser.Environment;
import jp.ac.aiit.jointry.util.JoinTryException;
import jp.ac.aiit.jointry.lang.parser.Token;

public class Name extends ASTLeaf {

    public Name(Token t) {
        super(t);
    }

    public String name() {
        return token().getText();
    }

    public Object eval(Environment env) {
        Object value = env.get(name());
        if (value != null) {
            return value;
        }
        throw new JoinTryException("undefined name: " + name(), this);
    }
}
