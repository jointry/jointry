package jp.ac.aiit.jointry.services.broker.core;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.awt.image.BufferedImage;

/**
 * DialogBaseは、Dialogインタフェースを実装した双方向対話型通信機能をもつ
 * アプリケーションを実現するためのベースクラスである。
 * 双方向対話型通信機能は、Dialogで定義された５つのメソッド（QUERYの送信、
 * QUERYの受信とANSWERの返信、ANSWERの受信、NOTIFYの送信、NOTIFYの受信）から
 * 構成されている。
 * DialogBaseではこれらのメソッドのデフォルト実装を提供している。
 * 拡張クラスにおいて、必要なメソッドを再定義することによって、
 * 任意の双方向対話型通信機能を実現することができる。
 * <p>
 * 受信処理を行うメソッド(onQuery, onAnswer, onNotify)では、
 * 対話メッセージの受信後にコンテンツの受信と内容確認を行うのに、
 * 以下のメソッドを利用できる。
 * <ul>
 * <li>protected void readContent(DInfo dinfo)
 * <li>protected void viewImageContent(DInfo dinfo, String title)
 * <li>protected void viewTextContent(DInfo dinfo, String title)
 * </ul>
 */

public class DialogBase extends HttpBase implements Dialog {

	/**
	 *  ダイアログクラス定義表：ダイアログ識別名とダイアログクラスの対応表
	 */
	private static Map<String,Class> dialogMap = new HashMap<String,Class>();
	static {
		addDialog(D_PING, PingDialog.class);		// ping
		addDialog(D_SYSTEM, SysDialog.class);		// system
		addDialog(D_FTP, FTPDialog.class);			// ftp
		addDialog(D_VIEW, ViewDialog.class);		// view
		addDialog(D_EXECUTE, null);					// execute
		// execute @fqcn=class-name で実行時にダイアログクラスを指定できる。
	}

	/**
	 * 登録されたダイアログ識別名か否かを判定する。
	 * @param dialogId ダイアログ識別名
	 * @return 登録されたダイアログ識別名ならばtrue
	 */
	public static boolean containsDialogId(String dialogId) {
		return dialogMap.containsKey(dialogId);
	}

	/**
	 * 指定されたダイアログ識別名とダイアログクラスをダイアログクラス定義表に
	 * 登録する。
	 * @param dialogId ダイアログ識別名
	 * @param fqcn ダイアログクラスのFQCN
	 */
	public static void addFQCN(String dialogId, String fqcn) {
		try {
			addDialog(dialogId, Class.forName(fqcn));
		} catch(Exception e) {
			System.err.println("DialogBase: can't find a dialog class; " + fqcn);
		}
	}
	
	/**
	 * 指定されたダイアログ識別名とダイアログクラスをダイアログクラス定義表に
	 * 登録する。
	 * @param dialogId ダイアログ識別名
	 * @param dclass ダイアログクラス
	 */
	public static void addDialog(String dialogId, Class dclass) {
		dialogMap.put(dialogId, dclass);
	}
	
	/**
	 * ダイアログ識別名に対応するダイアログオブジェクトを生成する。
	 * ダイアログ識別子(@id)プロパティをもつ場合は持続的ダイアログとして
	 * 管理する。
	 * @param dinfo ダイアログ情報(ダイアログ識別名かFQCNを含む)
	 * @param dialoguer 対話の当事者（ProxyまたはAgent）
	 */
	private static DialogBase createDialog(DInfo dinfo, HttpBase dialoguer) {
		if(!dinfo.has(K_ID)) return _createDialog(dinfo, dialoguer);
		String did = dinfo.get(K_ID);
		if(did == null) did = "dialog#" + (++dialogSeq);
		DialogBase dialog = dmap.get(did);
		if(dialog == null) {
			dialog = _createDialog(dinfo, dialoguer);
			dmap.put(did, dialog);
			dialoguer.addDialog(did);
		}
		return dialog;
	}
	private static int dialogSeq = 0; // 持続的ダイアログの通し番号
	private static Map<String,DialogBase>dmap = new HashMap<String,DialogBase>();

