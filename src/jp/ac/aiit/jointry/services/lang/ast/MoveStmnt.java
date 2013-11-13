package jp.ac.aiit.jointry.services.lang.ast;

import java.util.List;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import jp.ac.aiit.jointry.services.lang.parser.Environment;
import jp.ac.aiit.jointry.models.Sprite;

public class MoveStmnt extends ASTList {

    public MoveStmnt(List<ASTree> list) {
        super(list);
    }

    public ASTree condition() {
        return child(0);
    }

    @Override
    public String toString() {
        return "(move " + condition() + ")";
    }

    @Override
    public Object eval(Environment env) {
        Object c = ((ASTree) condition()).eval(env);
        if (c instanceof Integer) {
            Sprite sprite = env.getSprite();
            SequentialTransition st = env.getSequentialTransition();
            TranslateTransition tt =
                    new TranslateTransition(Duration.millis(100), sprite);
            double x = sprite.moveBy((Integer) c);
            tt.setByX(x);
            env.setX(sprite.getX() + env.getX() + x);
            st.getChildren().add(tt);
        }
        return c;
    }
}
