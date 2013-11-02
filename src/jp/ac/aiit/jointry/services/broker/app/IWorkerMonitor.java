/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import jp.ac.aiit.jointry.services.broker.core.DInfo;

public interface IWorkerMonitor extends IJointApp{

    public void onAnswer(int eventId, DInfo dinfo);

    public void onNotify(int eventId, DInfo dinfo);

    public void onQuery(int eventId, DInfo dinfo);
}
