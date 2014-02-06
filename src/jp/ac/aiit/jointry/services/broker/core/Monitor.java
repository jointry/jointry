package jp.ac.aiit.jointry.services.broker.core;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * Monitorインタフェースでは、NSBrokerが内部の処理状況や受信したデータコンテンツ
 * の内容を、アプリケーションのユーザインタフェースを通じてユーザに通知する
 * ためのメソッドを定義している。
 * アプリケーションのユーザインタフェースを実現するクラスでは
 * Monitorインタフェースを用途に応じて実装すべきである。
 * <p>
 * Monitorインタフェースのデフォルトの実装 DefaultMonitorでは、タグ付きの文字列
 * (画像の場合はタイトルの表示だけ)として標準出力へ出力している。
 * GUIを提供するアプリケーションでは、実行ステータスの表示領域、
 * メッセージの表示領域、テキスト・画像・その他のコンテンツの表示領域を実装し、
 * Monitorインタフェースの各メソッドを、これらの領域への文字列や画像の表示を
 * 行う機能として実装すべきである。
 * 
 * @see broker.core.DefaultMonitor
 * @see broker.gui.MonitorBase
 */

public interface Monitor {
	/**
	 * メッセージログの表示／非表示を示すフラグのセッタ。
	 * @param msglog メッセージログの表示／非表示を示すフラグ
	 */
	public void msglog(boolean msglog);

	/**
	 * メッセージログの表示／非表示を示すフラグのゲッタ。
	 * @return メッセージログの表示／非表示を示すフラグ
	 */
	public boolean msglog();

	/**
	 * システム要素（Broker, Proxy, Agent）の内部状態を表示する。
	 * @param status 内部状態の文字列表現
	 */
	public void showStatus(String status);

	/**
	 * 文字列を改行をしないでメッセージ領域に出力する。
	 * @param msg 表示する文字列
	 */
	public void print(String msg);

	/**
	 * 文字列を最後に改行を入れてメッセージ領域に出力する。
	 * @param msg 表示する文字列
	 */
	public void println(String msg);

	/**
	 * 受信したテキストコンテンツを表示する。
	 * @param title 表示するテキストコンテンツの表題
	 * @param text 表示するテキストコンテンツ
	 */
	public void viewText(String title, String text);

	/**
	 * 受信した画像コンテンツを表示する。
	 * @param title 表示するコンテンツの表題
	 * @param bimage 表示する画像コンテンツ
	 */
	public void viewImage(String title, BufferedImage bimage);

	/**
	 * Swingコンポーネントの独自コンテンツとして表示する。
	 * @param title 表示するコンテンツの表題
	 * @param component 表示する画像コンテンツ
	 */
	public void viewComponent(String title, JComponent component);

	/**
	 * 双方向通信の相手側から終了通知を受けた際に呼び出されるハンドラ。
	 */
	public void onClose();
}
