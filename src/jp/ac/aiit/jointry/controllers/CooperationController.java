package jp.ac.aiit.jointry.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jp.ac.aiit.jointry.services.broker.core.Agent;
import jp.ac.aiit.jointry.services.broker.core.Broker;
import jp.ac.aiit.jointry.services.broker.core.Common;

public class CooperationController {

    @FXML
    private Label kind;
    @FXML
    private TextField name;
    @FXML
    private PasswordField password;
    @FXML
    private TextField url;
    @FXML
    private Button proxy;
    private Broker broker;
    private Agent agent;
    private String role = Common.CLIENT;
    private final int PORT = 8081;

    @FXML
    protected void startProxy(ActionEvent event) {
        if (broker != null) return;

        try {
            broker = new Broker(PORT, "jointry");
        } catch (Exception ex) {
            Logger.getLogger(CooperationController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        broker.start();
    }

    @FXML
    protected void startCooperation(ActionEvent event) {
        agent = new Agent();

        if (agent.open(url.getText(), Common.CHAT_SERVICE, role, name.getText(), password.getText(), null)) {
            agent.startListening(Common.CHAT_TIMEOUT);
            windowClose();
        }
    }

    @FXML
    protected void windowClose(ActionEvent event) {
        windowClose();
    }

    private void windowClose() {
        Stage stage = (Stage) kind.getScene().getWindow();
        stage.close();
    }

    public void setRole(String role) {
        this.role = role; //サーバーになる
        kind.setText("協同編集 " + role);
        name.setText("Matsuko");
        password.setText("ym");
        proxy.setVisible(true);
    }

    public Broker getBroker() {
        return broker;
    }

    public Agent getAgent() {
        return agent;
    }
}
