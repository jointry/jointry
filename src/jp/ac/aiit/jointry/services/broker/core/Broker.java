package ash.broker.core;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import ash.broker.util.Util;
import ash.broker.core.Property.Counter;

/**
 * Brokerの双方向通信の概念モデルは、
 * Broker, Proxy, Agentの３種類の構成要素からなる。
 * Brokerはサービス交換の場を提供する役割をもつ。
 * AgentはBrokerに対してサービス交換のために接続要求を出す。
 * BrokerはAgentからの接続要求を受けたら、そのAgentに対応するProxyを
 * 生成しこのProxyにAgentとの双方向通信を委譲する。
 * <p>
 * 一般的なサービスの交換形態では、サービスを提供する１つのAgent(SA)が、
 * サービスを利用する複数のAgent(CAi)と１対多の関係をもつ。
 * サービスを提供するAgentをサーバ、サービスを利用するAgentをクライアントと呼ぶ。
 * Broker(B)の中では各Agentに対応するProxyがサービス交換の実務を担当する。
 * SAとCAi(i=1..n)に対応して、SPとCPiが作られる。
 * これらがProxyのグループを形成しグループでの双方向通信が可能になる。
 * SPとCPi(i=1..n)をあわせてProxyグループと呼ぶ。
 * Brokerは複数のProxyグループを管理し、
 * 様々なサービス交換形態の実現を可能にしている。
 * <pre>
 *                   B
 *          +-----------------+
 *   SA ----+-- SP ---- CP1 --+----CA1
 *          |    +----- CP2 --+----CA2
 *          |    +----- CPi --+----CAi
 *          |    +----- CPn --+----CAn
 *          +-----------------+
 * </pre>
 * <p>
 * Brokerの実装の概要を説明する。
 * Brokerは、Agentに対する接続サービスを提供し利用状況を管理するために、
 * Agentのアカウントを管理している。
 * Brokerは、起動後直ちにServerSocketでAgentからの接続要求を待ち受ける。
 * Brokerは接続要求を受理したら適切なProxyを生成し、
 * Agentとの交信を開始させるために生成したProxyを起動する。
 * <p>
 * Brokerは、現在Agentに接続され稼働している全てのProxyのリストと、
 * 全てのServerProxyのリストをもち、
 * 様々な形態のProxyグループの管理を可能にしている。
 * アカウント情報とProxyグループの情報を用いて
 * 様々な接続形態のサービス交換が可能になる。
 * <p>
 * Proxyを生成するメソッドは様々な接続形態のサポートが実装できるように
 * 設計してある。
 * 現在の実装では、ServerProxyの二重起動や不在時の代替Proxyへの接続、
 * 同一クライアントの同時接続等の制御をフラグで行う仕組みを組み込んである。
 * <p>
 * クライアントからの接続要求に対して接続すべきサーバを特定することは
 * マッチング戦略に係る問題である。
 * 現在の実装ではServerProxyのリストから最初にサービス可能なものを返す。
 * このメソッドをオーバライドすることによって、マッチング戦略を
 * カスタマイズすることができる。
 * <p>
 * Brokerは、稼働中のProxyに対して各種のメッセージやデータコンテンツを
 * ブロードキャストするためのAPIを提供している。
 * 
 * @see Proxy
 * @see Agent
 * @see HttpInfo
 */

public class Broker extends WorkerBase implements Shell {
	static String USAGE = "Usage: java Broker <port> <document-root>";
	static String MARKET = "Webサービスマーケット";

	/**
	 * コマンドラインから Brokerを起動する場合の呼出し口
	 * @param args コマンドラインパラメータ
	 */
	public static void main(String[] args) {
		new Broker().runApp(args);
	}

	/**
	 * コマンドラインパラメータを使って、
	 * サーバソケットやドキュメントルートの初期化を行う。
	 * @param args コマンドラインパラメータ
	 */
	protected void runApp(String[] args) {
        int port = 0;
        if(args.length < 2 || (port = Util.getPort(args[0])) == -1) {
			System.err.println(USAGE);
            System.exit(-1);
        }
		try {
			initialize(port, MARKET);
			setDocRoot(args[1]);
			start();
		} catch(Exception e) {
			System.err.println("Broker: " + e.getMessage());
			System.err.println(USAGE);
            System.exit(-1);
		}
	}

	/**
	 * Shell のデフォルト実装
	 */
	@Override public void initShell() {}
	@Override public String evalShell(String command) {
		return "eval(\"" + command + "\")";
	}

	/** ユーザアカウント */
	private Account account = Account.getInstance();

