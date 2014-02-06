package jp.ac.aiit.jointry.services.broker.core;

import jp.ac.aiit.jointry.services.broker.core.DInfo;
import jp.ac.aiit.jointry.services.broker.core.HttpBase;
import jp.ac.aiit.jointry.services.broker.core.Evaluator;
import jp.ac.aiit.jointry.services.broker.core.DialogBase;
import jp.ac.aiit.jointry.services.broker.core.Monitor;
import jp.ac.aiit.jointry.services.broker.core.URLInfo;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import jp.ac.aiit.jointry.services.broker.util.Util;

/**
 * Agentは、Broker側の対応するProxyとの間で双方向のSocket通信を行うために 以下の機能を提供している。
 * <ul>
 * <li>	BrokerのProxyとの間でのソケット通信の接続の確立と解消
 * <li>	Brokerのシステムパラメータの問合せや設定
 * <li>	交信先の宛名リストの管理
 * <li>	メッセージの受信の開始準備
 * <li>	宛先を指定したNOTIFYの送信
 * </ul>
 *
 * Agentの使用者は、HttpBaseから継承した以下のメソッドを利用して、
 * メッセージ送信、同期呼出し、QUERY送信、NOTIFY送信、テキスト送信、画像送信、 画像ファイル送信などを行うことができる。
 * <ul>
 * <li>	public void sendQuery(DInfo dinfo)
 * <li>	public void sendAnswer(DInfo dinfo, String result)
 * <li>	public void sendNotify(DInfo dinfo)
 * <li>	public void sendPlain(String message)
 * <li>	public void sendMessage(String message)
 * <li>	public boolean notifyViewText(String title, String text)
 * <li>	public void sendText(String text);
 * <li>	public boolean notifyViewImage(String title, BufferedImage bimage)
 * <li>	public void viewImageFile(String fname)
 * <li>	public void sendBinary(String fname)
 * <li>	public void setInvokeTimeout(int time)
 * <li>	public DInfo invoke(String query)
 * <li>	public DInfo invoke(DInfo dinfo)
 * <li>	public boolean evalQuery(String query)
 * <li>	public boolean evalNotify(String invoice)
 * </ul>
 *
 * Agentの親クラスHttpBaseで定義された以下のメソッドをオーバライドすることに よって、各種のメッセージの受信時の処理を拡張することができる。
 * <ul>
 * <li>	protected void notifyReceived(DInfo dinfo);
 * <li>	protected void onCommand(String command);
 * <li>	protected void onMessage(String message);
 * <li>	protected void onClose();
 * </ul>
 *
 * @see Monitor
 * @see DInfo
 * @see Proxy
 * @see ServerProxy
 * @see ClientProxy
 */
public class Agent extends HttpBase implements Evaluator {

    static final String DEFAULT_SERVICE = CHAT_SERVICE;
    static final String GUEST = "Guest";
    static final String GUESTPW = "*";

    public Agent() {
    }

    public Agent(Monitor monitor) {
        setMonitor(monitor);
    }

    private char roleMark = 'u';

    private void setRoleMark(String role) {
        roleMark = SERVER.equals(role) ? 's' : CLIENT.equals(role) ? 'c' : 'u';
    }

    @Override
    public char mark() {
        return roleMark;
    }

    @Override
    public boolean isAgent() {
        return true;
    }

    @Override
    public String continuousDialogID() {
        return getProp(K_PROXY);
    }

    protected String welcomeMessage = "Welcome to Network Service Broker.";

    /**
     * Agentを生成し要求条件を満たすProxyに接続する。
     * <pre>
     * agentapp <url> [<user-role> [<user-id> <password> [<proxy-fqcn>]]]
     * </pre>
     *
     * @param args パラメータ配列（コマンドラインの並び順）
     * @param serviceID サービスID(get_service/chat_service/canvas_draw)
     * @return 生成したAgent
     */
    public static Agent open(String[] args, String serviceID) {
        String url = args[0];
        String role = (args.length > 1) ? args[1] : UNKNOWN;
        String userId = (args.length > 2) ? args[2] : GUEST;
        String password = (args.length > 3) ? args[3] : GUESTPW;
        String proxyFQCN = (args.length > 4) ? args[4] : null;
        Agent agent = new Agent();
        return agent.open(url, serviceID, role, userId, password, proxyFQCN)
               ? agent : null;
    }

    public static Agent openAsClient(String urlpath,
                                     String userId, String password) {
        return open(urlpath, CLIENT, userId, password);
    }

    public static Agent open(String urlpath, String role,
                             String userId, String password) {
        Agent agent = new Agent();
        return agent.open(urlpath, DEFAULT_SERVICE, role, userId, password, null)
               ? agent : null;
    }

