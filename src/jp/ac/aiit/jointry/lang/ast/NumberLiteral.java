package jp.ac.aiit.jointry.lang.ast;

import jp.ac.aiit.jointry.lang.parser.env.Environment;
import jp.ac.aiit.jointry.lang.parser.Token;

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
