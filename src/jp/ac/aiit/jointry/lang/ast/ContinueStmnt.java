/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.lang.ast;

import java.util.List;
import jp.ac.aiit.jointry.lang.parser.Environment;

public class ContinueStmnt extends ASTList {
    public ContinueStmnt(List<ASTree> c) {
        super(c);
    }

    @Override
    public Object eval(Environment env) {
        return this;
    }
}
