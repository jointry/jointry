package jp.ac.aiit.jointry.services.broker.core;

/**
 * EvalDialogは、Scriptの実行とその実行結果の受信を扱うダイアログの
 * ベースクラスであり、抽象メソッドとしてevalScript(script)が定義されている。
 * 拡張クラスではこの評価実行のメソッドを実装しなければならない。
 * <p>
 * Dialogインタフェースを実装する各メソッドにおいて、
 * ダイアログ情報は以下のプロパティをもつ。
 * <ul>
 * <li> K_SCRIPT スクリプトの文字列
 * <li> K_RETURN メソッド呼出しの戻り値
 * <li> K_CONTENT 標準出力に出力された文字列
 * </ul>
 * <pre>
 * 使用例：
 * eval @script=script.as @method=method(params)
 * </pre>
 */

abstract public class EvalDialog extends DialogBase {

	/**
	 * スクリプトを評価実行し、標準出力に出力された文字列を返す。
	 * @param script スクリプトのテキスト
	 * @return スクリプトの評価実行によって標準出力に出力された文字列
	 */
	abstract public String evalScript(String script);

	/**
	 * スクリプトを評価実行した結果の戻り値を返す。
	 * 拡張クラスでオーバライドすることを想定している。
	 * @return スクリプトの評価実行結果の戻り値
	 */
	public Object getReturn() { return null; }

	/**
	 * スクリプトを実行し、その実行結果を送信する。
	 * @param invoker 実行結果の送信者
	 * @param script スクリプト
	 */
	public static void sendScript(HttpBase invoker, String script) {
		DInfo dinfo = new DInfo(D_EVAL);
		dinfo.set(K_SCRIPT, script);
		invoker.sendNotify(dinfo);
	}

	/**
	 * ダイアログ情報のK_SCRIPTプロパティに設定されたスクリプトを送信する。
	 * @param dinfo ダイアログ情報
	 */
	@Override public boolean query(DInfo dinfo) {
		assert trace("==> EvalDialog#query()");
		sendQuery(dinfo);
		return true;
	}

	/**
	 * ダイアログ情報のK_SCRIPTに設定されたスクリプトを実行する。
	 * 実行結果をK_CONTENTプロパティに設定し返信する。
	 * @param dinfo ダイアログ情報
	 */
	@Override public void onQuery(DInfo dinfo) {
		assert trace(theClass() + "#onQuery: " + dinfo.message());
		if(evalScript(dinfo)) {
			sendAnswer(dinfo, V_OK);
		} else {
			sendAnswer(dinfo, V_NG);
		}
	}

	/**
	 * スクリプトの実行結果を受信する。
	 * 実行結果はダイアログ情報のK_CONTENTプロパティに設定されてある。
	 * @param dinfo ダイアログ情報
	 */
	@Override public void onAnswer(DInfo dinfo) {
		assert trace(theClass() + "#onAnswer: " + dinfo.message());
	}

	/**
	 * K_SCRIPTプロパティに設定されてあるスクリプトの実行結果を送信する。
	 * @param dinfo ダイアログ情報
	 * @return スクリプトを実行できない場合はfalse
	 */
	@Override public boolean notify(DInfo dinfo) {
		assert trace(theClass() + "#notify: " + dinfo.message());
		if(evalScript(dinfo)) {
			sendNotify(dinfo);
			return true;
		}
		return false;
	}
	
	/**
	 * K_CONTENTプロパティに設定されたスクリプトの実行結果をモニタに表示する。
	 * @param dinfo ダイアログ情報
	 */
	@Override public void onNotify(DInfo dinfo) {
		assert trace(theClass() + "#onNotify: " + dinfo.message());
		println("=> " + dinfo.get(K_RETURN));
		println(dinfo.get(K_CONTENT));
	}

	/**
	 * ダイアログ情報のK_SCRIPTに設定されたスクリプトを実行し、
	 * 戻り値をK_RETURNに、標準出力をK_CONTENTに設定する。
	 * @param dinfo ダイアログ情報
	 * @return スクリプトを実行できない場合はfalse
	 */
	private boolean evalScript(DInfo dinfo) {
		String script = dinfo.get(K_SCRIPT);
		if(script == null) {
			printError("Script not defined.");
			return false;
		}
		String output = evalScript(script);
		if(output == null) {
			printError("Bad script: " + script);
			return false;
		}
		dinfo.set(K_RETURN, ""+getReturn());
		if(output.equals("\n")) output = "";
		dinfo.set(K_CONTENT, output);
		return true;
	}

}
