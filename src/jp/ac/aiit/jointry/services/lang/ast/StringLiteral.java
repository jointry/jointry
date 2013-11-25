package jp.ac.aiit.jointry.services.lang.ast;

import jp.ac.aiit.jointry.util.Environment;
import jp.ac.aiit.jointry.services.lang.parser.Token;

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
