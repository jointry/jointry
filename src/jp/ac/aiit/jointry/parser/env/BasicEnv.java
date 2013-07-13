package jp.ac.aiit.jointry.parser.env;

import java.util.HashMap;
import javafx.scene.image.ImageView;

/**
 * 木構造に対するアクセス方法を提供
 */
public class BasicEnv implements Environment {

    protected HashMap<String, Object> values;
    private ImageView image;

    @Override
    public ImageView getImage() {
        return image;
    }

    @Override
    public void setImage(ImageView image) {
        this.image = image;
    }

    public BasicEnv() {
        values = new HashMap<String, Object>();
    }

    @Override
    public void put(String name, Object value) {
        values.put(name, value);
    }

    @Override
    public Object get(String name) {
        return values.get(name);
    }

    //↓↓↓↓↓とりあえずオーバーライド↓↓↓↓↓
    @Override
    public void putNew(String name, Object value) {
        //何もしない
    }

    @Override
    public Environment where(String name) {
        //何もしない
        return null;
    }

    @Override
    public void setOuter(Environment e) {
        //何もしない
    }
}
