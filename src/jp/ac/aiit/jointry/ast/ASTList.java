package jp.ac.aiit.jointry.ast;

import java.util.List;
import java.util.Iterator;
import jp.ac.aiit.jointry.parser.env.Environment;
import jp.ac.aiit.jointry.parser.JoinTryException;

public class ASTList extends ASTree {

    protected List<ASTree> children;

    public ASTList(List<ASTree> list) {
        children = list;
    }

    public ASTree child(int i) {
        return children.get(i);
    }

    public int numChildren() {
        return children.size();
    }

    public Iterator<ASTree> children() {
        return children.iterator();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        String sep = "";
        for (ASTree t : children) {
            builder.append(sep);
            sep = " ";
            builder.append(t.toString());
        }
        return builder.append(')').toString();
    }

    public String location() {
        for (ASTree t : children) {
            String s = t.location();
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    public Object eval(Environment env) {
        throw new JoinTryException("cannot eval: " + toString(), this);
    }
}
