package jp.ac.aiit.jointry.services.broker.core;
import java.util.List;

/**
 * SysDialogは、Brokerのシステムパラメータを、ProxyからAgentへ通知する機能と
 * Agentからの問合せに対してProxyが回答する機能を提供する。
 * <p>以下のシステムパラメータに対応している。
 * <ul>
 * <li>@proxy_no: 稼動しているProxyの個数
 * <li>@proxy: 対応する（サーバまたはクライアントの）proxyID
 * <li>@server: 対応するServerProxyのproxyID
 * <li>@members: 同一ServerProxyに接続されたClientProxyのproxyIDリスト
 * <li>@proxy_info: 稼動しているProxyのproxyIDとuserID対のリスト
 * <li>@server_info: 稼動しているServerProxyのproxyIDとuserID対のリスト
 * <li>@member_info: 同一ServerProxyに接続されたClientProxyの
 * 					proxyIDとuserID対のリスト
 * </ul>
 * 複数の要素を含むリストは要素分離文字`:'で連結された文字列として表現される。
 * <pre>
 * system @proxy_no @proxy @server @members @member_info @proxy_info
 * </pre>
 */

public class SysDialog extends DialogBase {

	@Override public boolean query(DInfo dinfo) {		// Agent -> Proxy
		if(owner.isAgent()) {
			dinfo.set(K_TO, 0);
			sendQuery(dinfo);
			return true;
		} else {
			printError("Proxy can't send query: " + dinfo.message());
			return false;
		}
	}

	@Override public void onQuery(DInfo dinfo) {		// Proxy -> Agent
		if(owner.isProxy()) {
			if(accessParams(dinfo)) {
				sendAnswer(dinfo, V_OK);
			} else {
				sendAnswer(dinfo, V_NG);
			}
		} else {
			sendAnswer(dinfo, V_NG);
			printError("Agent can't answer the query: " + dinfo.message());
		}
	}

	@Override public void onAnswer(DInfo dinfo) {		// Agent
	}

	@Override public boolean notify(DInfo dinfo) {		// Proxy -> Agent
		if(owner.isProxy()) {
			accessParams(dinfo);
			sendNotify(dinfo);
			return true;
		} else {
			printError("Agent can't notify broker info.: " + dinfo.message());
			return false;
		}
	}

	@Override public void onNotify(DInfo dinfo) {		// Agent
	}

	/**
	 * ダイアログの各プロパティに対して、
	 * キーに対応するシステムパラメータの値の参照または設定を行う。
	 * プロパティの値が未設定の場合はシステムパラメータを設定し、
	 * 値をもつ場合はその値をシステムパラメータに設定する。
	 * @param dinfo ダイアログ情報
	 * @return 設定が許可されない場合はfalse
	 */
	private boolean accessParams(DInfo dinfo) {
		assert getProxy() != null : "Only proxy can answer the question.";
		SysProp sys = new SysProp();
		if(!dinfo.setIfNoValue(K_PROXY_NO, ""+sys.proxyNo)) return false;
		if(!dinfo.setIfNoValue(K_PROXY, ""+sys.proxyID)) return false;
		if(!dinfo.setIfNoValue(K_SERVER, ""+sys.serverID)) return false;
		if(!dinfo.setIfNoValue(K_MEMBERS,  sys.members)) return false;
		if(!dinfo.setIfNoValue(K_PROXY_INFO, sys.proxyList)) return false;
		if(!dinfo.setIfNoValue(K_SERVER_INFO, sys.serverList)) return false;
		if(!dinfo.setIfNoValue(K_MEMBER_INFO, sys.memberList)) return false;
		if(!getBroker().accessSerialNo(dinfo)) return false;
		return Proxy.accessNextID(dinfo);
	}

	class SysProp {
		int proxyNo;
		int proxyID;
		int serverID;
		String members;
		String proxyList;
		String serverList;
		String memberList;
		@SuppressWarnings("unchecked")
		SysProp() {
			Proxy proxy = getProxy();
			Broker broker = proxy.getBroker();
			List<Proxy> proxyList = broker.proxyList();
			this.proxyID = proxy.getID();
			this.proxyNo = proxyList.size();
			this.proxyList = joinProxy((List)proxyList);
			if(proxy.isServer() || proxy.isClient()) {
				ServerProxy server = proxy.isServer() ? (ServerProxy)proxy :
					((ClientProxy)proxy).getServerProxy();
				this.serverID = (server != null) ? server.getID() : 0;
				this.members = joinID((List)server.cpList());
				this.serverList = joinProxy((List)broker.spList());
				this.memberList = joinProxy((List)server.cpList());
			}
		}
		
	}

	private String joinID(List<Object> list) { return join(list, true); }

	private String joinProxy(List<Object> list) { return join(list, false); }

	private String join(List<Object> list, boolean id) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for(Object p : list) {
			if((i++) > 0) sb.append(":");
			sb.append(id ? ((Proxy)p).getID() : ""+p);
		}
		return sb.toString();
	}
}
