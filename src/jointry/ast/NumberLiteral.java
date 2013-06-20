package jointry.ast;
import jointry.parser.env.Environment;
import jointry.parser.Token;

public class NumberLiteral extends ASTLeaf {
	public NumberLiteral(Token t) { super(t); }
	public int value() { return token().getNumber(); }
	public Object eval(Environment e) { return value(); }
}
