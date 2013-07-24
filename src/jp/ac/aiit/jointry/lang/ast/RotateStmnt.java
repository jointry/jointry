package jp.ac.aiit.jointry.lang.ast;

import java.util.List;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import jp.ac.aiit.jointry.lang.parser.env.Environment;

public class RotateStmnt extends ASTList {

    public RotateStmnt(List<ASTree> list) {
        super(list);
    }

    public ASTree condition() {
        return child(0);
    }

    @Override
    public String toString() {
        return "(rotate " + condition() + ")";
    }

    @Override
    public Object eval(Environment env) {
        Object c = ((ASTree) condition()).eval(env);
        if (c instanceof Integer) {
            ImageView image = env.getImage();
            SequentialTransition st = env.getSequentialTransition();
            RotateTransition rt = new RotateTransition(Duration.millis(1000), image);
            rt.setByAngle((Integer) c);
            rt.setAutoReverse(true);
            st.getChildren().add(rt);
        }

        return c;
    }
}
