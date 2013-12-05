/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import broker.core.DInfo;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Costume;
import jp.ac.aiit.jointry.models.Sprite;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.D_SPRITE;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.KC_COLOR;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.KC_METHOD;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.KC_SPRITE_NAME;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.KC_X1;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.KC_X2;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.KC_Y1;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.KC_Y2;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.VM_SPRITE_CREATE;
import static jp.ac.aiit.jointry.services.broker.app.JointryDialogBase.mainController;

public class SpriteDialog extends JointryDialogBase {

    @Override
    public void onNotify(DInfo dinfo) {
        int event = getEvent(dinfo);

        if (event == VM_SPRITE_CREATE && mainController != null) {
            final Sprite sprite = new Sprite(mainController);

            sprite.setName(dinfo.get(KC_SPRITE_NAME));
            sprite.setLayoutX(dinfo.getInt(KC_X1));
            sprite.setLayoutY(dinfo.getInt(KC_Y1));

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mainController.getFrontStageController().addSprite(sprite);
                }
            });
        }


        Sprite sprite = getTargetSprite(dinfo);
        if (sprite == null) return; //該当なし

        switch (event) {
            case VM_SPRITE_SELECT:
                sprite.setEffect(new Shadow(4.0f, Color.valueOf(dinfo.get(KC_COLOR))));
                break;

            case VM_SPRITE_DRAGGED:
                sprite.setTranslateX(dinfo.getInt(KC_X2));
                sprite.setTranslateY(dinfo.getInt(KC_Y2));
                break;

            case VM_SPRITE_RELEASD:
                sprite.setTranslateX(dinfo.getInt(KC_X2));
                sprite.setTranslateY(dinfo.getInt(KC_Y2));

                sprite.setEffect(null);
                break;

            default:
                break;
        }
    }

    public static void sendMessage(int event, Sprite sprite) {
        if (mainController.getAgent() != null) {
            DInfo dinfo = new DInfo(D_SPRITE);

            dinfo.set(KC_METHOD, event);
            dinfo.set(KC_SPRITE_NAME, sprite.getName());
            dinfo.set(KC_X1, (int) sprite.getX());
            dinfo.set(KC_Y1, (int) sprite.getY());
            dinfo.set(KC_X2, (int) sprite.getTranslateX());
            dinfo.set(KC_Y2, (int) sprite.getTranslateY());
            dinfo.set(KC_COLOR, Color.RED.toString());

            mainController.getAgent().sendNotify(dinfo);
        }
    }

    public static void sendImage(String spriteName, int num, String costumeName, Image image) {
        if (mainController.getAgent() != null && image != null) {
            if (costumeName != null) spriteName = spriteName + "_" + costumeName+ "_" + num;

            BufferedImage buf = SwingFXUtils.fromFXImage(image, null);
            mainController.getAgent().notifyViewImage(spriteName, buf);
        }
    }
}
