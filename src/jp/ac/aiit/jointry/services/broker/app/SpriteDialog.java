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
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_COLOR;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_METHOD;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_SPRITE_NAME;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_X1;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_X2;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_Y1;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.K_Y2;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_SPRITE_CREATE;
import static jp.ac.aiit.jointry.services.broker.app.JointryDialogBase.mainController;

public class SpriteDialog extends JointryDialogBase {

    @Override
    public void onNotify(DInfo dinfo) {
        int event = getEvent(dinfo);

        if (event == M_SPRITE_CREATE && mainController != null) {
            final Sprite sprite = new Sprite();
            sprite.setMainController(mainController);
            sprite.setName(dinfo.get(K_SPRITE_NAME));
            sprite.setLayoutX(dinfo.getInt(K_X1));
            sprite.setLayoutY(dinfo.getInt(K_Y1));

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mainController.getFrontStageController().addSprite(sprite);
                }
            });
        }

        Sprite sprite = getTargetSprite(dinfo);
        if (sprite == null) {
            return; //該当なし
        }
        switch (event) {
            case M_SPRITE_SELECT:
                sprite.setEffect(new Shadow(4.0f, Color.valueOf(dinfo.get(K_COLOR))));
                break;

            case M_SPRITE_DRAGGED:
                sprite.setTranslateX(dinfo.getInt(K_X2));
                sprite.setTranslateY(dinfo.getInt(K_Y2));
                break;

            case M_SPRITE_RELEASD:
                sprite.setTranslateX(dinfo.getInt(K_X2));
                sprite.setTranslateY(dinfo.getInt(K_Y2));

                sprite.setEffect(null);
                break;

            default:
                break;
        }
    }

    public static void sendMessage(int event, Sprite sprite) {
        if (mainController.getAgent() != null) {
            DInfo dinfo = new DInfo(D_SPRITE);

            dinfo.set(K_METHOD, event);
            dinfo.set(K_SPRITE_NAME, sprite.getName());
            dinfo.set(K_X1, (int) sprite.getX());
            dinfo.set(K_Y1, (int) sprite.getY());
            dinfo.set(K_X2, (int) sprite.getTranslateX());
            dinfo.set(K_Y2, (int) sprite.getTranslateY());
            dinfo.set(K_COLOR, Color.RED.toString());

            mainController.getAgent().sendNotify(dinfo);
        }
    }

    public static void sendImage(String spriteName, int num, String costumeName, Image image) {
        if (mainController.getAgent() != null && image != null) {
            if (costumeName != null) {
                spriteName = spriteName + "_" + costumeName + "_" + num;
            }

            BufferedImage buf = SwingFXUtils.fromFXImage(image, null);
            mainController.getAgent().notifyViewImage(spriteName, buf);
        }
    }
}
