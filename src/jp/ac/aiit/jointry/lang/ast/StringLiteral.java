package jp.ac.aiit.jointry.lang.ast;

import jp.ac.aiit.jointry.lang.parser.Environment;
import jp.ac.aiit.jointry.lang.parser.Token;

public class StringLiteral extends ASTLeaf {

    public StringLiteral(Token t) {
        super(t);
    }

    public String value() {
        return token().getText();
    }

    public Object eval(Environment e) {
        return value();
    }
}
