/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import broker.core.DInfo;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Sprite;

public class SpriteDialog extends JointryDialogBase {

    @Override
    public void onNotify(DInfo dinfo) {
        Sprite sprite = getTargetSprite(dinfo);
        if (sprite == null) return; //該当なし

        switch (getEvent(dinfo)) {
            case VM_SPRITE_CREATE:
                break;

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
}
