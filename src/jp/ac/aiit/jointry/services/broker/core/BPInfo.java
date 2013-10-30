package jp.ac.aiit.jointry.services.broker.core;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;
import jp.ac.aiit.jointry.services.broker.util.Util;

/**
 * BPInfoはHTTP通信プロトコルとNSBroker独自プロトコルのデータ構造を定義し、
 * メッセージの文字列表現と内部のデータ構造の間の相互変換のためのメソッドを
 * 提供する。
 * プロトコルは全て文字列で送受信される。
 * プロトコルの包括的仕様を以下にBNFで示す。
 * <pre>
 * message ::= dialog-id property*
 * property ::= key[=value]
 * </pre>
 * <p>
 * 改行コードによって分けられた文字列が通信の処理単位であるメッセージである。
 * メッセージは空白文字で分けられた複数のトークンの列から構成される。
 * 先頭のトークンはメッセージの種別を示すダイアログ識別名である。
 * ダイアログ識別名に続くトークンはダイアログのプロパティとして扱われる。
 * <p>
 * 各プロパティはキーと値を持つ。キーと値は分離文字'='で分けられる。
 * キーはプロパティの種別を表し値はその内容である。
 * 値は空白文字以外の任意の文字からなる文字列であり空文字列であっても良い。
 * 空文字列の場合は分離文字は省略できる。
 * <p>
 * プロパティの値は任意の文字列を持ち得る。メッセージの中のプロパティには
 * 空白文字や改行コードを含めることができないので、プロパティの値はエスケープ
 * 処理を施し、空白文字と改行コードを含まない文字列に変換されてメッセージが
 * 組み立てられる。
 * <p>
 * BPInfoは、プロトコルの各プロパティに対して、キーを指定して、値の設定、
 * 値の読取り、値の判定などを行う各種のメソッドを提供している。
 */

public class BPInfo implements Common {
	/** 各種プロトコルのメッセージ識別名またはダイアログ識別名 */
	protected String dialogId;

	/** プロパティ情報を保持するkey/valueマップ */
	private Map<String,String> propMap = new LinkedHashMap<String,String>();

	/**
	 * キーと値から成るプロパティを登録する。
	 * @param key プロパティのキー
	 * @param value プロパティの値
	 */
	private void setProp(String key, String value) {
		propMap.put(key, value);
	}

	/**
	 * プロパティのキーのリストを返す。
	 * @return プロパティのキーのリスト
	 */
	public Set<String> keySet() { return propMap.keySet(); }

	@Override public String toString() { return "BPInfo[" + dialogId + "]"; }

	/**
	 * コマンドのメッセージ形式文字列を返す。
	 * @return コマンドのメッセージ形式文字列
	 */
	public String message() { return dialogId + " " + params("=", " ", true); }

	/**
	 * プロパティの定義順に、一行１件の key = value 形式の文字列で返す。
	 * @return プロパティの定義情報の文字列
	 */
	public String params() { return params("=", "\n"); }

	/**
	 * プロパティの定義順に、指定書式で key/value情報を返す。
	 * @param vs keyとvlueの間のセパレータ
	 * @param ps プロパティ間のセパレータ
	 * @return プロパティの定義情報の文字列
	 */
	public String params(String vs, String ps) { return params(vs, ps, false); }

	private String params(String vs, String ps, boolean escape) {
		StringBuilder sb = new StringBuilder();
		for(String key : propMap.keySet()) {
			String value = propMap.get(key);
			if(value == null) {
				sb.append(key + ps);
			} else {
				if(escape) value = escape(value);
				sb.append(key + vs + value + ps);
			}
		}
		return sb.toString();
	}

	/**
	 * Timestamp情報を文字列として返す。
	 * @return Timestamp情報の文字列
	 */
	public String getTimestamp() {
		String timestamp = get(K_TIMESTAMP);
		if(timestamp == null) return null;
		StringBuilder sb = new StringBuilder();
		for(String item : timestamp.split(":"))
			sb.append(item + "\t");
		return sb.toString();
	}

	/**
	 * エスケープされた文字列を指定されたキーの値として設定する。
	 * @param key プロパティのキー
	 * @param value エスケープされた文字列
	 */
	public void setEscapedValue(String key, String value) {
		setProp(key, antiEscape(value));
	}
	/**
	 * 指定されたキーと整数値を持つプロパティを設定する。
	 * 設定済みの場合は再設定する（値を置換する）。
	 * @param key プロパティのキー
	 * @param ivalue プロパティの整数値
	 */
	public void set(String key, int ivalue) {
		setProp(key, ""+ivalue);
	}
	/**
	 * 指定されたキーと値を持つプロパティを設定する。
	 * 設定済みの場合は再設定する（値を置換する）。
	 * @param key プロパティのキー
	 * @param value プロパティの値
	 */
	public void set(String key, String value) {
		setProp(key, value);
	}

