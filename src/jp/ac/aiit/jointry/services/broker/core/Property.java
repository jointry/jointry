package jp.ac.aiit.jointry.services.broker.core;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Propertyはキーと値をもち、文字列表現されたプロパティ値の適正判定や、
 * プロパティ値の設定・参照の機能を提供する。
 * <p>
 * DInfoのプロパティと相互に値の参照・設定を行うメソッドaccessを提供する。
 */
public class Property implements Common {

	public static void main(String[] args) {
		Counter counter = new Counter(K_NEXT_PROXY, 201);
		pp(counter.nextID());			// 201
		pp(counter.nextID());			// 202
		DInfo dinfo = new DInfo(D_SYSTEM);
		dinfo.set(K_NEXT_PROXY, 301);
		pp(counter.access(dinfo));		// true # dinfo -> counter
		pp(counter.ivalue());			// 301
		pp(counter.nextID());			// 301
		pp(dinfo.get(K_NEXT_PROXY));	// 301
		dinfo.define(K_NEXT_PROXY);
		pp(dinfo.get(K_NEXT_PROXY));	// null
		pp(counter.access(dinfo));		// true # counter -> dinfo
		pp(dinfo.get(K_NEXT_PROXY));	// 302
		dinfo.set(K_NEXT_PROXY, 0);
		pp(counter.access(dinfo));		// false # dinfo -> counter
		pp(counter.ivalue());			// 302
	}

	static void pp(Object obj) { System.out.println(obj); }

	private String key;
	protected String value;
	private Pattern validPat;

	public Property(String key) { this.key = key; }

	public String key() { return key; }

	public String value() { return value; }

	/**
	 * ダイアログ情報のプロパティ定義に基づきプロパティの値の参照・設定を行う。
	 * @param dinfo ダイアログ情報
	 */
	public boolean access(DInfo dinfo) {
		if(dinfo.has(key)) {
			String v = dinfo.get(key);
			if(v != null) {
				// vが適正な値ならばプロパティ値に設定する。
				return value(v);
			} else {
				// このプロパティの値をダイアログ情報のプロパティに設定する。
				dinfo.set(key, this.value());
			}
		}
		return true;
	}

	/**
	 * valueが適正な値ならばプロパティ値に設定する。
	 * @param value 設定する値
	 * @return valueが適正な値であればtrue
	 */
	public boolean value(String value) {
		if(isValid(value)) {
			setValue(value);
			return true;
		}
		return false;
	}

	/**
	 * 適正チェックを行わずに値を設定する。
	 * @param value 設定する値の文字列表現
	 */
	protected void setValue(String value) { this.value = value; }

	/**
	 * 適正な値の正規表現からパターンを設定する。
	 * @param regex 適正な値の正規表現
	 */
	public void setPattern(String regex) {
		validPat = Pattern.compile(regex);
	}

	/**
	 * 適正な値かの判定を行う。
	 * @param value 判定対象の文字列
	 * @return 適正な値であればtrue
	 */
	public boolean isValid(String value) {
		if(validPat == null) return true;
		Matcher m = validPat.matcher(value.trim());
		return m.find();
	}

	/*================================================================*/
	/**
	 * 参照だけ可能なプロパティクラス
	 */
	static public class ReadOnlyProperty extends Property {
		public ReadOnlyProperty(String key) { super(key); }
		
		public boolean isValid(String value) { return false; }
	}

	/*================================================================*/
	/**
	 * 整数値を値とするプロパティクラス
	 */
	static public class IntProperty extends Property {
		protected int ivalue = 0;

		public IntProperty(String key, int ivalue) {
			super(key);
			this.ivalue = ivalue;
			setPattern("^[+-]?\\d+$");
		}

		@Override public String value() { return ""+ivalue; }

		@Override protected void setValue(String value) {
			ivalue = parseInt(value);
		}

		public int ivalue() { return ivalue; }

		protected int parseInt(String value) {
			return Integer.parseInt(value.trim());
		}
	}

	/*================================================================*/
	/**
	 * カウンタプロパティクラス：１以上の値を順番に生成する。
	 */
	static public class Counter extends IntProperty {
		public Counter(String key, int ivalue) { super(key, ivalue); }

		@Override public boolean isValid(String value) {
			if(!super.isValid(value)) return false;
			return parseInt(value) > 0;
		}

		public int nextID() { return ivalue++; }
	}

}
