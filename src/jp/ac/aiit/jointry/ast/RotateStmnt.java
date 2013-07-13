package jp.ac.aiit.jointry.ast;

import java.util.List;
import javafx.scene.image.ImageView;

import jp.ac.aiit.jointry.parser.env.Environment;

/**
 * RotateStatement. 言語仕様rotateに対応するステートメント.
 *
 * @author kanemoto
 */
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
            image.setRotate(image.getRotate() + (Integer) c);
        }

        return c;
    }
}
