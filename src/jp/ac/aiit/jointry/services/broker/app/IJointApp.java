package jp.ac.aiit.jointry.services.broker.app;

//協同編集アプリケーションインタフェース
public interface IJointApp {

    //どこのノードにいるかとかに使えそう
    static final String D_FRONT = "front";
    //役割とか動きとか イベント
    static final String KC_METHOD = "method";
    static final int VM_DUMMY = 0x000000;
    static final int VM_SELECT_SPRITE = 0x000001;
    static final int VM_MOVE_SPRITE = 0x000002;
    //位置情報
    static final String KC_X1 = "x1";
    static final String KC_Y1 = "y1";
    //色
    static final String KC_COLOR = "color";
    //ユーザ名項目
    static final String KC_USERNAME = "username";
}
