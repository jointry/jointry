package jp.ac.aiit.jointry.lang.parser.env;

import javafx.animation.SequentialTransition;
import javafx.scene.image.ImageView;

public interface Environment {

    void put(String name, Object value);

    Object get(String name);

    void putNew(String name, Object value);

    Environment where(String name);

    void setOuter(Environment e);

    ImageView getImage();

    public void setImage(ImageView image);

    SequentialTransition getSequentialTransition();

    public void setSequentialTransition(SequentialTransition sequentialTransition);
}
