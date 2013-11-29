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
            case VM_SPRITE_SELECT:
                sprite.setEffect(new Shadow(4.0f, Color.valueOf(dinfo.get(KC_COLOR))));
                break;

            case VM_SPRITE_MOVE:
                sprite.setTranslateX(dinfo.getInt(KC_X1));
                sprite.setTranslateY(dinfo.getInt(KC_Y1));

                sprite.setEffect(null);
                break;

            default:
                break;
        }
    }
}
