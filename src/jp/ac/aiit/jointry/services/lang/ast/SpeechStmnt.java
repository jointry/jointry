package jp.ac.aiit.jointry.services.lang.ast;

import java.util.List;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.services.lang.parser.Environment;

public class SpeechStmnt extends ASTList {

    public SpeechStmnt(List<ASTree> list) {
        super(list);
    }

    public ASTree condition() {
        return child(0);
    }

    @Override
    public String toString() {
        return "(speech " + condition() + ")";
    }

    @Override
    public Object eval(Environment env) {
        final Object c = ((ASTree) condition()).eval(env);
        if (c instanceof String) {
            final Sprite sprite = env.getSprite();
            SequentialTransition st = env.getSequentialTransition();
            TranslateTransition tt
                    = new TranslateTransition(Duration.millis(10), sprite);
            tt.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    sprite.setSpeechBubble((String) c);
                }
            });
            st.getChildren().add(tt);
        }
        return c;
    }
}
