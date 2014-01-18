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

        double scale = sprite.getScaleX();
        t.setFromX(scale);
        t.setToX(scale * -1);

        t.setNode(sprite);
        st.getChildren().add(t);
        return null;
    }
}
