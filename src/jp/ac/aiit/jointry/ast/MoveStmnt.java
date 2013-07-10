package jp.ac.aiit.jointry.ast;

import java.util.List;
import javafx.scene.image.ImageView;
import jp.ac.aiit.jointry.parser.env.Environment;

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
            image.setTranslateX((Integer) c);
        }
        return c;
    }
}
