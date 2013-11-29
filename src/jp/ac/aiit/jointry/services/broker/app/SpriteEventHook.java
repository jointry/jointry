/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import broker.core.DInfo;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.controllers.MainController;
import jp.ac.aiit.jointry.models.Sprite;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.KC_X1;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.KC_Y1;

public class SpriteEventHook extends EventHook {

    private Sprite sprite;
    private MainController mainController;

    public SpriteEventHook(Sprite sprite) {
        this.sprite = sprite;
        this.mainController = sprite.getMainController();
    }

    @Override
    public void enableHook() {
        putHookEvent(MouseEvent.MOUSE_PRESSED, sprite.getOnMousePressed());
        putHookEvent(MouseEvent.MOUSE_DRAGGED, sprite.getOnMouseDragged());
        putHookEvent(MouseEvent.MOUSE_RELEASED, sprite.getOnMouseReleased());

        //hook event 登録
        sprite.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                callHandle(event, getHookEvent(event.getEventType()));
                sendMessage(VM_SPRITE_SELECT);
            }
        });

        sprite.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                callHandle(event, getHookEvent(event.getEventType()));
            }
        });

        sprite.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                callHandle(event, getHookEvent(event.getEventType()));
                sendMessage(VM_SPRITE_MOVE);
            }
        });
    }

    @Override
    public void sendMessage(int event) {
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
