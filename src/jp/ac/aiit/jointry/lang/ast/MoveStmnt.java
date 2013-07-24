package jp.ac.aiit.jointry.lang.ast;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import jp.ac.aiit.jointry.lang.parser.env.Environment;

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
            ImageView image = env.getImage();
            SequentialTransition st = env.getSequentialTransition();
            TranslateTransition tt =
                    new TranslateTransition(Duration.millis(1000), image);
            tt.setByX((Integer) c);
            st.getChildren().add(tt);
        }
        return c;
    }
}
