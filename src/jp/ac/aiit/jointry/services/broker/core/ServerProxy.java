package jp.ac.aiit.jointry.services.broker.core;
import java.util.List;
import java.util.ArrayList;

/**
 * ServerProxyは、サーバ(サービスを提供するAgent)との間で
 * 双方向ソケット通信を行い、ClientProxyとの間でメソッド呼出しを行うことで、
 * サーバとクライアント(サービスを利用するAgent)との間の双方向通信を可能とする。
 * ServerProxyはProxyの役割に加えて以下の役割を担う。
 * <ul>
 * <li>ClientProxyとの接続と接続解除
 * <li>サーバ側Agentより受信したメッセージのProxyグループへの配信
 * <li>サーバ側Agentより受信したQUERYのProxyグループの特定メンバへの転送
 * <li>サーバ側Agentより受信したNOTIFYのProxyグループへの転送(配信中継)
 * </ul>
 * NSBrokerでは1つのQUERYの送信に対して1つのANSWERの受信を行う双方向通信
 * モデルとなっている。
 * QUERYの宛先が明記されていないサーバからのQUERYに対しては、
 * 1つのClientProxyを特定しなければならない。
 * ServerProxyではProxyグループの最初のClientProxyに転送する実装としてある。
 * この宛先決定の戦略はhandleQA(dinfo)メソッドをオーバライドすることによって
 * 変更することが可能である。
 * 
 * @see Broker
 * @see ClientProxy
 * @see Agent
 * @see DInfo
 */

public class ServerProxy extends Proxy {

	/** このServerProxyに接続中のClientProxyのリスト */
	private List<ClientProxy> cpList = new ArrayList<ClientProxy>();

	/**
	 * このServerProxyに接続中のClientProxyのリストを返す。
	 */
	List<ClientProxy> cpList() { return cpList; }

	synchronized private ClientProxy[] cpArray() {
		return cpList.toArray(new ClientProxy[cpList.size()]);
	}

	synchronized List<Proxy> proxyList() {
		List<Proxy> pl = new ArrayList<Proxy>();
		for(Proxy proxy : cpList) pl.add(proxy);
		return pl;
	}

	public ServerProxy(HttpInfo hinfo) { super(hinfo); }

	@Override public char mark() { return 'S'; }
	@Override public boolean isServer() { return true; }

	/**
	 * サービス要求に対応できるか判定：
	 * @param hinfo HTTPリクエスト情報
	 * @return サービス提供可能ならばtrue
	 */
	public boolean canServeTo(HttpInfo hinfo) {
		return httpInfo.match(SERVICE_ID, hinfo);
	}

	/**
	 * ClientProxyを登録し関係を確立する。
	 * @param cp 登録するClientProxy
	 */
	synchronized public void addClient(ClientProxy cp) {
		cpList.add(cp);
		cp.setServer(this);
	}
	/**
	 * ClientProxyとの関係を解消する。
	 * @param cp 関係を解消するClientProxy
	 */
	synchronized void removeClient(ClientProxy cp) {
		cpList.remove(cp);
	}

	/**
	 * ClientProxyとの接続の解消とServerAgentとの間のソケットのクローズ
	 */
	@Override synchronized public void close() {
		for(ClientProxy cp : cpArray()) cp.disconnect();
		super.close();
	}

	/**
	 * ServerAgentからのメッセージの受信処理
	 * @param message 受信メッセージ
	 */
	@Override synchronized protected void onMessage(String message) {
		multicast(httpInfo.get(USER_ID) + ": " +  message);
	}

	/**
	 * ServerAgentより受信したメッセージのClientProxy群への配信
	 * @param message 配信メッセージ
	 */
	synchronized public void multicast(String message) {
		sendPlain(message);
		for(ClientProxy cp : cpArray()) cp.sendPlain(message);
	}

	/**
	 * ServerAgentから送信されたQUERYの受信処理を行う。
	 * 宛先が未定義の場合は、宛先決定ポリシーに従って宛先（K_TOの内容）を決める。
	 * 宛先決定後の処理は、上位クラスのメソッドと同じ。
	 * <p>
	 * 宛先決定の戦略はProxyグループの最初のClientProxyとしてあるが、
	 * 本メソッドをオーバライドすることによって変更することが可能である。
	 * @param dinfo 送信するQUERY情報
	 */
	@Override protected void handleQA(DInfo dinfo) {
		if(!dinfo.has(K_TO) && cpList.size() > 0) {
			// 宛先決定メソッドで宛先を決める。★要検討
			Proxy pi = cpList.get(0); // とりあえず最初のメンバーとする
			if(pi != null) dinfo.set(K_TO, pi.getID());
		}
		super.handleQA(dinfo);
	}

	/**
	 * ServerAgentから送信された（または他のProxyから転送された）NOTIFYの
	 * 受信処理を行う。
	 * <p>
	 * 複数の宛先（K_TO）を指定できる。宛先の指定がない場合は、配下の
	 * ClientProxy全部が宛先となる。"0"が含まれている場合は自らも受信処理する。
	 * @param dinfo 受信したNOTIFY情報
	 */
	@Override protected void notifyReceived(DInfo dinfo) {
		dinfo.isEndPoint = false;
		super.notifyReceived(dinfo);

		List<Proxy> destList = broker.selectProxies(dinfo.getArray(K_TO));
		if(destList == null) destList = proxyList();
		for(Proxy proxy : destList)
			proxy.relayMessage(dinfo, M_NOTIFY);
	}
}
