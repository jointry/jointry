package jp.ac.aiit.jointry.services.broker;

import java.util.logging.Level;
import java.util.logging.Logger;
import jp.ac.aiit.jointry.services.broker.app.JointryBroker;
import jp.ac.aiit.jointry.services.broker.app.JointryDialogBase;
import jp.ac.aiit.jointry.services.broker.core.Broker;

public class JointryBrokerMain {

    private static Broker broker;
    private static final int PORT = 8081;

    public static void main(String[] args) {
        if (broker != null) {
            return;
        }

        try {
            broker = new JointryBroker(PORT, "jointry");
            broker.multiServer(true);
            JointryDialogBase.install(null);
        } catch (Exception ex) {
            System.out.println("既に起動している可能性があります。");
            System.out.println("止めるときはプロセス殺してください。");
            Logger.getLogger(JointryBrokerMain.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        broker.start(); //止めるときはプロセス殺してください
    }
}
