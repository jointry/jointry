package jp.ac.aiit.jointry.services.broker.app;

//協同編集アプリケーションインタフェース
import broker.core.Common;

public interface JointryCommon extends Common {

    //どこのノードにいるかとかに使えそう
    public static final String D_FRONT = "front";
    public static final String D_SPRITE = "sprite";

    //役割とか動きとか イベント
    public static final String KC_METHOD = "method";
    public static final int VM_DUMMY = 0x000000;
    public static final int VM_SPRITE_SELECT = 0x000001;
    public static final int VM_SPRITE_MOVE = 0x000002;
    public static final int VM_SPRITE_ADD = 0x000003;

    //位置情報
    public static final String KC_X1 = "x1";
    public static final String KC_Y1 = "y1";
    //色
    public static final String KC_COLOR = "color";
    //名前
    public static final String KC_USER_NAME = "user_name";
    public static final String KC_SPRITE_NAME = "sprite_name";
    public static final String PROXY_ID = "proxy_id";		// サーバプロキシID
}
