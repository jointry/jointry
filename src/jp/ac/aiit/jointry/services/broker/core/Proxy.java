package jp.ac.aiit.jointry.services.broker.core;

import java.io.IOException;
import java.io.BufferedReader;
import java.util.List;
import jp.ac.aiit.jointry.services.broker.core.Property.Counter;

/**
 * Proxyは、Broker側で Agentとの双方向通信を処理する。 Proxyは以下の役割を担う。
 * <ul>
 * <li>接続情報や実行監視情報の管理
 * <li>Agentから送信されたQUERYの受信と転送処理
 * <li>Agentから送信されたANSWERの受信と転送処理
 * <li>Agentから送信されたNOTIFYの受信と転送処理
 * <li>Proxy間での各種メッセージの転送処理
 * <li>Broker側のShellコマンドの実行と結果の返信
 * <li>Agentから送信されたメッセージの全Proxyへの配信
 * </ul>
 * メッセージの宛先はAgentが@toプロパティで明示的に指定することができる。
 * 省略した場合はProxyグループの情報を元にProxyまたはその拡張クラスが メッセージ配送ポリシーに従って配送先を特定する。
 * 配送方法は、メッセージの発信元種別（クライアント/サーバ）と メッセージ種別(QUERY/NOTIFY/COMMAND/PLAIN)によって決まる。
 * <pre>
 * ------------------------------------------------
 * メッセージ種別   クライアント送信    サーバ送信
 * ================================================
 *      QUERY       @to=SP              @to=Ci
 *      ANSWER      @fromへ返信         @fromへ返信
 *      NOTIFY      @to=CP*-CPi+SP      @to=CP*
 *      COMMAND     @to=SP              @to=Ci
 *      PLAIN       @to=CP*+SP          @to=CP*
 * ------------------------------------------------
 * SP: ServerProxy,  CPi: ClientProxyの１つ,
 * CP*:全てのClientProxy
 * </pre>
 * <p>
 * Proxyは各種メッセージの転送処理を行うが、受信側で発信元を特定できるように、
 * ダイアログ情報の@fromプロパティに発信元のProxy(発信元のAgentに対応するProxy) のproxyIDを転送の際に追加している。
 *
 * @see Broker
 * @see Agent
 * @see DInfo
 * @see HttpInfo
 */
public class Proxy extends HttpBase {

    private static Counter counter = new Counter(K_NEXT_PROXY, 1);
    protected Broker broker = null;		// このProxyを保持するBroker
    private int proxyID = counter.nextID();	// Broker起動期間中のProxyの一意識別子
    protected HttpInfo httpInfo;		// 接続情報（USER_ID, PASSWORD, など）
    private boolean monitoring = false;	// Agent間の交信を監視する場合はtrue

    /**
     * 次に生成するProxyのproxyIDの参照または設定を行う。
     *
     * @param dinfo ダイアログ情報
     */
    public static boolean accessNextID(DInfo dinfo) {
        return counter.access(dinfo);
    }

    /**
     * Agent間の交信を監視するか否かを設定する。
     *
     * @param monitoring 監視する場合はtrue
     */
    public void setMonitoring(boolean monitoring) {
        this.monitoring = monitoring;
    }

    /**
     * ProxyのコンストラクタはBrokerから呼ばれる。
     *
     * @param hinfo 接続要求情報
     */
    public Proxy(HttpInfo hinfo) {
        this.httpInfo = hinfo;
        // Proxy用の作業ファイル格納ディレクトリのパス名を生成する。
        workRoot += "/proxies/" + hinfo.get(USER_ID);
    }

    /**
     * Proxy の一意識別子を取得する。
     */
    public int getID() {
        return proxyID;
    }

    /**
     * proxyIDの文字列表現を持続的ダイアログのIDとして返す。
     */
    @Override
    protected String continuousDialogID() {
        return "" + proxyID;
    }

    /**
     * このProxy のユーザ名を得る。
     */
    public String getUserID() {
        return httpInfo.get(USER_ID, UNKNOWN_USER);
    }

    /**
     * Proxy を Broker に接続する。
     */
    public void setBroker(Broker broker) {
        this.broker = broker;
        broker.login(httpInfo.get(USER_ID));
    }

    /**
     * 接続している Brokerを返す。
     */
    public Broker getBroker() {
        return broker;
    }

    @Override
    public char mark() {
        return 'P';
    }

    @Override
    public boolean isProxy() {
        return true;
    }

    public boolean isServer() {
        return false;
    }

    public boolean isClient() {
        return false;
    }

