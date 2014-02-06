package jp.ac.aiit.jointry.services.broker.core;
import java.lang.reflect.Constructor;

/**
 * ProxyFactoryは、アプリケーションで拡張したServerProxyとClientProxyの、
 * オブジェクトをリフレクションによって生成する。
 */
public class ProxyFactory implements Common {

	private Monitor monitor;

	/**
	 * コンストラクタでは、オブジェクト生成に失敗した時のエラーメッセージの
	 * 通知先を設定する。
	 */
	public ProxyFactory(Monitor monitor) { this.monitor = monitor; }

	/**
	 * ServerProxyの拡張クラスのオブジェクトを生成する。
	 * @param hinfo 接続要求情報(HTTPメソッド、接続要求判別、サービス種別など)
	 * @return 生成したServerProxyオブジェクト
	 */
 	public ServerProxy createServerProxy(HttpInfo hinfo) {
		String fqcn = hinfo.get(PROXY_FQCN);
 		return (fqcn != null) ?
 			(ServerProxy)createProxy(fqcn,hinfo) : new ServerProxy(hinfo);
  	}

	/**
	 * ClientProxyの拡張クラスのオブジェクトを生成する。
	 * @param hinfo 接続要求情報(HTTPメソッド、接続要求判別、サービス種別など)
	 * @return 生成したClientProxyオブジェクト
	 */
	public ClientProxy createClientProxy(HttpInfo hinfo) {
		String fqcn = hinfo.get(PROXY_FQCN);
		return (fqcn != null) ?
			(ClientProxy)createProxy(fqcn,hinfo) : new ClientProxy(hinfo);
	}

	@SuppressWarnings("unchecked")
	private Proxy createProxy(String fqcn, HttpInfo hinfo) {
		try {
			Class c = Class.forName(fqcn);
			Constructor con = c.getConstructor(new Class[]{HttpInfo.class});
			return (Proxy)con.newInstance(new Object[]{hinfo});
		} catch(Exception e) {
			monitor.println("ERROR: ProxyFactory can't create a proxy: "
							+ e.getMessage());
			return null;
		}
	}
}
