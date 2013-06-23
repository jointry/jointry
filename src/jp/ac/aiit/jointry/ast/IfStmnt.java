package jp.ac.aiit.jointry.ast;
import java.util.List;

import jp.ac.aiit.jointry.parser.env.Environment;

public class IfStmnt extends ASTList {
	public IfStmnt(List<ASTree> c) { super(c); }
	public ASTree condition() { return child(0); }
	public ASTree thenBlock() { return child(1); }
	public ASTree elseBlock() {
		return numChildren() > 2 ? child(2) : null;
	}
	public String toString() {
		return "(if " + condition() + " " + thenBlock()
			+ " else " + elseBlock() + ")";
	}

	public Object eval(Environment env) {
		Object c = ((ASTree)condition()).eval(env);
		if (c instanceof Integer && ((Integer)c).intValue() != FALSE)
			return ((ASTree)thenBlock()).eval(env);
		ASTree b = elseBlock();
		if (b == null) return 0;
		return ((ASTree)b).eval(env);
	}
}
