package jp.ac.aiit.jointry.services.broker.core;
import java.util.List;

/**
 * ClientProxyは、クライアント(サービスを利用するAgent)との間で
 * 双方向ソケット通信を行い、ServerProxyとの間でメソッド呼び出しを行うことで、
 * クライアントとサーバ(サービスを提供するAgent)との間の双方向通信を可能とする。
 * ClientProxyはProxyの役割に加えて以下の役割を担う。
 * <ul>
 * <li>ServerProxyとの接続と接続解除
 * <li>クライアント側Agentより受信したメッセージのProxyグループへの配信
 * <li>クライアント側Agentより受信したQUERYのServerProxyへの転送(配信中継)
 * <li>クライアント側Agentより受信したNOTIFYのProxyグループの多メンバへの転送
 * </ul>
 * 
 * @see Broker
 * @see ServerProxy
 * @see Agent
 * @see DInfo
 */
public class ClientProxy extends Proxy {
	public ClientProxy(HttpInfo hinfo) { super(hinfo); }

	private ServerProxy serverProxy;
	/**
	 * ServerProxyとの接続を確立する。
	 * @param serverProxy 接続先のServerProxy
	 */
	void setServer(ServerProxy serverProxy) { this.serverProxy = serverProxy; }

	@Override public char mark() { return 'C'; }
	@Override public boolean isClient() { return true; }

	/**
	 * 接続しているServerProxyを返す。
	 * @return 接続しているServerProxy
	 */
	ServerProxy getServerProxy() { return serverProxy; }

	/**
	 * ServerProxyとの接続解消し、クライアント側Agent間のソケットをクローズする。
	 */
	@Override public void close() {
		serverProxy.removeClient(this);
		super.close();
	}
	/**
	 * クライアント側Agentとの間のソケットのクローズ
	 */
	void disconnect() {
		super.close();
	}

	/**
	 * クライアント側Agentが送信したメッセージを受信し、発信者のuserIDを先頭に
	 * 添えて、Proxyグループの全メンバーへ配信する。


	 * @param message 受信したメッセージ
	 */
	@Override protected void onMessage(String message) {
		serverProxy.multicast(httpInfo.get(USER_ID) + ": " +  message);
	}

	/**
	 * クライアント側Agentが送信したQUERYを受信し、
	 * K_TO の指定に従って宛先のProxyを特定し転送処理を行う。
	 * 宛先が未定義の場合はServerProxyを宛先とする。
	 * <p>
	 * このメソッドをオーバライドすることによってQUERYの転送先を制御できる。
	 * @param dinfo 受信したQUERY情報
	 */
	@Override protected void handleQA(DInfo dinfo) {
		if(!dinfo.has(K_TO)) {
			dinfo.set(K_TO, serverProxy.getID());
		}
		super.handleQA(dinfo);
	}

	/**
	 * クライアント側Agentが送信したNOTIFYを受信し、K_TO の宛先指定に従って
	 * 他のProxyに転送する。
	 * 宛先の指定が無い場合は、Proxyグループの自分以外のメンバに転送する。
	 * <p>
	 * このメソッドをオーバライドすることによってNOTIFYの転送先を制御できる。
	 * @param dinfo 受信したNOTIFY情報
	 */
	@Override protected void notifyReceived(DInfo dinfo) {
		// ClientProxyとしてNOTIFYを受信する。
		dinfo.isEndPoint = false;
		super.notifyReceived(dinfo);

		// Brokerに接続しているProxyから転送先を特定する。
		List<Proxy> destList = broker.selectProxies(dinfo.getArray(K_TO));
		if(destList == null) {
			destList = serverProxy.proxyList();
			destList.remove(this);
			destList.add(serverProxy);
		}
		// 転送先のProxy群にNOTIFYを転送転送する。
		for(Proxy proxy : destList)
			proxy.relayMessage(dinfo, M_NOTIFY);
	}
}