    /**
     * 文字列表現は、クラス名、proxyID、userIDを含む。
     */
    @Override
    public String toString() {
        return theClass() + "[" + proxyID + ". " + httpInfo.get(USER_ID) + "]";
    }

    /**
     * Agentから送信されたQUERYの受信処理を行う。 K_TOプロパティに従って宛先のProxyを特定し転送する。
     * 宛先不定の場合は自分でQUERYの受信処理を行う
     *
     * @param dinfo 受信したQUERY情報
     */
    @Override
    protected void handleQA(DInfo dinfo) {
        if (dinfo.weakMatch(K_TO, "0")) {
            // @toが未定義または @to=0 ならば、このProxy内で処理する。
            super.handleQA(dinfo);
        } else {
            dinfo.set(K_FROM, proxyID);		// ANSWERメッセージの転送先となる。
            // @to=i ならば、Piへ M_QUERYを転送する。
            Proxy pi = broker.getProxy(dinfo.getInt(K_TO));
            if (pi != null) {
                dinfo.define(K_RELAY);		// QUERYメッセージの中継送信
                pi.relayMessage(dinfo, M_QUERY);
            } else {
                sendAnswer(dinfo, V_NG);
                printError("Answerer can't be identified: @to=" + dinfo.get(K_TO));
            }
        }
    }

    /**
     * Agentから送信されたANSWERの受信処理を行う。 K_FROMの内容に従って返信先を特定し、転送処理を行う。
     *
     * @param dinfo 受信したANSWER情報
     */
    @Override
    protected void answerReceived(DInfo dinfo) {
        if (!dinfo.has(K_RELAY)) { // ★dinfo.isEndPoint にすべき？
            // 中継メッセージでなければ、このProxy内で処理する。
            dinfo.viewable = true;
            super.answerReceived(dinfo);	// ANSWERを受信する
        } else {
            // 中継メッセージの場合、QUERY発信者へANSWERを転送する。
            dinfo.viewable = monitoring;
            super.answerReceived(dinfo);	// ANSWERを受信する
            dinfo.remove(K_RELAY);			// 中継フラグを削除する
            // @from=i ならば、Piへ M_ANSWERを転送する。
            Proxy pi = broker.getProxy(dinfo.getInt(K_FROM));
            if (pi != null) {
                pi.relayMessage(dinfo, M_ANSWER);
            } else {
                // ANSWERが返信される前に、Proxyが終了してしまった！
                printError("Questioner not found: @from=" + dinfo.get(K_FROM));
            }
        }
    }

    /**
     * Agentから送信されたNOTIFYの受信処理を行う。
     *
     * @param dinfo 受信したNOTIFY情報
     */
    @Override
    protected void notifyReceived(DInfo dinfo) {
        // 監視中か宛先がBrokerならばモニタ表示を有効とする。
        dinfo.viewable = monitoring || (this instanceof Proxy);
        dinfo.set(K_FROM, proxyID);			// NOTIFYの発信元
        super.notifyReceived(dinfo);
    }

    /**
     * 指定されたタイプのメッセージをAgentへ転送する。
     *
     * @param dinfo 転送すべきNOTIFY情報
     * @param type メッセージ種別(M_QUERY/M_ANSWER/M_NOTIFY)
     * @see ClientProxy
     * @see ServerProxy
     */
    protected void relayMessage(DInfo dinfo, char type) {
        assert timestamp(dinfo, sign(type));
        sendMessage(type + dinfo.message());
        dinfo.sendContent(sender);
    }

    private String sign(char type) {
        return (type == M_QUERY) ? "Q=" : (type == M_ANSWER) ? "A=" : "N=";
    }

    /**
     * 受信したShellコマンドをBroker側で実行し、その結果を返信する。
     *
     * @param command Shellコマンド
     */
    @Override
    protected void onCommand(String command) {
        println(getUserID() + "> " + command);
        sendText(broker.evalShell(command));
    }

    /**
     * 受信したメッセージにユーザIDを付加したメッセージを Brokerに接続中の全てのProxyに配信する。
     */
    @Override
    protected void onMessage(String message) {
        broker.broadcast(httpInfo.get(USER_ID) + ": " + message);
    }

    /**
     * Agentからの交信終了の通知を受け呼び出される。 Proxy側ではソケットをクローズしBrokerとの接続を解消する。
     */
    @Override
    protected void onClose() {
        close();
        if (broker != null) {
            broker.removeProxy(this);
        }
        broker.logout(httpInfo.get(USER_ID));
    }
}
