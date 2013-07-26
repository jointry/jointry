package jp.ac.aiit.jointry.lang.parser.env;

import java.util.HashMap;
import javafx.animation.SequentialTransition;
import jp.ac.aiit.jointry.models.Sprite;

public class Environment {

    protected HashMap<String, Object> values;
    private Sprite sprite;
    private SequentialTransition sequentialTransition;

    public Environment() {
        values = new HashMap<>();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void put(String name, Object value) {
        values.put(name, value);
    }

    public Object get(String name) {
        return values.get(name);
    }

    public SequentialTransition getSequentialTransition() {
        return sequentialTransition;
    }

    public void setSequentialTransition(SequentialTransition sequentialTransition) {
        this.sequentialTransition = sequentialTransition;
    }
}
