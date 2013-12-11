/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import broker.core.DInfo;
import broker.core.DialogBase;
import jp.ac.aiit.jointry.controllers.MainController;
import jp.ac.aiit.jointry.models.Sprite;

public abstract class JointryDialogBase extends DialogBase implements JointryCommon {

    protected static MainController mainController;
    protected static boolean installed = false;

    public static void install(MainController mainController) {
        if (!installed) {
            DialogBase.addDialog(JointryCommon.D_MAIN, MainDialog.class);
            DialogBase.addDialog(JointryCommon.D_SPRITE, SpriteDialog.class);
            DialogBase.addDialog(JointryCommon.D_BLOCK, BlockDialog.class);

            if (mainController != null) JointryDialogBase.mainController = mainController;
            installed = true;
        }
    }

    protected int getEvent(DInfo dinfo) {
        int event = dinfo.getInt(JointryCommon.K_METHOD);

        if (event == 0) return JointryCommon.M_DUMMY;
        return event;
    }

    protected Sprite getTargetSprite(DInfo dinfo) {
        if (mainController == null) return null;

        for (Sprite sprite : mainController.getFrontStageController().getSprites()) {
            if (sprite.getName().equals(dinfo.get(K_SPRITE_NAME))) {
                return sprite;
            }
        }

        return null;
    }

    @Override
    public void onAnswer(DInfo dinfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onNotify(DInfo dinfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onQuery(DInfo dinfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
