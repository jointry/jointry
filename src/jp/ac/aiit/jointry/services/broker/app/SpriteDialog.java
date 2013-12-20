/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import broker.core.DInfo;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Sprite;

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
