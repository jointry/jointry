package jp.ac.aiit.jointry.services.broker.app;

//協同編集アプリケーションインタフェース
import jp.ac.aiit.jointry.services.broker.core.Common;

public interface JointryCommon extends Common {

    //どこのノードにいるかとかに使えそう
    public static final String D_MAIN = "main";
    public static final String D_SPRITE = "sprite";
    public static final String D_BLOCK = "block";
    //役割とか動きとか イベント
    public static final String K_METHOD = "method";
    public static final int M_DUMMY = 0x000000;
    public static final int M_SPRITE_CREATE = 0x000001;
    public static final int M_SPRITE_SELECT = 0x000002;
    public static final int M_SPRITE_DRAGGED = 0x000003;
    public static final int M_SPRITE_RELEASD = 0x000004;
    public static final int M_SPRITE_CHANGED = 0x000005;
    public static final int M_COSTUME_SYNC = 0x000006;
    public static final int M_BLOCK_CREATE = 0x000101;
    public static final int M_BLOCK_VARIABLE_CREATE = 0x000102;
    public static final int M_BLOCK_MOVE = 0x000103;
    public static final int M_BLOCK_REMOVE = 0x000104;
    public static final int M_BLOCK_ADDLINK = 0x000105;
    public static final int M_BLOCK_ADDCHILD = 0x000106;
    public static final int M_BLOCK_ADDVARIABLE = 0x000107;
    public static final int M_BLOCK_ADDEMBRYO = 0x000108;
    public static final int M_BLOCK_CHANGE_STATE = 0x000109;
    public static final int M_MAIN_REQUEST = 0x001001;
    public static final int M_MAIN_SYNCHRONIZE = 0x001002;
    public static final int M_MAIN_CONNECT = 0x001003;
    public static final int M_MAIN_DISCONNECT = 0x001004;
    public static final int M_MAIN_MEMBERS = 0x001005;
    //位置情報
    public static final String K_X1 = "x1";
    public static final String K_Y1 = "y1";
    public static final String K_X2 = "x2";
    public static final String K_Y2 = "y2";
    public static final String K_ROTATE = "rotate";
    public static final String K_SPEECH = "speech";
    public static final String K_COSTUME_CURRENT = "costume_current";
    public static final String K_VALUE_POS = "value_pos";
    public static final String K_VALUE = "value";
    public static final String K_LEFT_VALUE = "left_value";
    public static final String K_RIGHT_VALUE = "right_value";
    //色
    public static final String K_COLOR = "color";
    //名前
    public static final String K_USER_NAME = "user_name";
    public static final String K_USER_NAME_LIST = "user_name_list";
    public static final String K_SPRITE_NAME = "sprite_name";
    public static final String K_BLOCK_CLASS_NAME = "block_class";
    public static final String K_BLOCK_LABEL_NAME = "block_label";
    public static final String K_BLOCK_ID = "block_id";
    public static final String K_PREV_BLOCK_ID = "prev_block_id";
    public static final String K_PARENT_BLOCK_ID = "parent_block_id";
    //その他
    public static final String K_MAIN_INFO = "main_info";
    public static final String K_COSTUME_LIST = "costume_list";
    public static final String K_BLOCK_STATUS = "block_status";
    public static final String PROXY_ID = "proxy_id"; // サーバプロキシID
    /**
     * ルーム状況を取得するためのダミーネーム
     */
    public static final String DUMMY_AGENT_NAME = "dummyAgent";
}
