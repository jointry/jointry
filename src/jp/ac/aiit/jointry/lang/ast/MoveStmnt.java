package jp.ac.aiit.jointry.lang.ast;

import java.util.List;
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
            TranslateTransition tt = new TranslateTransition(
                    Duration.millis(1000), image);
            tt.setFromX(image.getTranslateX());
            tt.setToX(image.getTranslateX() + (Integer) c);
            tt.play();
        }
        return c;
    }
}
