package jp.ac.aiit.jointry.statics;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: データのやり取りが決まるまでの暫定クラス
 */
public class TestData<T> {

    private static final Map<String, Object> map = new HashMap();

    public T get(String key) {
        return (T) map.get(key);
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }
}
