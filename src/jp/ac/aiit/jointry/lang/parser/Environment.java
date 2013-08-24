package jp.ac.aiit.jointry.lang.parser;

import java.util.HashMap;
import javafx.animation.SequentialTransition;
import jp.ac.aiit.jointry.models.Sprite;

public class Environment {

    protected HashMap<String, Object> values;
    private Sprite sprite;
    private SequentialTransition sequentialTransition;
    private double x = 0.0;
    private double y = 0.0;

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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
