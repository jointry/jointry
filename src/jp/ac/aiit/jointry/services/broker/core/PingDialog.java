package jp.ac.aiit.jointry.services.broker.core;

import jp.ac.aiit.jointry.services.broker.core.Property.IntProperty;
import jp.ac.aiit.jointry.services.broker.util.Util;

/**
 * 時刻を刻印したメッセージを交換するpingダイアログの実装クラス<br>
 * <pre>
 * Usage: /ping
 * </pre>
 */
public class PingDialog extends DialogBase {

    public static int queryCount = 0;
    public static int notifyCount = 0;

    @Override
    public boolean query(DInfo dinfo) {
        dinfo.set(K_TIME_QUERY, Util.date());
        sendQuery(dinfo);
        return true;
    }

    @Override
    public void onQuery(DInfo dinfo) {
        dinfo.set(K_TIME_ONQUERY, Util.date());
        sendAnswer(dinfo, V_OK);
        accessTimeOffset(dinfo);
        queryCount++;
    }

    @Override
    public void onAnswer(DInfo dinfo) {
        dinfo.set(K_TIME_ONANSWER, Util.date());
    }

    @Override
    public boolean notify(DInfo dinfo) {
        dinfo.set(K_TIME_NOTIFY, Util.date());
        sendNotify(dinfo);
        return true;
    }

    @Override
    public void onNotify(DInfo dinfo) {
        dinfo.set(K_TIME_ONNOTIFY, Util.date());
        showStatus(dinfo.message());
        notifyCount++;
        accessTimeOffset(dinfo);
    }

    /**
     * 時刻補正
     */
    private static IntProperty timeOffset = new IntProperty(K_TIME_OFFSET, 0);

    public static void setTimeOffset(int offset) {
        timeOffset.setValue("" + offset);
        Util.timeOffset(offset);
    }

    private static void accessTimeOffset(DInfo dinfo) {
        int offset1 = timeOffset.ivalue();
        if (timeOffset.access(dinfo)) {
            int offset2 = timeOffset.ivalue();
            if (offset1 != offset2) {
                Util.timeOffset(offset2);
            }
        }
    }

}
