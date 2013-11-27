/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import broker.core.DInfo;
import broker.core.DialogBase;
import java.util.ArrayList;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Sprite;

public class SpriteDialog extends DialogBase implements JointryCommon {

    private static ArrayList<Sprite> sprites = new ArrayList();
    private static boolean installed = false;

    public static void putSprite(Sprite sprite) {
        sprites.add(sprite);
    }

    public static void Install() {
        if (!installed) {
            DialogBase.addDialog(JointryCommon.D_SPRITE, SpriteDialog.class);
            installed = true;
        }
    }

    @Override
    public void onAnswer(DInfo dinfo) {
    }

    @Override
    public void onNotify(DInfo dinfo) {
        Sprite sprite = getTargetSprite(dinfo);
        if (sprite == null) return; //該当なし

        switch (getEvent(dinfo)) {
            case VM_SELECT_SPRITE:
                sprite.setEffect(new Shadow(4.0f, Color.valueOf(dinfo.get(KC_COLOR))));
                break;

            case VM_MOVE_SPRITE:
                sprite.setTranslateX(dinfo.getInt(KC_X1));
                sprite.setTranslateY(dinfo.getInt(KC_Y1));

                sprite.setEffect(null);
                break;

            default:
                break;
        }
    }

    @Override
    public void onQuery(DInfo dinfo) {
    }

    private int getEvent(DInfo dinfo) {
        int event = dinfo.getInt(JointryCommon.KC_METHOD);

        if (event == 0) return JointryCommon.VM_DUMMY;
        return event;
    }

    private Sprite getTargetSprite(DInfo dinfo) {
        for (Sprite sprite : sprites) {
            if (sprite.getName().equals(dinfo.get(KC_SPRITE_NAME))) {
                return sprite;
            }
        }

        return null;
    }
}
