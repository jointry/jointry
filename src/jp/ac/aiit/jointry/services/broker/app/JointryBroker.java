/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import broker.core.Broker;
import broker.core.HttpInfo;
import broker.core.ServerProxy;

public class JointryBroker extends Broker implements JointryCommon {

    public JointryBroker(int port, String market) throws Exception {
        super(port, market);
        this.setAccount(new JointryAccount());
    }

    @Override
    protected ServerProxy findServer(HttpInfo hinfo) {
        for (ServerProxy sp : spList()) {
            if (sp.canServeTo(hinfo)) {
                //プロキシIDと一致させ特定のサーバに紐づける
                int temp_hinfo =  hinfo.getInt(PROXY_ID);
                int temp_sp = sp.getID();

                if (temp_sp == temp_hinfo) return sp;
            }
        }
        return null;
    }
}
