package jp.ac.aiit.jointry.services.lang.ast;

import java.util.List;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.services.lang.parser.Environment;

public class SpeechStmnt extends ASTList {

    public SpeechStmnt(List<ASTree> list) {
        super(list);
    }

    public ASTree condition() {
        return child(0);
    }

    @Override
    public String toString() {
        return "(speech " + condition() + ")";
    }

    @Override
    public Object eval(Environment env) {
        Sprite sprite = env.getSprite();
        Object c = ((ASTree) condition()).eval(env);
        sprite.setSpeechBubble(c.toString());
        return c;
    }
}
