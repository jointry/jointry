package jp.ac.aiit.jointry.lang.ast;

import java.util.List;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;
import jp.ac.aiit.jointry.lang.parser.Environment;
import jp.ac.aiit.jointry.models.Sprite;

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