	/**
	   ユーザアカウントを設定する。
	   @param account ユーザアカウント
	 */
	public void setAccount(Account account) { this.account = account; }

	/**
	 * ProxyとAgentの接続回数を記録する。
	 * @param name ユーザID
	 */
	public void login(String name) { account.login(name); }

	/**
	 * 交信を終了するProxyをアカウントから削除する。
	 * ProxyがAgentからの交信終了通知を受けた場合に呼び出される。
	 * @param name ユーザID
	 */
	public void logout(String name) {
		if(name != null) account.logout(name);
	}

	private ProxyFactory proxyFactory;

	/** Agentからの接続要求を待ち受けるサーバソケット */
	private ServerSocket serverSocket;

	/** Brokerのマーケット名 */
	private String market;

	/** サーバの二重起動の制御 */
	private boolean multiServer = false;// サーバの二重起動を許可するか？
	public void multiServer(boolean ms) { multiServer = ms; }

	/** 同一クライアントの同時接続の制御 */
	private boolean multiClient = false;// 同一Clientからの同時接続を許可するか？
	public void multiClient(boolean mc) { multiClient = mc; }

	/**
	 * 引数を持たないコンストラクタ
	 */
	public Broker() { proxyFactory = new ProxyFactory(this.monitor); }

	/**
	 * サーバソケットやドキュメントルートの初期化を行うコンストラクタ
	 * @param port ポート番号
	 * @param market マーケット名、
	 */
	public Broker(int port, String market) throws Exception {
		this();
		initialize(port, market);
	}

	/**
	 * サーバソケットやドキュメントルートの初期化を行う。
	 * @param port ポート番号
	 * @param market マーケット名、
	 */
	private void initialize(int port, String market) throws Exception {
		this.serverSocket = new ServerSocket(port);
		this.market = market;
		// Broker用の作業ファイル格納ディレクトリのパス名を生成する。
		workRoot += "/" + market;
		initShell();				// Shellの初期化
	}

	/** マーケット名とポート番号を含む文字列表現を返す。 */
	@Override public String toString() {
		return "Broker[" + market + ", " + serverSocket.getLocalPort() + "]";
	}

	/**
	 * Brokerの接続サービスを開始する。これ以降、Agentからの接続要求を受理し、
	 * 要求されたProxyを生成し、Agentとの間で交信を開始する。
	 */
	@Override public void run() {
		String description = serverSocket.getLocalPort() + "("+market+")";
		try {
			// Brokerのサービス開始のメッセージをモニタに表示する。
			println("Broker: " + description + " running...");
			Socket socket;
			// Agentからの接続要求を受理する
			while((socket = serverSocket.accept()) != null) {
				assert trace("accept: " + socket);
				// 適切なProxyを生成し、Agentとの交信を開始する。
				Proxy proxy = createProxy(socket);
				if(proxy != null) {
					proxy.setBroker(this);
					proxyList.add(proxy);
					println("" + proxy + ": starting");
					proxy.reply(HTTP200);
					proxy.start();
				}
			}
		} catch(IOException e) { // SocketException, SocketTimeoutException
			// Agent との接続が切断されると SocketExceptionが投げられる。
			println("Broker: " + description + ": " + e.getMessage());
		} catch(RuntimeException e) { // SecurityException, ...
			printStackTrace(e, null);
		}
	}

	// このBrokerの元で稼動しているProxyのリスト
	private List<Proxy> proxyList = new ArrayList<Proxy>();

	// このBrokerの元で稼動しているServerProxyのリスト(proxyListの部分集合)
	private List<ServerProxy> spList = new ArrayList<ServerProxy>();

	/** このBrokerの元で稼動しているProxyのリストを取得する。 */
	public List<Proxy> proxyList() { return proxyList; }

	/** このBrokerの元で稼動しているServerProxyのリストを取得する。 */
	public List<ServerProxy> spList() { return spList; }

	/**
	 * このBrokerからProxyを削除する。
	 * @param proxy 削除するProxy
	 */
	synchronized void removeProxy(Proxy proxy) {
		proxyList.remove(proxy);
		spList.remove(proxy);
		println("" + proxy + ": terminated");
	}

	/**
	 * BrokerがProxyを介してAgentをinvokeする際のパラメータ<br>
	 * currProxyIDのプロキシが存在する場合はそれをinvokeする。
	 * 存在しない場合は、Brokerに接続しているプロキシを順番に呼出す。
	 * proxyIndexは次にinvokeするプロキシのIDである。
	 */
	private int timeout = INVOKE_TIMEOUT; // :timeoutで設定／参照
	private int currProxyID = 0;	// invoke先ProxyのID(:proxyで設定／参照)
	private int proxyIndex = 0;		// 次のinvoke先Proxyを指すproxyListの位置

