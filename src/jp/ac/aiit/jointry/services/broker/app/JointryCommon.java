package jp.ac.aiit.jointry.services.broker.app;

//協同編集アプリケーションインタフェース
import broker.core.Common;

public interface JointryCommon extends Common {

    //どこのノードにいるかとかに使えそう
    static final String D_FRONT = "front";
    static final String D_SPRITE = "sprite";
    //役割とか動きとか イベント
    static final String KC_METHOD = "method";
    static final int VM_DUMMY = 0x000000;
    static final int VM_SELECT_SPRITE = 0x000001;
    static final int VM_MOVE_SPRITE = 0x000002;
    static final int VM_ADD_SPRITE = 0x000003;
    //位置情報
    static final String KC_X1 = "x1";
    static final String KC_Y1 = "y1";
    //色
    static final String KC_COLOR = "color";
    //名前
    static final String KC_USER_NAME = "user_name";
    static final String KC_SPRITE_NAME = "sprite_name";
    static final String PROXY_ID = "proxy_id";		// サーバプロキシID
}
