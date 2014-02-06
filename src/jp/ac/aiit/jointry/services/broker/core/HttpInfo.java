package jp.ac.aiit.jointry.services.broker.core;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * HTTPプロトコルのリクエストとレスポンスの情報を管理する。
 * メッセージフォーマットは以下の通り。
 * <pre>
 * GET /index.html HTTP/1.1     # 先頭行
 * Host: localhost:8080
 * ...                          # ここまでヘッダ部
 *                              # 空白行
 * VAR1 = VALUE1
 * ...                          # ここまで本体部
 *                              # 空白行
 * </pre>
 */

public class HttpInfo extends BPInfo {
	private final static String PARAM_FMT = "(\\w+)\\s*=\\s*(.+)\\s*";
	private final static Pattern paramPat = Pattern.compile(PARAM_FMT);
	private String uri;				// /index.html
	private String[] header;		// Host: localhost:8080
	private String[] body;			// VAR1=VALUE1&...
	public String getURI() { return uri; }

	/**
	 * 受信したHTTPプロトコルのテキストからHTTPプロトコル情報を取得する。
	 * @param contents HTTPプロトコルのテキスト行のリスト。
	 * <p>先頭行はリクエストのメソッドまたはレスポンスのエラーコード。
	 */
	HttpInfo(List<String> contents) {
		// 先頭行の解析
		String req = contents.get(0);
		if(req.startsWith("HTTP")) {	// HTTP Resoponse
			dialogId = req;
		} else {						// GET /index.html HTTP/1.1
			String[] s = req.split("\\s+");
			dialogId = s[0];
			uri = s[1].trim();
			if(uri.equals("/")) uri = "index.html";
		}

		// ヘッダ部の抽出：
		// 先頭行を含み空白行の直前までのテキストをヘッダ部とする。
		int n = contents.size();
		int index = 0;
		while(index < n) {
			if(contents.get(index).isEmpty()) break;
			index++;
		}
		header = new String[index];
		for(int i = 0; i < index; i++)
			header[i] = contents.get(i);
		
		// 本体部の解析：
		// bodyにテキスト行を保存しパラメータ(VAR=VALUE)を登録する。
		index++;
		if(index < n) {
			body = new String[n - index];
			for(int i = index; i < n; i++) {
				String param = contents.get(i);
				body[i-index] = param;
				Matcher m = paramPat.matcher(param);
				if(m.find()) set(m.group(1),m.group(2));
			}
		}
	}
	@Override public String toString() {
		return "HttpInfo[" + dialogId + ", " + uri + "]\n" + params();
	}

	public boolean demandServer() {
		return match(USER_ROLE, SERVER);
	}
	public boolean demandClient() {
		return match(USER_ROLE, CLIENT);
	}
}
