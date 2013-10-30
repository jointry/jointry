package jp.ac.aiit.jointry.services.broker.core;

/**
 * ShellDialogは、Shellコマンドの発行とその実行結果の受信を扱うダイアログの
 * ベースクラスであり、抽象メソッドとしてevalShell(command)が定義されている。
 * 拡張クラスではこの評価実行のメソッドを実装しなければならない。
 * <p>
 * Dialogインタフェースを実装する各メソッドにおいて、
 * Shellコマンドはダイアログ情報のK_COMMANDプロパティに設定され、
 * Shellコマンドの実行結果である標準出力に出力された文字列は
 * K_CONTENTプロパティに設定される。
 */
// @shell @command=ls~-l

abstract public class ShellDialog extends DialogBase {

	/**
	 * Shellコマンドを評価実行し、標準出力に出力された実行結果の文字列を返す。
	 * @param command Shellコマンド
	 * @return Shellコマンドの評価実行結果の文字列
	 */
	abstract public String evalShell(String command);

	/**
	 * Shellコマンドを評価実行した結果のexitCodeを返す。
	 * 拡張クラスでオーバライドすることを想定している。
	 * @return Shellコマンドの評価実行結果のexitCode
	 */
	public int getExitCode() { return 0; }

	/**
	 * Shellコマンドを実行し、その実行結果を送信する。
	 * @param invoker 実行結果の送信者
	 * @param command Shellコマンド
	 */
	public static void sendShell(HttpBase invoker, String command) {
		DInfo dinfo = new DInfo(D_SHELL);
		dinfo.set(K_COMMAND, command);
		invoker.sendNotify(dinfo);
	}

	/**
	 * ダイアログ情報のK_COMMANDプロパティに設定されたShellコマンドを送信する。
	 * @param dinfo ダイアログ情報
	 */
	@Override public boolean query(DInfo dinfo) {
		assert trace("==> ShellDialog#query()");
		sendQuery(dinfo);
		return true;
	}

	/**
	 * ダイアログ情報のK_COMMANDに設定されたShellコマンドを実行する。
	 * 実行結果をK_CONTENTプロパティに設定し返信する。
	 * @param dinfo ダイアログ情報
	 */
	@Override public void onQuery(DInfo dinfo) {
		assert trace(theClass() + "#onQuery: " + dinfo.message());
		if(evalShell(dinfo)) {
			sendAnswer(dinfo, V_OK);
		} else {
			sendAnswer(dinfo, V_NG);
		}
	}

	/**
	 * Shellコマンドの実行結果を受信する。
	 * 実行結果はダイアログ情報のK_CONTENTプロパティに設定されてある。
	 * @param dinfo ダイアログ情報
	 */
	@Override public void onAnswer(DInfo dinfo) {
		assert trace(theClass() + "#onAnswer: " + dinfo.message());
	}

	/**
	 * K_COMMANDプロパティに設定されてあるShellコマンドの実行結果を送信する。
	 * @param dinfo ダイアログ情報
	 * @return Shellコマンドを実行できない場合はfalse
	 */
	@Override public boolean notify(DInfo dinfo) {
		assert trace(theClass() + "#notify: " + dinfo.message());
		if(evalShell(dinfo)) {
			sendNotify(dinfo);
			return true;
		}
		return false;
	}
	
	/**
	 * K_CONTENTプロパティに設定されたShellコマンドの実行結果をモニタに表示する。
	 * @param dinfo ダイアログ情報
	 */
	@Override public void onNotify(DInfo dinfo) {
		assert trace(theClass() + "#onNotify: " + dinfo.message());
		println(dinfo.get(K_CONTENT));
	}

	/**
	 * ダイアログ情報のK_COMMANDに設定されたShellコマンドを実行し、
	 * exitCodeをK_EXITCODEに、結果をK_CONTENTに設定する。
	 * @param dinfo ダイアログ情報
	 * @return Shellコマンドを実行できない場合はfalse
	 */
	private boolean evalShell(DInfo dinfo) {
		String command = dinfo.get(K_COMMAND);
		if(command == null) {
			printError("Shell command not defined.");
			return false;
		}
		String output = evalShell(command);
		if(output == null) {
			printError("Bad shell command: " + command);
			return false;
		}
		dinfo.set(K_EXITCODE, getExitCode());
		if(output.equals("\n")) output = "";
		dinfo.set(K_CONTENT, output);
		return true;
	}

}
