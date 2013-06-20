package jointry.ast;
import java.util.List;

import jointry.parser.env.Environment;
import jointry.parser.JoinTryException;

public class NegativeExpr extends ASTList {
	public NegativeExpr(List<ASTree> c) { super(c); }
	public ASTree operand() { return child(0); }
	public String toString() {
		return "-" + operand();
	}

	public Object eval(Environment env) {
		Object v = ((ASTree)operand()).eval(env);
		if(v instanceof Integer)
			return new Integer(-((Integer)v).intValue());
		throw new JoinTryException("bad type for -", this);
	}
}
