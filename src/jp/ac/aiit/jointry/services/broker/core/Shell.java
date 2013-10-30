package jp.ac.aiit.jointry.services.broker.core;

/**
 * Shellインタフェースでは、エージェントから送信されたコマンド文字列を
 * 評価・実行するインタプリタの初期化とコマンドの評価・実行を行うメソッドを
 * 定義している。
 */

public interface Shell {
	/**
	 * コマンドを評価・実行するインタプリタを初期化する。
	 */
	public void initShell();
	/**
	 * コマンドを評価・実行する。
	 * @param command コマンド文字列
	 */
	public String evalShell(String command);
}
