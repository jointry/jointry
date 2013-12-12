/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import broker.core.Agent;
import broker.core.DInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import jp.ac.aiit.jointry.models.Jty;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.util.JsonUtil;

public class MainDialog extends JointryDialogBase {

    @Override
    public void onNotify(DInfo dinfo) {
        if (mainController == null) {
            return;
        }

        int event = getEvent(dinfo);

        switch (event) {
            case M_MAIN_REQUEST:
                List<String> spriteList = new ArrayList();
                for (Sprite sprite : mainController.getFrontStageController().getSprites()) {
                    Jty wrap = new Jty();
                    wrap.setSprite(JsonUtil.processSprite(sprite));

                    try {
                        String filePath = makeFilePath("img"); //workroot直下へ作成
                        wrap.setCostume(JsonUtil.processCostumes(sprite, JsonUtil.TYPE_BASE64, filePath));
                    } catch (IOException ex) {
                        Logger.getLogger(MainDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    wrap.setScript(JsonUtil.processScript(sprite, JsonUtil.TYPE_BASE64, null));

                    spriteList.add(JsonUtil.convertObjectToJsonString(wrap));
                }

                String response = JsonUtil.convertObjectToJsonString(spriteList);
                sendMessage(M_MAIN_RESPONSE, response);
                break;

            case M_MAIN_RESPONSE:
                String main_info = dinfo.get(K_MAIN_INFO);
                final List<String> list = JsonUtil.convertJsonStringToList(main_info);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mainController.initWindow("load");

                        for (String jsonString : list) {
                            try {
                                Sprite sprite = JsonUtil.parseJSONStringToSprite(jsonString, JsonUtil.TYPE_BASE64, null);
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

            agent.sendNotify(dinfo);
        }
    }
}