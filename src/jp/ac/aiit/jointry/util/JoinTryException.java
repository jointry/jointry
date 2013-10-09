package jp.ac.aiit.jointry.util;

import jp.ac.aiit.jointry.lang.ast.ASTree;

public class JoinTryException extends RuntimeException {

    public JoinTryException(String m) {
        super(m);
    }

    public JoinTryException(String m, ASTree t) {
        super(m + " " + t.location());
    }
}