	/**
	 * 持続的ダイアログを削除する。
	 * @param id 持続的ダイアログのID
	 */
	protected static void removeDialog(String id) { dmap.remove(id); }

	/**
	 * ダイアログ識別名に対応するダイアログオブジェクトを生成する。
	 * @param dinfo ダイアログ情報(ダイアログ識別名かFQCNを含む)
	 * @param dialoguer 対話の当事者（ProxyまたはAgent）
	 */
	private static DialogBase _createDialog(DInfo dinfo, HttpBase dialoguer) {
		// dialogId が登録されていたら対応する対話処理クラスで生成する。
		Class dclass = dialogMap.get(dinfo.dialogId);
		if(dclass != null) return createDialogByClass(dclass, dialoguer);

		// execute @fqcn=class-name で指定したクラス名で生成する。
		String fqcn = dinfo.get(K_FQCN);
		if(fqcn != null) return createDialogByFQCN(fqcn, dialoguer);

		// それ以外の場合は、DialogBaseクラスのオブジェクトを生成する。
		return new DialogBase(dialoguer);
	}

	private static DialogBase createDialogByFQCN(String fqcn, HttpBase http) {
		try {
			return createDialogByClass(Class.forName(fqcn), http);
		} catch(ClassNotFoundException e) {
			http.printStackTrace(e, null);
			return new DialogBase(http);
		}
	}

	@SuppressWarnings("unchecked")
	private static DialogBase createDialogByClass(Class dclass, HttpBase http) {
		try {
			Constructor con = dclass.getConstructor(new Class[]{});
			DialogBase d = (DialogBase)con.newInstance(new Object[]{});
			d.takeOver(http);
			return d;
		} catch(Exception e) {
			http.printStackTrace(e, null);
			return new DialogBase(http);
		}
	}

	/*========== HttpBaseから使用されるAPI ==========*/

	/**
	 * QUERYを送信する。
	 * @param dinfo 送信するQUERY情報
	 * @param interrogator 質問者
	 */
	public static boolean query(DInfo dinfo, HttpBase interrogator) {
		return createDialog(dinfo,interrogator).query(dinfo);
	}

	/**
	 * QUERYを受信し、受信したQUERYに対するANSWERを返信する。
	 * @param dinfo 受信したQUERY情報
	 * @param interrogee 被質問者
	 */
	public static void onQuery(DInfo dinfo, HttpBase interrogee) {
		createDialog(dinfo,interrogee).onQuery(dinfo);
	}

	/**
	 * 送信済みのQUERYに対する ANSWERの受信処理を行う。
	 * @param dinfo 信したANSWER情報
	 * @param interrogator 質問者
	 */
	public static void onAnswer(DInfo dinfo, HttpBase interrogator) {
		createDialog(dinfo,interrogator).onAnswer(dinfo);
	}

	/**
	 * NOTIFYを送信する。送達確認は行わない。
	 * @param dinfo 送信するNOTIFY情報
	 * @param notifier 通知者
	 */
	public static boolean notify(DInfo dinfo, HttpBase notifier) {
		return createDialog(dinfo,notifier).notify(dinfo);
	}
	/**
	 * NOTIFYの受信処理を行う。
	 * @param dinfo 受信したNOTIFY情報
	 * @param receiver 通知の受取人
	 */
	public static void onNotify(DInfo dinfo, HttpBase receiver) {
		createDialog(dinfo,receiver).onNotify(dinfo);
	}

	/*========== インスタンスメンバー ==========*/

	protected DialogBase() {}

	/**
	 * 対話オブジェクトを生成し、HttpBaseオブジェクトからソケットの入出力等の
	 * 動作環境を引き継ぎ
	 * @param dialoguer 動作環境の引継ぎ元
	 */
	protected DialogBase(HttpBase dialoguer) {
		takeOver(dialoguer);
	}

	/**
	 * 環境引継ぎの対象をownerに設定し、実行環境を引き継ぐ。
	 * @param originator 環境引継ぎの対象となる対話の当事者(ProxyかAgent)
	 */
	@Override synchronized public void takeOver(HttpBase originator) {
		owner = originator;
		super.takeOver(originator);
	}

