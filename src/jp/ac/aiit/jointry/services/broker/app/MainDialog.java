package jp.ac.aiit.jointry.services.broker.app;

import broker.core.Agent;
import broker.core.DInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.services.file.FileManager;
import jp.ac.aiit.jointry.util.JsonUtil;

public class MainDialog extends JointryDialogBase {

    @Override
    public void onAnswer(int event, DInfo dinfo) {
    }

    @Override
    public void onQuery(int event, DInfo dinfo) {
        switch (event) {
            case M_MAIN_CONNECT:
                mMainConnect(dinfo);
                break;

            case M_MAIN_DISCONNECT:
                mMainDisconnect(dinfo);
                break;
        }
    }

    @Override
    public void onNotify(int event, DInfo dinfo) {
        if (mainController == null) {
            return;
        }

        switch (event) {
            case M_MAIN_MEMBERS:
                mMainMembers(dinfo);
                break;

            case M_MAIN_SYNCHRONIZE:
                mMainSynchronize(dinfo);
                break;

            default:
                break;
        }
    }

    private void mMainConnect(DInfo dinfo) {
        JointryAccount.addUser(dinfo.get(K_USER_NAME));
        mainController.refreshMembers();
        sendMembers();
        sendSynchronize();
    }

    private void mMainDisconnect(DInfo dinfo) {
        JointryAccount.removeUser(dinfo.get(K_USER_NAME));
        mainController.refreshMembers();
        sendMembers();
    }

    private void mMainSynchronize(DInfo dinfo) {
        //一旦画面をクリアしてから同期させる
        mainController.initWindow("load");
        mainController.initWindow("connect");

        String main_info = dinfo.get(K_MAIN_INFO);
        final List<String> list = JsonUtil.convertJsonStringToList(main_info);

        for (String jsonString : list) {
            Sprite sprite = null;
            try {
                sprite = JsonUtil.parseJSONStringToSprite(jsonString, new File(""));
            } catch (Exception ex) {
                Logger.getLogger(MainDialog.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (sprite != null) {
                sprite.setMainController(mainController);
                mainController.getFrontStageController().addSprite(sprite, false);
                if (mainController.getFrontStageController().getCurrentSprite() == null) {
                    mainController.getFrontStageController().setCurrentSprite(sprite);
                }
            }
        }
    }

    private void mMainMembers(DInfo dinfo) {
        JointryAccount.getUsers().clear();
        String members = dinfo.get(K_USER_NAME_LIST);
        JointryAccount.addAllUser(members.substring(1, members.length() - 1).split(","));
        mainController.refreshMembers();
    }

    public static void sendSynchronize() {
        List<String> spriteList = new ArrayList();
        for (Sprite sprite : mainController.getFrontStageController().getSprites()) {
            String json = null;
            try {
                String tempFile = new MainDialog().makeFilePath("");
                json = FileManager.convertSpriteToJson(sprite, tempFile);
            } catch (IOException ex) {
                Logger.getLogger(MainDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
            spriteList.add(json);
        }

        String main_info = JsonUtil.convertObjectToJsonString(spriteList);

        DInfo dinfo = new DInfo(D_MAIN);
        dinfo.set(K_METHOD, M_MAIN_SYNCHRONIZE);
        dinfo.set(K_MAIN_INFO, main_info);

        mainController.getAgent().sendNotify(dinfo);
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