	public int getInvokeTimeout() { return timeout; }
	public void setInvokeTimeout(int t) { timeout = t; }
	public int currProxyID() { return currProxyID; }
	public void currProxyID(int id) { currProxyID = id; }

	/**
	 * invokeするプロキシを特定する。
	 * @return QUERY/COMMAND呼出し先プロキシ
	 */
	synchronized public Proxy nextProxy() {
		Proxy proxy = getProxy(currProxyID);
		if(proxy == null) {
			int n = proxyList.size();
			if(n == 0) return null;
			if(proxyIndex >= n) proxyIndex = 0;
			proxy = proxyList.get(proxyIndex);
			proxyIndex++;
		}
		proxy.setInvokeTimeout(timeout);
		proxy.setReturnFlag(returnFlag);
		return proxy;
	}

	/**
	 * invokeの戻り値のダイアログ情報を表示可否フラグを設定する
	 */
	public void setReturnFlag(boolean b) { returnFlag = b; }
	private boolean returnFlag = false;
	
	/*
	 * 複数のProsyIDから対応するProxyのリストを作成する。
	 * @param proxyIDs proxyID を ":" で連結した文字列
	 * @return 対応するProxyのリスト(proxyIDsが空の場合はnull)
	 */
	public List<Proxy> selectProxies(String[] idArray) {
		if(idArray == null) return null;
		Map<String,Proxy> proxyMap = new HashMap<String,Proxy>();
		for(Proxy proxy : proxyList) proxyMap.put(""+proxy.getID(), proxy);
		List<Proxy> memberList = new ArrayList<Proxy>();
		for(String id : idArray) {
			Proxy proxy = proxyMap.get(id);
			if(proxy != null) memberList.add(proxy);
		}
		return memberList;
	}

	/**
	 * 指定されたProxy識別子を持つProxyを返す。
	 * @param proxyID Proxyの一意識別子
	 * @return 指定されたProxy識別子を持つProxy（存在しない場合はnull）
	 */
	public Proxy getProxy(int proxyID) {
		for(Proxy proxy : proxyList) {
			if(proxy.getID() == proxyID) return proxy;
		}
		return null;
	}

	/**
	 * Agentからの接続要求に従って適切なProxyを生成する。
	 * @param socket Agentに接続したソケット
	 */
	synchronized private Proxy createProxy(Socket socket) {
		HttpBase http = new HttpBase(socket);
		http.setMonitor(monitor);
		HttpInfo hinfo = null;
		try {
			hinfo = http.receiveRequest();
			if(hinfo.dialogId.equals(HTTP_GET)) {
				Proxy proxy = new GetProxy(hinfo);
				proxy.takeOver(http);
				return proxy;
			}
			Proxy proxy = createProxy(hinfo);
			proxy.takeOver(http);
			account.addActiveUser(hinfo.get(USER_ID), proxy.getID());
			return proxy;

		} catch(IOException e) {
			// Proxy の生成に失敗したら、モニタにエラーメッセージを表示し、
			// 接続要求を出したAgentには HTTP400　を返す。
			println("*** ERROR: " + e.getMessage());
			if(hinfo != null) println(""+hinfo);
			http.reply(HTTP400);
			return null;
		}
	}

	/**
	 * 接続要求情報を元に該当するProxyを生成する。
	 * @param hinfo 接続要求情報(HTTPメソッド、接続要求判別、サービス種別など)
	 */
	protected Proxy createProxy(HttpInfo hinfo) throws IOException {
		if(!account.certify(hinfo.get(USER_ID), hinfo.get(PASSWORD))) {
			throw new IOException(HTTP401);
		}
		if(!hinfo.dialogId.equals(HTTP_POST)) {
			throw new IOException(HTTP405 + " ---- " + hinfo.dialogId);
		}

		if(hinfo.demandServer()) {
			if(!multiServer && spList.size() > 0) {
				// サーバの二重起動拒否
				throw new IOException(HTTP403 + " ---- サーバの二重起動");
			}
			ServerProxy sp = proxyFactory.createServerProxy(hinfo);
			spList.add(sp);
			onServerStart(hinfo);
			return sp;

		} else if(hinfo.demandClient()) {
			if(!multiClient && account.isActiveUser(hinfo.get(USER_ID))) {
				// 同一クライアントの同時接続拒否
				throw new IOException(HTTP403 + " ---- クライアント接続済み");
			}
			ServerProxy sp = findServer(hinfo);
			if(sp == null) {
				// サービス提供可能なサーバ不在のため接続不可
				throw new IOException(HTTP404 + " ---- サーバ不在");
			}
			// 適切なServerProxyが存在したらClientProxyを生成し両者を関連付ける
			ClientProxy cp = proxyFactory.createClientProxy(hinfo);
			onClientStart(hinfo);
			sp.addClient(cp);
			return cp;

		} else {
			return new Proxy(hinfo);
		}
	}
	/**
	 * クライアントからの接続要求に対して接続すべきサーバを特定する。
	 * 現在の実装ではServerProxyのリストから最初にサービス可能なものを返す。
	 * このメソッドをオーバライドすることによって、マッチング戦略を
	 * カスタマイズすることができる。
	 * @param hinfo 接続情報
	 * @return 接続要求に応えるServerProxy(存在しない場合はnull)
	 */
	protected ServerProxy findServer(HttpInfo hinfo) {
		for(ServerProxy sp : spList) {
			if(sp.canServeTo(hinfo)) return sp;
		}
		return null;
	}

