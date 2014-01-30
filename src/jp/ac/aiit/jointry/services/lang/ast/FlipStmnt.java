package jp.ac.aiit.jointry.services.lang.ast;

import java.util.List;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import jp.ac.aiit.jointry.services.lang.parser.Environment;
import jp.ac.aiit.jointry.models.Sprite;

public class FlipStmnt extends ASTList {

    public FlipStmnt(List<ASTree> c) {
        super(c);
    }

    @Override
    public Object eval(Environment env) {
        Sprite sprite = env.getSprite();
        SequentialTransition st = env.getSequentialTransition();
        ScaleTransition t = new ScaleTransition();

        int fromDirection = sprite.getDirection();
        t.setFromX(fromDirection);

        int toDirection = fromDirection * -1;
        t.setToX(toDirection);
 
         t.setNode(sprite);
         st.getChildren().add(t);

        sprite.setDirection(toDirection);
        return null;
    }
}
