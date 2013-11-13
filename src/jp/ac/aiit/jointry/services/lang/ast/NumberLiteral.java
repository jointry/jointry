package jp.ac.aiit.jointry.services.lang.ast;

import jp.ac.aiit.jointry.services.lang.parser.Environment;
import jp.ac.aiit.jointry.services.lang.parser.Token;

public class NumberLiteral extends ASTLeaf {

    public NumberLiteral(Token t) {
        super(t);
    }

    public int value() {
        return token().getNumber();
    }

    public Object eval(Environment e) {
        return value();
    }
}