	/**
	 * 指定されたキーを定義する（値を持たないプロパティを登録する）。
	 * @param key プロパティのキー
	 */
	public void define(String key) {
		setProp(key, null);
	}

	/**
	 * 指定されたキーが存在しかつ値を持たないならば指定された値を設定する。
	 * 参照できるが設定を許さないプロパティのアクセス制御の際に利用する。
	 * @param key プロパティのキー
	 * @param value プロパティの値
	 * @return キーが存在し値をもつ場合はfalse
	 */
	public boolean setIfNoValue(String key, String value) {
		if(has(key)) {
			if(get(key) != null) return false;
			set(key, value);
		}
		return true;
	}

	/**
	 * 指定されたキーの値が未定義の場合にデフォルト値を設定する。
	 * @param key プロパティのキー
	 * @param defaultVal プロパティのデフォルト値
	 */
	public void setDefault(String key, String defaultVal) {
		setProp(key, get(key, defaultVal));
	}

	/**
	 * 指定されたキーのプロパティを削除する。
	 * @param key プロパティのキー
	 */
	public String remove(String key) {
		return propMap.remove(key);
	}

	/**
	 * 指定されたキーの値を取得する。
	 * @param key プロパティのキー
	 */
	public String get(String key) {
		return propMap.get(key);
	}
	/**
	 * 指定されたキーの値を取得する。値が未定義(null)の場合はデフォルト値を返す。
	 * @param key プロパティのキー
	 * @param defaultVal プロパティのデフォルト値
	 */
	public String get(String key, String defaultVal) {
		String val = get(key);
		return (val != null) ? val : defaultVal;
	}

	/**
	 * 指定されたキーの時刻をミリ秒として取得する。
	 * @param key 時刻プロパティのキー
	 * @return 時刻のミリ秒
	 */
	public int getTime(String key) {
		return Util.parseMillisecond(propMap.get(key));
	}

	/**
	 * 指定されたキーの値を整数値として取得する。
	 * @param key プロパティのキー
	 * @return キーの値の整数値
	 */
	public int getInt(String key) {
		return Util.getInt(get(key));
	}
	/**
	 * 指定されたキーの値を整数値として取得する。値が未定義(null)の場合は
	 * デフォルト値を返す。
	 * @param key プロパティのキー
	 * @param defaultVal プロパティのデフォルト値
	 * @return キーの値の整数値
	 */
	public int getInt(String key, int defaultVal) {
		String val = get(key);
		return (val != null) ? Util.getInt(val) : defaultVal;
	}

	/**
	 * 指定された文字列配列を":"を分離文字として連結した文字列を、
	 * 指定されたキーの値に設定する。
	 * @param key プロパティのキー
	 * @param values プロパティの文字列配列
	 */
	public void setArray(String key, String[] values) {
		set(key, Util.join(":", (Object[])values));
	}
	/**
	 * 指定されたキーの値（":"を分離文字とする文字列）を文字列配列として
	 * 取得する。
	 * @param key プロパティのキー
	 * @return 文字列配列
	 */
	public String[] getArray(String key) {
		String value = get(key);
		if(value == null || value.isEmpty()) return null;
		return value.split(":");
	}
	/**
	 * 指定された文字列配列を指定された分離文字で連結した文字列を、
	 * 指定されたキーの値に設定する。
	 * @param key プロパティのキー
	 * @param values プロパティの文字列配列
	 * @param separator 文字列の分離文字
	 */
	public void setArray(String key, String[] values, String separator) {
		set(key, Util.join(separator, (Object[])values));
	}
	/**
	 * 指定されたキーの値（指定された分離文字列で連結した文字列）を
	 * 文字列配列として取得する。
	 * @param key プロパティのキー
	 * @param separator 文字列の分離文字
	 * @return 文字列配列
	 */
	public String[] getArray(String key, String separator) {
		String value = get(key);
		if(value == null || value.isEmpty()) return null;
		return value.split(separator);
	}

	/**
	 * 指定された整数値配列を":"で連結した文字列を、指定されたキーの値に
	 * 設定する。
	 * @param key プロパティのキー
	 * @param values プロパティの文字列配列
	 */
	public void setIntArray(String key, int[] values) {
		StringBuilder sb = new StringBuilder();
		for(int i : values) {
			if(sb.length() > 0) sb.append(":");
			sb.append(""+i);
		}
		set(key, sb.toString());
	}
	/**
	 * 指定されたキーの値（数字を":"で連結した文字列）を、整数値配列として
	 * 取得する。
	 * @param key プロパティのキー
	 * @return キーの値の整数値配列
	 */
	public int[] getIntArray(String key) {
		String value = get(key);
		if(value == null || value.isEmpty()) return null;
		String[] items = value.split(":");
		int[] array = new int[items.length];
		int i = 0;
		for(String item : items) array[i++] = Util.getInt(item);
		return array;
	}