    /**
     * Brokerを介してProxyに接続し、双方向通信開始の準備を行う。
     *
     * @param urlpath BrokerのURL
     * @param serviceId サービスID(get_service/chat_service/canvas_draw)
     * @param role ユーザ種別(server/client/unknown)
     * @param userId ユーザID
     * @param password パスワード
     * @param proxyFQCN ProxyのFQCN
     * @return 接続に成功したら true、失敗したら false
     */
    public boolean open(String urlpath, String serviceId, String role,
                        String userId, String password, String proxyFQCN) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(USER_ROLE, role);
        paramMap.put(SERVICE_ID, serviceId);
        paramMap.put(USER_ID, userId);
        paramMap.put(PASSWORD, password);
        if (proxyFQCN != null) {
            paramMap.put(PROXY_FQCN, proxyFQCN);
        }
        return open(urlpath, paramMap);
    }

    /**
     * Brokerを介してProxyに接続し、双方向通信開始の準備を行う。
     *
     * @param urlpath BrokerのURL
     * @param paramMap パラメータのマップ（キー・バリュー対の集合）
     * @return 接続に成功したら true、失敗したら false
     */
    public boolean open(String urlpath, Map<String, String> paramMap) {
        try {
            // Agent用の作業ファイル格納ディレクトリのパス名を生成する。
            workRoot += "/agents/" + paramMap.get(USER_ID);

            // Broker側のProxyとの接続を確立する。
            for (int i = 1; i <= 5; i++) {
                URLInfo u = new URLInfo(urlpath);
                setSocketWith(u);
                sendRequest(HTTP_POST, paramMap, u);
                List<String> res = receiveHTTP(RESPONSE_TIMEOUT, "response");
                if (res.get(0).equals(HTTP200)) {
                    println(theClass() + ": " + welcomeMessage);
                    setRoleMark(paramMap.get(USER_ROLE));
                    return true;
                }
                printError("Agent #" + i + ": " + res.get(0));
                Util.sleep(100 * i);
            }
            throw new IOException("request refused");

        } catch (Exception e) {
            // MalformedURLException, UnknownHostException, IOException
            socket = null;
            printError("Agent", e);
            return false;
        }
    }

    /**
     * ダイアログを管理下に置く。
     *
     * @param dialog
     */
    public void manage(DialogBase dialog) {
        dialog.takeOver(this);
    }

    /**
     * Proxyからのメッセージ受付を開始する。 オープニングメッセージを表示し、受信待ち状態になる。
     *
     * @param timeout メッセージ受信待ちの打ち切り時間
     */
    public void startListening(int timeout) {
        try {
            socket.setSoTimeout(timeout);
            start();
            println(theClass() + ": waiting for messages from the proxy.");
        } catch (SocketException e) {
            printError("Agent", e);
        }
    }

    /**
     * Proxyとの接続を解除する。
     */
    @Override
    public void close() {
        println(theClass() + ": closed.");
        super.close();
    }

    /**
     * Proxyからの交信終了の通知を受け呼び出される。 Agent側ではソケットを利用不可状態としモニタに終了の通知を出す。
     */
    @Override
    protected void onClose() {
        println(theClass() + ": terminated.");
        socket = null;
        monitor.onClose();
    }

    /**
     * 受信したメッセージをモニタに表示する。
     *
     * @param message メッセージ行
     */
    @Override
    protected void onMessage(String message) {
        println(message);
    }

    /**
     * Brokerのシステムパラメータを問合せる。
     *
     * @param keys 問合せを行いたい任意個のキー
     * @return 問合せの結果が格納されたダイアログ情報
     */
    public DInfo query(String... keys) {
        DInfo dinfo = new DInfo(D_SYSTEM);
        for (String key : keys) {
            dinfo.set(key, null);
        }
        return invoke(dinfo);
    }

    /**
     * Brokerのシステムパラメータを取得する。
     *
     * @param key 取得したいパラメータのキー
     * @return パラメータの値
     */
    public String getProp(String key) {
        DInfo dinfo = new DInfo(D_SYSTEM);
        dinfo.set(key, null);
        dinfo = invoke(dinfo);
        return (dinfo != null) ? dinfo.get(key) : null;
    }

    /**
     * Brokerのシステムパラメータを設定する。
     *
     * @param key 設定したいパラメータのキー
     * @param value 設定したいパラメータの値
     */
    public void setProp(String key, String value) {
        DInfo dinfo = new DInfo(D_SYSTEM);
        dinfo.set(key, value);
        invoke(dinfo);
    }

    /**
     * 宛先を管理するリスト
     */
    private List<String> destList = new ArrayList<String>();
    //{ destList.add("1"); destList.add("2"); destList.add("3"); }

    /**
     * 宛先を管理するリストを返す。
     */
    public List<String> getDestinationList() {
        return destList;
    }

    /**
     * 宛先を管理するリストからすべての宛先を削除する。
     */
    public void clearDestinationList() {
        destList.clear();
    }

    /**
     * 宛先を管理するリストを同一グループのメンバーで初期化する。
     */
    public void initDestinationList() {
        DInfo d = query(K_PROXY, K_SERVER, K_MEMBERS);
        if (d == null) {
            printError("Agent#initDestinationList() failed!");
            return;
        }
        String proxyID = d.get(K_PROXY);
        String serverProxyID = d.get(K_SERVER);
        String[] members = d.get(K_MEMBERS).split(":");
        destList.clear();
        if (!proxyID.equals(serverProxyID)) {
            destList.add(serverProxyID);
        }
        for (String cp : members) {
            if (!cp.equals(proxyID)) {
                destList.add(cp);
            }
        }
    }

    /**
     * Proxyを追加する。
     *
     * @param proxyID 追加するProxyの識別子
     */
    public void addDestination(String proxyID) {
        destList.add(proxyID);
    }

    /**
     * Proxyを削除する。
     *
     * @param proxyID 削除するProxyの識別子
     */
    public void removeDestination(String proxyID) {
        destList.remove(proxyID);
    }

    /**
     * 送信プロパティ @to に配布先の宛先を設定し、NOTIFYを送信する。 すでに明示的に宛先(@to)が指定されていたらそれを採用する。
     * Agentの宛先管理リストが定義されていたらその内容を@toに設定する。
     * それ以外の場合は未定義とし、Broker側（Proxy/ClientProxy/ServerProxy）の 配布ポリシーに従う。
     *
     * @param dinfo 送信するNOTIFY情報
     */
    @Override
    public void notifyD(DInfo dinfo) {
        if (!dinfo.has(K_TO) && destList.size() > 0) {
            dinfo.set(K_TO, makeToParam());
        }
        super.notifyD(dinfo);
    }

    /**
     * 宛先管理リストから宛先パラメータを作成する。
     */
    private String makeToParam() {
        String[] members = destList.toArray(new String[destList.size()]);
        return Util.join(":", (Object[]) members);
    }

    /**
     * 設定・参照の指示をもとに宛先管理リストを操作する。
     * <p>
     * 設定・参照の指示は以下の書式をもつ:
     * <pre>
     *  空       --- 宛先リストを参照する
     *  clear    --- 宛先リストを空にする
     *  init     --- 同一グループのメンバーで初期化する
     *  id:...   --- 指定プロキシリストで初期化する
     *  +id:...  --- プロキシリストを追加する
     *  -id:...  --- プロキシリストを削除する
     * </pre>
     *
     * @param spec 設定・参照の指示
     */
    private void evalDest(String param) {
        if (param != null) {
            char head = param.charAt(0);
            if (param.equals("clear")) {
                destList.clear();
            } else if (param.equals("init")) {
                initDestinationList();
            } else {
                String[] pids = param.substring(1).split(":");
                if (head != '+' && head != '-') {
                    destList.clear();
                }
                for (String pid : pids) {
                    switch (head) {
                        case '-':
                            removeDestination(pid);
                            break;
                        default:
                            addDestination(pid);
                            break;
                    }
                }
            }
        }
        println(makeToParam());
    }

    /**
     * プロパティのヘルプメッセージを表示する。
     */
    @Override
    public void printPropHelp() {
        println("  :timeout [msec] ------ invoke timeoutの参照／設定");
        println("  :timelag    ---------- 時刻の差分");
        println("  :sync       ---------- 時刻の補正");
        println("  :dest [clear|init] --- 宛先の参照・クリア・初期化");
        println("  :dest [+|-]pid:... --- 宛先の設定・追加・削除");
    }

    /**
     * プロパティアクセッサを評価する。
     *
     * @param tt プロパティアクセッサのトークン列
     * @return 構文不正の場合はfalse
     */
    @Override
    public boolean evalProp(String[] tt) {
        if (tt.length == 0) {
            return false;
        }
        String tag = tt[0];

        if (tag.equals("timeout")) {
            if (tt.length == 2) {
                setInvokeTimeout(Util.getInt(tt[1]));
            }
            println(getInvokeTimeout() + " msec");

        } else if (tag.equals("timelag")) {
            println("" + getTimelag());

        } else if (tag.equals("sync")) {
            syncMyTime();

        } else if (tag.equals("dest")) {
            evalDest(tt.length == 1 ? null : tt[1]);

        } else {
            return false;
        }
        return true;
    }

}
