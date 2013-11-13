/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import java.util.ArrayList;
import broker.core.DInfo;
import broker.core.DialogBase;

public class JointryDialog extends DialogBase {

    private static ArrayList<IWorkerMonitor> listeners = new ArrayList();
    private static boolean installed = false;

    public static void putListener(IWorkerMonitor listener) {
        listeners.add(listener);
    }

    public static void Install() {
        if (!installed) {
            DialogBase.addDialog(JointryCommon.D_FRONT, JointryDialog.class);
            installed = true;
        }
    }

    @Override
    public void onAnswer(DInfo dinfo) {
        for (IWorkerMonitor listener : listeners) {
            listener.onAnswer(getEventId(dinfo), dinfo);
        }
    }

    @Override
    public void onNotify(DInfo dinfo) {
        for (IWorkerMonitor listener : listeners) {
            listener.onNotify(getEventId(dinfo), dinfo);
        }
    }

    @Override
    public void onQuery(DInfo dinfo) {
        for (IWorkerMonitor listener : listeners) {
            listener.onQuery(getEventId(dinfo), dinfo);
        }
    }

    private int getEventId(DInfo dinfo) {
        int eventId = dinfo.getInt(JointryCommon.KC_METHOD);

        if (eventId == 0) return JointryCommon.VM_DUMMY;
        return eventId;
    }
}
