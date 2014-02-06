package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import jp.ac.aiit.jointry.models.Room;
import jp.ac.aiit.jointry.services.broker.core.Agent;
import jp.ac.aiit.jointry.services.broker.core.DInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import jp.ac.aiit.jointry.services.broker.app.JointryAccount;
import jp.ac.aiit.jointry.services.broker.app.JointryCommon;
import jp.ac.aiit.jointry.services.broker.app.MainDialog;

public class CooperationController implements Initializable, JointryCommon {

    @FXML
    private TextField name;
    @FXML
    private TextField url;
    @FXML
    private FlowPane roomList;
    @FXML
    private Label messages;
    @FXML
    private RadioButton default_server;
    @FXML
    private RadioButton custom_server;
    private Agent agent;
    private Agent dummyAgent;
    private RoomView selectRoom;
    private final String DEFAULT_SERVER = "http://localhost:8081/index.html";
    private MainController mainController;

    @FXML
    protected void createRoom(ActionEvent event) {
        agent = new Agent();

        if (agent.open(url.getText(), CHAT_SERVICE, SERVER, name.getText(), "", null)) {
            agent.startListening(CHAT_TIMEOUT);
            JointryAccount.addUser(name.getText());
            mainController.initWindow("connect");

            windowClose();
        }
    }

    @FXML
    protected void participationRoom(ActionEvent event) {
        agent = new Agent();

        if (agent.open(url.getText(), accessMapping(CHAT_SERVICE, CLIENT, name.getText(), "", null, selectRoom.getRoom().getProxyId()))) {
            agent.startListening(CHAT_TIMEOUT);
            MainDialog.sendConnection(M_MAIN_CONNECT, agent, name.getText());
            mainController.initWindow("connect");

            windowClose();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connect(DEFAULT_SERVER);
    }

    @FXML
    protected void connect(ActionEvent event) {
        if (custom_server.isSelected()) {
            connect(this.url.getText());
        } else {
            connect(DEFAULT_SERVER);
        }
    }

    private void connect(String server) {
        if (dummyAgent != null) {
            return;
        }

        roomList.getChildren().clear();
        dummyAgent = new Agent();

        //立ち上がっているサーバー一覧を取得する
        if (dummyAgent.open(server, CHAT_SERVICE, SERVER, DUMMY_AGENT_NAME, "", null)) {
            dummyAgent.startListening(CHAT_TIMEOUT);

            DInfo info = dummyAgent.query(K_SERVER_INFO);
            String[] serverList = info.get(K_SERVER_INFO).split(":");

            int roomId = 1; //部屋番号
            for (String s : serverList) {
                Room room = new Room(s);
                if (room.getName().equals(DUMMY_AGENT_NAME)) {
                    continue;
                }

                addRoom(roomId++, room);//部屋登録
            }
        } else {
            messages.setText("協同編集用サーバーに接続出来ません。");
        }

        dummyAgent.close();
        dummyAgent = null;
    }

    private void addRoom(int roomId, Room room) {
        final RoomView roomView = new RoomView(roomId, room);

        roomView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (selectRoom != null) {
                    selectRoom.setStyle("-fx-border-color: white;");
                }

                roomView.setStyle("-fx-border-color: red;");
                selectRoom = roomView;
            }
        });

        FlowPane.setMargin(roomView, new Insets(20, 0, 0, 40));
        roomList.getChildren().add(roomView);
    }

    private Map<String, String> accessMapping(String serviceId, String role,
                                              String userId, String password, String proxyFQCN, String proxyId) {
        Map<String, String> paramMap = new HashMap();
        paramMap.put(USER_ROLE, role);
        paramMap.put(SERVICE_ID, serviceId);
        paramMap.put(USER_ID, userId);
        paramMap.put(PASSWORD, password);
        paramMap.put(PROXY_ID, proxyId);
        if (proxyFQCN != null) {
            paramMap.put(PROXY_FQCN, proxyFQCN);
        }

        return paramMap;
    }

    public void windowClose() {
        if (dummyAgent != null) {
            dummyAgent.close();
        }
        Stage stage = (Stage) roomList.getScene().getWindow();
        stage.close();
    }

    public Agent getAgent() {
        return agent;
    }

    public String getName() {
        return name.getText();
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    public void runBroker(ActionEvent event) {
        Runtime r = Runtime.getRuntime();
        String jar = System.getProperty("java.class.path");
        StringBuilder sb = new StringBuilder("java -classpath ");
        sb.append(jar);
        sb.append(" jp.ac.aiit.jointry.services.broker.JointryBrokerMain");
        try {
            System.out.println(sb.toString());
            Process p = r.exec(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(CooperationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
