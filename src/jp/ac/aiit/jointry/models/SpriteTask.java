package jp.ac.aiit.jointry.models;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.SequentialTransition;
import javafx.concurrent.Task;
import javafx.scene.Node;
import jp.ac.aiit.jointry.controllers.BackStageController;
import jp.ac.aiit.jointry.services.lang.ast.ASTree;
import jp.ac.aiit.jointry.services.lang.ast.NullStmnt;
import jp.ac.aiit.jointry.services.lang.parser.Environment;
import jp.ac.aiit.jointry.services.lang.parser.JointryParser;
import jp.ac.aiit.jointry.services.lang.parser.LangReader;
import jp.ac.aiit.jointry.services.lang.parser.Lexer;
import jp.ac.aiit.jointry.services.lang.parser.ParseException;
import jp.ac.aiit.jointry.services.lang.parser.Token;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;

public class SpriteTask extends Task {

    private Sprite sprite;
    private double speed;
    private SequentialTransition st;

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public String getCode() {
        StringBuilder code = new StringBuilder();

        for (Node node : sprite.getScriptPane().getChildrenUnmodifiable()) {
            if (node instanceof Statement) {
                Statement block = (Statement) node;
                if (block.isTopLevelBlock()) {
                    code.append(block.intern());
                }
            }
        }
        // Debug
        System.out.println(code);

        return code.toString();
    }

    @Override
    protected Object call() throws Exception {
        String code = getCode();
        Lexer lexer = new Lexer(new LangReader(code));
        JointryParser parser = new JointryParser();

        Environment env = new Environment();
        env.setSprite(sprite);
        env.setSpeed(speed);
        st = new SequentialTransition();
        env.setSequentialTransition(st);
        Object retval = null;
        try {
            while (lexer.peek(0) != Token.EOF) {
                ASTree t = parser.parse(lexer);
                if (!(t instanceof NullStmnt)) {
                    retval = t.eval(env);
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(BackStageController.class.getName()).log(Level.SEVERE, null, ex);
        }

        st.play();

        return retval;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void finish() {
        if (st != null) {
            st.stop();
        }
    }
}
