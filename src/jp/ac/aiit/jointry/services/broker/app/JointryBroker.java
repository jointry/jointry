/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import jp.ac.aiit.jointry.services.broker.core.Broker;
import jp.ac.aiit.jointry.services.broker.core.HttpInfo;
import jp.ac.aiit.jointry.services.broker.core.ServerProxy;

public class JointryBroker extends Broker {

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
