package jp.ac.aiit.jointry.ast;

import jp.ac.aiit.jointry.parser.env.Environment;
import jp.ac.aiit.jointry.parser.Token;

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