	@Override public char mark() { return 'D'; }

	/**
	 * このダイアログの識別名を取得する。
	 * @return ダイアログ識別名
	 */
	protected final String dialogName() {
		for(Map.Entry<String,Class> e : dialogMap.entrySet()) {
			if(e.getValue() == this.getClass()) return e.getKey();
		}
		return "dialog";
	}

	/** この対話オブジェクトを生成するHttpBaseオブジェクト */
	protected HttpBase owner = null;

	/**
	 * この対話オブジェクトの生成者が Proxyならばそのオブジェクトを返す。
	 * @return この対話オブジェクトを生成したProxy
	 */
	protected Proxy getProxy() {
		if(owner.isProxy()) return (Proxy)owner;
		return null;
	}

	/**
	 * この対話オブジェクトを生成したProxyが所属するBrokerを返す。
	 * @return この対話オブジェクトを生成したProxyが所属するBroker
	 */
	protected Broker getBroker() {
		if(owner.isProxy()) return ((Proxy)owner).getBroker();
		return null;
	}

	/**
	 * この対話オブジェクトの生成者が Agentならばそのオブジェクトを返す。
	 * @return この対話オブジェクトを生成したAgent
	 */
	protected Agent getAgent() {
		if(owner.isAgent()) return (Agent)owner;
		return null;
	}

	/*========== Dialogインタフェースのデフォルト実装 ==========*/
	/**
	 * QUERYの送信処理を行う。
	 * @param dinfo 送信するQUERY情報
	 */
	@Override public boolean query(DInfo dinfo) {
		sendQuery(dinfo);
		return true;
	}

	/**
	 * QUERYを受信し、QUERYを受信しANSWERを送信する。
	 * @param dinfo 受信したQUERY情報
	 */
	@Override public void onQuery(DInfo dinfo) {
		sendAnswer(dinfo, V_OK);
	}

	/**
	 * ANSWERの受信処理を行う。
	 * @param dinfo 受信したANSWER情報
	 */
	@Override public void onAnswer(DInfo dinfo) {
	}

	/**
	 * NOTIFYの送信処理を行う。
	 * @param dinfo 送信するNOTIFY情報
	 */
	@Override public boolean notify(DInfo dinfo) {
		sendNotify(dinfo);
		return true;
	}

	/**
	 * NOTIFYの受信処理を行う。
	 * @param dinfo 受信したNOTIFY情報
	 */
	@Override public void onNotify(DInfo dinfo) {
	}

	/*============ コンテンツの受信と内容確認 ============*/
	/**
	 * テキスト形式のデータコンテンツを読み取り、ダイアログ情報に設定する。
	 * @param dinfo ダイアログ情報
	 */
	protected void readContent(DInfo dinfo) {
		try {
			dinfo.readContent(this);
		} catch(IOException e) {
			showError(dialogName() + ": " + e.getMessage());
		}
	}

	protected void storeContent(DInfo dinfo, String fname) {
		try {
			dinfo.storeContent(fname);
		} catch(IOException e) {
			showError(dialogName() + ": " + e.getMessage());
		}
	}

	/**
	 * 画像コンテンツを表示する。
	 * @param dinfo ダイアログ情報
	 * @param title コンテンツの表題（ダイアログ情報で未定義の場合に使用）
	 */
	protected void viewImageContent(DInfo dinfo, String title) {
		BufferedImage bimage = dinfo.getImageContent();
		if(bimage != null) monitor.viewImage(dinfo.get(K_TITLE, title), bimage);
	}
	/**
	 * テキストコンテンツを表示する。
	 * @param dinfo ダイアログ情報
	 * @param title コンテンツの表題（ダイアログ情報で未定義の場合に使用）
	 */
	protected void viewTextContent(DInfo dinfo, String title) {
		String text = dinfo.getTextContent();
		if(text != null) monitor.viewText(dinfo.get(K_TITLE, title), text);
	}
}
