package jp.ac.aiit.jointry.services.broker.core;

/**
 * Common インタフェースでは、Brokerの共通定数を定義する。
 * <p>
 * NSBrokerでは、送受信されるメッセージはすべて文字列であり
 * 行単位で処理される。Commmon インタフェースでは、
 * これらメッセージの構成要素となるプロパティの語彙を定義している。
 * <p>
 * 定数名のプレフィックスは以下の様に語彙の種別を示す。
 * <ul>
 * <li> M_: メッセージの種別を表す先頭文字
 * <li> K_: プロパティのキー
 * <li> V_: プロパティの値
 * </ul>
 * Commonで定義するプロパティのキーは、メッセージのキーワード（予約語）に
 * 相当するものであり、アプリケーションで定義するキーと区別するために
 * 名前の先頭にアットマーク`@'を付けている。
 */
public interface Common {

	static final String CRLF = "\n"; // "\r\n"

	static final String HTTP200 = "HTTP/1.1 200 OK";
	static final String HTTP400 = "HTTP/1.1 400 Bad Request";
	static final String HTTP401 = "HTTP/1.1 401 Unauthorized";
	static final String HTTP402 = "HTTP/1.1 402 Payment Required";
	static final String HTTP403 = "HTTP/1.1 403 Forbidden";
	static final String HTTP404 = "HTTP/1.1 404 Not Found";
	static final String HTTP405 = "HTTP/1.1 405 Method Not Allowed";

	static final String PORT_ID = "8081";
	static final String SERVICE_URL = "http://localhost:8081/home.html";
	static final String UNKNOWN_USER = "UNKNOWN";

	static final int RESPONSE_TIMEOUT = 2000;		// 2 sec
	static final int REQUEST_TIMEOUT = 2000;		// 2 sec
	static final int SERVER_TIMEOUT = 60*60*1000;	// 60 min
	static final int PROXY_TIMEOUT = 60*60*1000;	// 60 min
	static final int CHAT_TIMEOUT = 10*60*1000;		// 10 min
	static final int INVOKE_TIMEOUT = 500;			// 0.5 sec
	static final int REQUEST_MAX = 100;				// GETリクエストの最大数

	// HTTPリクエストに関する情報
	static final String HTTP_GET        = "GET";			// HTTP GET
	static final String HTTP_POST       = "POST";			// HTTP POST
	static final String USER_ROLE       = "user_role";		// ユーザ役割
	static final String   SERVER        = "server";			// サーバ
	static final String   CLIENT        = "client";			// クライアント
	static final String   UNKNOWN       = "unknown";		// 不明
	static final String SERVICE_ID      = "service_id";		// サービスID
	static final String   GET_SERVICE   = "get_service";	// HTML取得
	static final String   CHAT_SERVICE  = "chat_service";	// チャット
	static final String PROXY_FQCN      = "proxy_fqcn";		// ProxyのFQCN
	static final String USER_ID         = "user_id";		// ユーザID
	static final String PASSWORD        = "password";		// パスワード

	// メッセージの種別を示す先頭文字
	static final char M_QUERY = '?';				// QUERY
	static final char M_ANSWER = '%';				// ANSWER
	static final char M_NOTIFY = '!';				// NOTIFY
	static final char M_COMMAND = '>';				// COMMAND
	static final char M_PLAIN = '@';				// PLAIN

	// Brokerが初期実装しているDialogのダイアログ識別名
	static final String D_SYSTEM = "system";		// システムパラメータ問合せ
	static final String D_PING = "ping";			// メッセージの送受信時刻
	static final String D_FTP = "ftp";				// ファイル転送
	static final String D_VIEW = "view";			// ファイル転送・表示
	static final String D_SHELL = "shell";			// シェルコマンド実行
	static final String D_EVAL = "eval";			// スクリプト評価ダイアログ
	static final String D_EXECUTE = "execute";		// ダイアログクラスの実行

	// 共通で使用するKEYとVALLUE
	static final String K_ID = "@id";				// ダイアログID
	static final String K_SERIAL_NO = "@serial_no";// メッセージの通し番号
	static final String K_QUERY_SEQ = "@query_seq";	// QUERYの通し番号
	static final String K_FROM = "@from";			// 送信元のproxyID
	static final String K_TO = "@to";				// 送信先のproxyID
	static final String K_RELAY = "@relay";			// 中継送信の定義
	static final String K_RESULT = "@result";		// QUERYの実行結果
	static final String   V_OK = "OK";				// 正常終了
	static final String   V_NG = "NG";				// 異常終了
	static final String K_TIMESTAMP = "@timestamp";	// タイムスタンプ
	static final String K_TYPE = "@type";			// コンテンツ種別
	static final String   V_TEXT= "text";			// テキスト
	static final String   V_IMAGE= "image";			// 画像
	static final String   V_BINARY= "binary";		// バイナリ

	// systemダイアログで使用するKEY(パラメータ種別)
	static final String K_PROXY = "@proxy";			// proxyID
	static final String K_SERVER = "@server";		// サーバのproxyID
	static final String K_MEMBERS = "@members";		// メンバのproxyIDのリスト
	static final String K_PROXY_NO = "@proxy_no";	// Proxyの個数
	static final String K_PROXY_INFO = "@proxy_info"; 	// Proxy情報のリスト
	static final String K_SERVER_INFO = "@server_info";	// サーバ情報のリスト
	static final String K_MEMBER_INFO = "@member_info";	// メンバ情報のリスト
	static final String K_NEXT_PROXY = "@next_proxy";	// 次のproxyID
	static final String K_TIME_OFFSET = "@time_offset";	// 時刻の補正
	static final String K_TIME_SYNC = "@time_sync";	// 時刻の同期

	// pingダイアログで使用するKEY(パラメータ種別)
	static final String K_TIME_QUERY = "@time_query";	// QUERYの発信時刻
	static final String K_TIME_ONQUERY = "@time_onquery";	// QUERYの受信時刻
	static final String K_TIME_ONANSWER = "@time_onanswer";	// ANSWERの受信時刻
	static final String K_TIME_NOTIFY = "@time_notify";		// NOTIFYの発信時刻
	static final String K_TIME_ONNOTIFY = "@time_onnotify";	// NOTIFYの受信時刻

	// ftpダイアログとviewダイアログで使用するKEYとVALUE
	static final String K_FILE = "@file";			// ファイル名
	static final String K_SINK = "@sink";			// 転送時一時ファイル名
	static final String K_SIZE = "@size";			// ファイルサイズ
	static final String K_TITLE = "@title";			// コンテンツタイトル

	// shellダイアログで使用するKEY
	static final String K_COMMAND = "@command";		// シェルコマンド
	static final String K_EXITCODE = "@exitcode";	// コマンドの戻り値
	static final String K_CONTENT = "@content";		// コンテンツ

	// スクリプト評価ダイアログで使用するKEY(パラメータ種別)
	static final String K_SCRIPT = "@script";		// スクリプト文字列
	static final String K_RETURN = "@return";		// メソッド呼出しの戻り値

	// executeダイアログで使用するKEY
	static final String K_FQCN = "@fqcn";			// ダイアログクラスのFQCN

}
