package jp.ac.aiit.jointry.services.lang.ast;

import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import jp.ac.aiit.jointry.services.lang.parser.Environment;
import jp.ac.aiit.jointry.models.Sprite;

public class ReboundStmnt extends ASTList {

    public ReboundStmnt(List<ASTree> list) {
        super(list);
    }

    public ASTree condition() {
        return child(0);
    }

    @Override
    public String toString() {
        return "(rebound " + condition() + ")";
    }

    @Override
    public Object eval(Environment env) {

        Sprite sprite = env.getSprite();
        Bounds spriteBounds = sprite.getBoundsInLocal();
        Pane parent = (AnchorPane) sprite.getParent();

        //x軸への動きしか想定しない
        double rCollision = parent.getWidth() - sprite.getTranslateX() - env.getX() - spriteBounds.getWidth();
        double lCollision = sprite.getTranslateX() + env.getX();

        if (rCollision < 0 || lCollision < 0) {
            return TRUE;
        }

        return FALSE;
    }
}
