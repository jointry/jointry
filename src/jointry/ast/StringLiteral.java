package jointry.ast;
import jointry.parser.env.Environment;
import jointry.parser.Token;

public class StringLiteral extends ASTLeaf {
	public StringLiteral(Token t) { super(t); }
	public String value() { return token().getText(); }
	public Object eval(Environment e) { return value(); }
}
