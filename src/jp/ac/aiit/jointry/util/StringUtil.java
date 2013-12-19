package jp.ac.aiit.jointry.util;

public class StringUtil {

    public static String multply(String str, int times) {
        StringBuilder buf = new StringBuilder();
        for (int multi = 0; multi < times; multi++) {
            buf.append(str);
        }
        return buf.toString();
    }
}
