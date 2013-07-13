package jp.ac.aiit.jointry.ast;

import jp.ac.aiit.jointry.parser.env.Environment;
import jp.ac.aiit.jointry.parser.JoinTryException;
import jp.ac.aiit.jointry.parser.Token;

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
