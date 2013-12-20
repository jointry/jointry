package jp.ac.aiit.jointry.services.broker.app;

import broker.core.Agent;
import broker.core.DInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.services.file.FileManager;
import jp.ac.aiit.jointry.util.JsonUtil;

public class MainDialog extends JointryDialogBase {

    @Override
    public void onAnswer(DInfo dinfo) {
    }

    @Override
    public void onQuery(final DInfo dinfo) {
        int event = getEvent(dinfo);

        switch (event) {
            case M_MAIN_CONNECT:
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        JointryAccount.addUser(dinfo.get(K_USER_NAME));
                        mainController.refreshMembers();
                        sendMembers();
                        sendAnswer(dinfo, V_OK);
                    }
                });
                break;

            case M_MAIN_DISCONNECT:
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        JointryAccount.removeUser(dinfo.get(K_USER_NAME));
                        mainController.refreshMembers();
                        sendMembers();
                        sendAnswer(dinfo, V_OK);
                    }
                });
                break;

            case M_MAIN_REQUEST:
                List<String> spriteList = new ArrayList();
                for (Sprite sprite : mainController.getFrontStageController().getSprites()) {
                    String json = null;
                    try {
                        json = FileManager.convertSpriteToJson(sprite, makeFilePath(""));
                    } catch (IOException ex) {
                        Logger.getLogger(MainDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    spriteList.add(json);
                }

                String response = JsonUtil.convertObjectToJsonString(spriteList);
                sendMessage(M_MAIN_RESPONSE, response);
                sendAnswer(dinfo, V_OK);
                break;
        }
    }

    @Override
    public void onNotify(final DInfo dinfo) {
        if (mainController == null) {
            return;
        }

        int event = getEvent(dinfo);

        switch (event) {
            case M_MAIN_MEMBERS:
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        JointryAccount.clearUser();
                        String members = dinfo.get(K_USER_NAME_LIST);
                        JointryAccount.addAllUser(members.substring(1, members.length() - 1).split(","));
                        mainController.refreshMembers();
                    }
                });
                break;

            case M_MAIN_RESPONSE:
                String main_info = dinfo.get(K_MAIN_INFO);
                final List<String> list = JsonUtil.convertJsonStringToList(main_info);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        for (String jsonString : list) {
                            try {
                                Sprite sprite = JsonUtil.parseJSONStringToSprite(jsonString, new File(""));
                                if (sprite != null) {
                                    sprite.setMainController(mainController);
                                    mainController.getFrontStageController().addSprite(sprite, false);
                                    if (mainController.getFrontStageController().getCurrentSprite() == null) {
                                        mainController.getFrontStageController().setCurrentSprite(sprite);
                                    }

                                }
                            } catch (Exception ex) {
                                Logger.getLogger(MainDialog.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });

                break;

            default:
                System.out.println("unknown event : " + event);
                break;
        }
    }

    private void sendMessage(int event, String main_info) {
        DInfo dinfo = new DInfo(D_MAIN);
        dinfo.set(K_METHOD, event);
        dinfo.set(K_MAIN_INFO, main_info);

        sendNotify(dinfo);
    }

    public static void sendMessage(int event, Agent agent) {
        if (agent != null) {
            DInfo dinfo = new DInfo(D_MAIN);
            dinfo.set(K_METHOD, event);

            agent.sendQuery(dinfo);
        }
    }

    public static void sendConnection(int event, Agent agent, String name) {
        if (agent != null) {
            DInfo dinfo = new DInfo(D_MAIN);
            dinfo.set(K_METHOD, event);
            dinfo.set(K_USER_NAME, name);

            agent.sendQuery(dinfo);
        }
    }

    public void sendMembers() {
        DInfo dinfo = new DInfo(D_MAIN);
        dinfo.set(K_METHOD, M_MAIN_MEMBERS);
        dinfo.set(K_USER_NAME_LIST, JointryAccount.getUsers().toString());

        sendNotify(dinfo);
    }
}
