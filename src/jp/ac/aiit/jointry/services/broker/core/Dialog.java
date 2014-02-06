package jp.ac.aiit.jointry.services.broker.core;

/**
 * Dialogは、双方向対話型通信機能の仕様を示すインタフェースであり、
 * Brokerにおける QA型通信とNOTIFY型通信の２種類の通信形式を定義している。
 * QA型通信は「QUERYの送信」、「QUERYの受信とANSWERの発行」、「ANSWERの受信」を
 * 行う３つのメソッドからなり、NOTIFY型通信は「OTIFYの送信」と「NOTIFYの受信」を
 * 行う２つのメソッドからなる。
 * ここでQUERY・ANSWER・NOTIFYは、アプリケーション毎に定められたプロパティや
 * データコンテンツを含むメッセージである。
 * <ul>
 * <li>public boolean query(DInfo dinfo)
 * <li>public void onQuery(DInfo dinfo)
 * <li>public void onAnswer(DInfo dinfo)
 * <li>public boolean notify(DInfo dinfo)
 * <li>public void onNotify(DInfo dinfo)
 * </ul>
 * <p>
 * ５つのメソッドはいずれもDInfo型を引数にとる。
 * DInfoはプロトコルのダイアログ識別名とプロパティ情報（key/valueマップ）を
 * 保持しており、アプリケーションレベルのプロトコルを定義することができる。
 * またプロトコルの実装の際は DInfoが提供している各種メソッドを利用して
 * プロパティの値の参照や設定が行える。
 * <p>
 * Dialogインタフェースを実装したクラスをダイアログクラスとよぶ。
 * 双方向通信を行うアプリケーションを開発することは Dialogインタフェースの
 * 各メソッドを実装したダイアログクラス作成することに帰着する。
 * ダイアログクラスを使用することにより、NSBrokerの通信モデルに基づいた
 * グループでの双方向通信が可能となる。
 * 
 * @see DialogBase
 * @see DInfo
 */
public interface Dialog {
	/**
	 * QUERYを送信する
	 * @param dinfo 送信するQUERY情報
	 * @return 不正なqueryの場合はfalse
	 */
	public boolean query(DInfo dinfo);

	/**
	 * QUERYを受信し、受信したQUERYに対するANSWERを返信する。
	 * @param dinfo 受信したQUERY情報
	 */
	public void onQuery(DInfo dinfo);

	/**
	 * ANSWERの受信処理を行う。
	 * @param dinfo 受信したANSWER情報
	 */
	public void onAnswer(DInfo dinfo);

	/**
	 * NOTIFYを送信する。送達確認は行わない。
	 * @param dinfo 送信するNOTIFY情報
	 * @return 不正なnotifyの場合はfalse
	 */
	public boolean notify(DInfo dinfo);

	/**
	 * NOTIFYの受信処理を行う。
	 * @param dinfo 受信したNOTIFY情報
	 */
	public void onNotify(DInfo dinfo);
}