	/**
	 * サーバ起動時の処理を記述するハンドラ。
	 * 拡張クラスでオーバライドできる。
	 * @param hinfo 接続情報
	 */
	public void onServerStart(HttpInfo hinfo) {
	}

	/**
	 * クライアント起動時の処理を記述するハンドラ。
	 * 拡張クラスでオーバライドできる。
	 * @param hinfo 接続情報
	 */
	public void onClientStart(HttpInfo hinfo) {
	}

	/**
	 * Brokerを終了させる。
	 */
	synchronized public void close() {
		try {
			account.save();
			serverSocket.close();
			for(Proxy proxy : proxyList) proxy.close();
			proxyList.clear();
		} catch(IOException e) {
			printStackTrace(e, null);
		}
	}

	/**==== 稼働中の全Proxyに対する各種メッセージ・データの配信 ====<br>
	 * Brokerは、運用状況の管理のために、稼働中のProxyに対して
	 * 各種のメッセージやデータコンテンツをブロードキャストするための
	 * APIを提供している。
	 */

	private Counter counter = new Counter(K_SERIAL_NO, 1);

	public boolean accessSerialNo(DInfo dinfo) {
		return counter.access(dinfo);
	}

	/**
	 * このBrokerに接続している全Proxyに対してメッセージを送信する。
	 * @param message 送信するメッセージ
	 */
	synchronized public void broadcast(String message) {
		message = ""+ counter.nextID() + ": " + message;
		println(message);
		for(Proxy proxy : proxyList) proxy.sendPlain(message);
	}

	/**
	 * このBrokerに接続している全Proxyに対してQUERYを発行する。
	 * @param query QUERY文
	 * @return エラーがあった場合はfalse
	 */
	synchronized public boolean sendQuery(String query) {
		if(msglog()) logSend(query);
		for(Proxy proxy : proxyList) {
			if(!proxy.evalQuery(query)) return false;
		}
		return true;
	}

	/**
	 * このBrokerに接続している全Proxyに対してNOTIFYを発行する。
	 * @param invoice 送信する送り状
	 * @return エラーがあった場合はfalse
	 */
	synchronized public boolean sendNotify(String invoice) {
		if(msglog()) logSend(invoice);
		for(Proxy proxy : proxyList) {
			if(!proxy.evalNotify(invoice)) return false;
		}
		return true;
	}

	/**
	 * このBrokerに接続している全Proxyに対してメッセージを送信する。
	 * @param message 送信するメッセージ
	 */
	synchronized public void pushMessage(String message) {
		for(Proxy proxy : proxyList) proxy.sendMessage(M_NOTIFY + message);
	}

	/**
	 * このBrokerに接続している全Proxyに対してテキストを送信する。
	 * @param title テキストの名前
	 * @param text 送信するテキスト
	 */
	synchronized public void pushText(String title, String text) {
		for(Proxy proxy : proxyList) proxy.notifyViewText(title, text);
	}
	/**
	 * このBrokerに接続している全Proxyに対して画像を送信する。
	 * @param title 画像の名前
	 * @param bimage 送信する画像
	 */
	synchronized public boolean pushImage(String title, BufferedImage bimage) {
		assert startTime("Broker#pushImage:");
		String filepath = createImageFile(title, bimage);
		if(filepath == null) return false;
		assert time("image written:");
		for(Proxy proxy : proxyList) proxy.viewImageFile(filepath);
		assert time("Broker#pushImage done:");
		return true;
	}

}
