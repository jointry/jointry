package jp.ac.aiit.jointry.services.broker.app;

import broker.core.DInfo;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.util.JsonUtil;

public class SpriteDialog extends JointryDialogBase {

    @Override
    public void onAnswer(int event, DInfo dinfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onQuery(int event, DInfo dinfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onNotify(int event, DInfo dinfo) {
        if (event == M_SPRITE_CREATE && mainController != null) {
            mSpriteCreate(dinfo);
        }

        Sprite sprite = getTargetSprite(dinfo);
        if (sprite == null) {
            return; //該当なし
        }
        switch (event) {
            case M_SPRITE_SELECT:
                mSpriteSelect(sprite, dinfo);
                break;

            case M_SPRITE_DRAGGED:
                mSpriteDragged(sprite, dinfo);
                break;

            case M_SPRITE_RELEASD:
                mSpriteReleasd(sprite, dinfo);
                break;

            case M_SPRITE_CHANGED:
                mSpriteChanged(sprite, dinfo);
                break;

            case M_COSTUME_SYNC:
                mCostumeSync(sprite, dinfo);
                break;

            default:
                break;
        }
    }

    private void mSpriteCreate(DInfo dinfo) {
        Sprite sprite = new Sprite();
        sprite.setMainController(mainController);
        sprite.setName(dinfo.get(K_SPRITE_NAME));
        sprite.setLayoutX(dinfo.getInt(K_X1));
        sprite.setLayoutY(dinfo.getInt(K_Y1));

        String jsonString = dinfo.get(K_COSTUME_LIST);
        List<Map> list = JsonUtil.convertJsonStringToList(jsonString);
        try {
            JsonUtil.parseJSONStringToCostumes(sprite, list, new File(""));
        } catch (MalformedURLException ex) {
            Logger.getLogger(SpriteDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        sprite.setSpriteCostume(dinfo.getInt(K_COSTUME_CURRENT));

        mainController.getFrontStageController().addSprite(sprite, false);
    }

    private void mSpriteSelect(Sprite sprite, DInfo dinfo) {
        sprite.setEffect(new Shadow(4.0f, Color.valueOf(dinfo.get(K_COLOR))));
    }

    private void mSpriteDragged(Sprite sprite, DInfo dinfo) {
        sprite.setTranslateX(dinfo.getInt(K_X2));
        sprite.setTranslateY(dinfo.getInt(K_Y2));
    }

    private void mSpriteReleasd(Sprite sprite, DInfo dinfo) {
        sprite.setTranslateX(dinfo.getInt(K_X2));
        sprite.setTranslateY(dinfo.getInt(K_Y2));

        sprite.setEffect(null);
    }

    private void mSpriteChanged(Sprite sprite, DInfo dinfo) {
        sprite.setTranslateX(dinfo.getInt(K_X2));
        sprite.setTranslateY(dinfo.getInt(K_Y2));
        sprite.setRotate(dinfo.getInt(K_ROTATE));
        sprite.setSpeechBubble(dinfo.get(K_SPEECH));
    }

    private void mCostumeSync(Sprite sprite, DInfo dinfo) {
        //コスチュームを一旦全削除
        sprite.clearCostume();

        String jsonString = dinfo.get(K_COSTUME_LIST);
        List<Map> list = JsonUtil.convertJsonStringToList(jsonString);
        try {
            JsonUtil.parseJSONStringToCostumes(sprite, list, new File(""));
        } catch (MalformedURLException ex) {
            Logger.getLogger(SpriteDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        sprite.setSpriteCostume(dinfo.getInt(K_COSTUME_CURRENT));

        //同一のスプライト編集中は即時更新
        String name = mainController.getFrontStageController().getCurrentSprite().getName();
        if (sprite.getName().equals(name)) {
            mainController.getBackStageController().showCostumes(sprite);
        }
    }

    public static void sendSimpleMessage(int event, Sprite sprite) {
        if (mainController.getAgent() != null) {
            mainController.getAgent().sendNotify(getSimpleMessage(event, sprite));
        }
    }

    public static void sendAllMessage(int event, Sprite sprite) {
        if (mainController.getAgent() != null) {
            DInfo dinfo = getSimpleMessage(event, sprite);

            try {
                String tempFile = new SpriteDialog().makeFilePath("");
                List costumeList = JsonUtil.processCostumes(sprite, tempFile);
                String jsonString = JsonUtil.convertObjectToJsonString(costumeList);
                dinfo.set(K_COSTUME_LIST, jsonString);
            } catch (IOException ex) {
                Logger.getLogger(SpriteDialog.class.getName()).log(Level.SEVERE, null, ex);
            }

            mainController.getAgent().sendNotify(dinfo);
        }
    }

    private static DInfo getSimpleMessage(int event, Sprite sprite) {
        DInfo dinfo = new DInfo(D_SPRITE);

        dinfo.set(K_METHOD, event);
        dinfo.set(K_SPRITE_NAME, sprite.getName());
        dinfo.set(K_X1, (int) sprite.getX());
        dinfo.set(K_Y1, (int) sprite.getY());
        dinfo.set(K_X2, (int) sprite.getTranslateX());
        dinfo.set(K_Y2, (int) sprite.getTranslateY());
        dinfo.set(K_ROTATE, (int) sprite.getRotate());
        dinfo.set(K_SPEECH, sprite.getSpeech());
        dinfo.set(K_COSTUME_CURRENT, sprite.getCostumeNumber());
        dinfo.set(K_COLOR, Color.RED.toString());

        return dinfo;
    }
}
