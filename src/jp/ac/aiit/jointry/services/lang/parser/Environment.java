package jp.ac.aiit.jointry.services.lang.parser;

import java.util.HashMap;
import javafx.animation.SequentialTransition;
import jp.ac.aiit.jointry.controllers.MainController;
import jp.ac.aiit.jointry.models.Sprite;

public class Environment {

    protected HashMap<String, Object> values;
    private Sprite sprite;
    private MainController mainController;
    private SequentialTransition sequentialTransition;
    private double x = 0.0;
    private double y = 0.0;
    private double speed;

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

    public void setValues(HashMap values) {
        this.values = values;
    }

    public HashMap getValues() {
        return values;
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public MainController getMainController() {
        return mainController;
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

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return this.speed;
    }
}
