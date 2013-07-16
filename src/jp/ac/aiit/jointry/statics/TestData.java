package jp.ac.aiit.jointry.statics;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: データのやり取りが決まるまでの暫定クラス
 */
public class TestData<T> {

    private static BufferedImage buf;
    private static final Map<String, Object> map = new HashMap();

    public T get(String key) {
        return (T) map.get(key);
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public BufferedImage getCameraFile() {
        return buf;
    }

    public void setCameraFile(BufferedImage buf) {
        this.buf = buf;
    }
}
