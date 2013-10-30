package jp.ac.aiit.jointry.services.broker.core;

/**
 * Brokerスクリプトの評価実行に必要なメソッドを定義する。
 */
public interface Evaluator {
	/**
	 * プロパティのヘルプメッセージを表示する。
	 */
	public void printPropHelp();

	/**
	 * プロパティアクセッサを評価する。
	 * @param tokens プロパティアクセッサのトークン列
	 * @return 構文不正の場合はfalse
	 */
	public boolean evalProp(String[] tokens);

	/**
	 * メッセージを表示する。
	 * @param text メッセージテキスト
	 */
	public void println(String text);

	/**
	 * QUERYの同期呼び出し
	 * @param query QUERY文
	 * @return エラーの場合はfalse
	 */
	public boolean invokeQuery(String query);

	/**
	 * Shellコマンドの同期呼び出し
	 * @param command Shellコマンド
	 * @return エラーの場合はfalse
	 */
	public boolean invokeShell(String command);

	/**
	 * スクリプトの同期呼び出し
	 * @param script
	 * @return エラーの場合はfalse
	 */
	public boolean invokeScript(String script);

	/**
	 * QUERYを送信する。
	 * @param query QUERY文
	 * @return エラーの場合はfalse
	 */
	public boolean evalQuery(String query);

	/**
	 * 送り状を送信する
	 * @param invoice 送り状
	 * @return エラーの場合はfalse
	 */
	public boolean evalNotify(String invoice);

	/**
	 * 平文を送信する。
	 * @param message 平文
	 */
	public void sendPlain(String message);

}
