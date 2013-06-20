package jointry.ast;
import jointry.parser.env.Environment;
import jointry.parser.JoinTryException;
import jointry.parser.Token;

public class Name extends ASTLeaf {
	public Name(Token t) { super(t); }
	public String name() { return token().getText(); }

	public Object eval(Environment env) {
		Object value = env.get(name());
		if (value != null) return value;
		throw new JoinTryException("undefined name: " + name(), this);
	}
}
