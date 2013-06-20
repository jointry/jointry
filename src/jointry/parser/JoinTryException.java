package jointry.parser;
import jointry.ast.ASTree;

public class JoinTryException extends RuntimeException {
	public JoinTryException(String m) { super(m); }
	public JoinTryException(String m, ASTree t) {
		super(m + " " + t.location());
	}
}
