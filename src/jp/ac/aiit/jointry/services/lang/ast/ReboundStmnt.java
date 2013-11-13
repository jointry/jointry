package jp.ac.aiit.jointry.services.lang.ast;

import java.util.List;
import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.util.Duration;
import static jp.ac.aiit.jointry.services.lang.ast.ASTree.TRUE;
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

        //TODO

        /*
         // 端っこにぶつかったとき！
         if (!sprite.isInsideDragRange(b.getMaxX(), b.getMaxY())
         || !sprite.isInsideDragRange(b.getMinX(), b.getMinY())
         || !sprite.isInsideDragRange(b.getMaxX(), b.getMinY())
         || !sprite.isInsideDragRange(b.getMinX(), b.getMaxY())) {

         TranslateTransition tt =
         new TranslateTransition(Duration.millis(100), sprite);
         sprite.changeDirection();
         tt.setByX(sprite.moveBy(sprite.getImage().getWidth() + 50));
         st.getChildren().add(tt);
         }
         */

        return null;
    }
}
