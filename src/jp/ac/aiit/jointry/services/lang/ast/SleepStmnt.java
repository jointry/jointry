package jp.ac.aiit.jointry.services.lang.ast;

import java.util.List;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import jp.ac.aiit.jointry.services.lang.parser.Environment;

public class SleepStmnt extends ASTList {

    public SleepStmnt(List<ASTree> list) {
        super(list);
    }

    public ASTree condition() {
        return child(0);
    }

    @Override
    public Object eval(Environment env) {
        Object c = ((ASTree) condition()).eval(env);
        Integer sleep_time = (Integer) c;
        SequentialTransition st = env.getSequentialTransition();
        PauseTransition pt = new PauseTransition(Duration.millis(sleep_time * 100));
        st.getChildren().add(pt);
        return this;
    }

}