	/**
	 * 指定されたキーの値に指定された整数値を要素として追加する。
	 * @param key プロパティのキー
	 * @param value 追加する整数値
	 */
	public void append(String key, int value) {
		append(key, ""+value, ":");
	}

	/**
	 * 指定されたキーの値に指定された文字列を要素として追加する。
	 * @param key プロパティのキー
	 * @param value 追加する文字列
	 */
	public void append(String key, String value) {
		append(key, value, ":");
	}

	/**
	 * 指定されたキーの値に指定された文字列を要素として追加する。
	 * この際、指定された分離文字を使用する。
	 * @param key プロパティのキー
	 * @param value 追加する文字列
	 * @param separator 文字列の分離文字
	 */
	public void append(String key, String value, String separator) {
		if(has(key)) value = get(key, "") + separator + value;
		set(key, value);
	}

	/**
	 * 指定されたキーの値の一致を判定する。
	 * @param key プロパティのキー
	 * @param value 比較対象の値
	 */
	public boolean match(String key, String value) {
		return value.equals(get(key));
	}
	/**
	 * キーとプロトコル情報を指定しキーの値の一致を判定する。
	 * @param key プロパティのキー
	 * @param bpinfo 比較対象のプロトコル情報
	 */
	public boolean match(String key, BPInfo bpinfo) {
		String value = get(key);
		String value2 = bpinfo.get(key);
		return (value != null) && (value2 != null) && value.equals(value2);
	}
	/**
	 * 指定されたキーが未定義または指定した値と一致するかを判定する。
	 * @param key プロパティのキー
	 * @param value 比較対象の値
	 * @return 判定値
	 */
	public boolean weakMatch(String key, String value) {
		return !has(key) || match(key, value);
	}
	/**
	 * 指定されたキーが定義されているか否かを判定する。
	 * @param key プロパティのキー
	 * @return キーが定義されている場合はtrue
	 */
	public boolean has(String key) {
		return propMap.containsKey(key);
	}
	/**
	 * 指定されたキーが指定された値を含むか否かを判定する。
	 * @param key プロパティのキー
	 * @param value 要素の値
	 */
	public boolean include(String key, String value) {
		return include(key, value, ":");
	}
	/**
	 * 指定されたキーが指定された値を含むか否かを判定する。
	 * @param key プロパティのキー
	 * @param value 要素の値
	 * @param separator 文字列の分離文字
	 */
	public boolean include(String key, String value, String separator) {
		String[] array = getArray(key, separator);
		if(array != null) {
			for(String e : array) {
				if(e.equals(value)) return true;
			}
		}
		return false;
	}

	/**
	 * 特殊文字のエスケープ処理：
	 * <p>スペース、タブ、改行を含む文字列を、これらを含まない文字列に変換する。
	 * スペースは `~' で表す。
	 * タブ、改行、バックスラッシュ、`~'は、バックスラッシュでエスケープする。
	 * <pre>
	 * 例: Takashi Yamada	45\5 --> Takashi~Yamada\t45\\5
	 * </pre>
	 */
	private static String escape(String value) {
		if(value == null) return null;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch(c) {
			case ' ':  sb.append('~'); break;
			case '~':  sb.append("\\~"); break;
			case '\\': sb.append("\\\\"); break;
			case '\t': sb.append("\\t"); break;
			case '\n': sb.append("\\n"); break;
			default:   sb.append(c); break;
			}
		}
		return sb.toString();
	}
	/**
	 * 特殊文字のアンチエスケープ処理：
	 * <p> escape(value)でエスケープ処理した文字列を元の文字列に復元する。
	 * <pre>
	 * 例: Takashi~Yamada\t45\\5 --> Takashi Yamada	45\5
	 * </pre>
	 */
	private static String antiEscape(String value) {
		if(value == null) return null;
		StringBuilder sb = new StringBuilder();
		boolean inEscape = false;
		for(int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if(inEscape) {
				switch(c) {
				case 't': sb.append('\t'); break;
				case 'n': sb.append('\n'); break;
				default:  sb.append(c); break;
				}
				inEscape = false;
			} else {	
				switch(c) {
				case '\\': inEscape = true; break;
				case '~':  sb.append(' '); break;
				default:   sb.append(c); break;
				}
			}
		}
		return sb.toString();
	}
}
