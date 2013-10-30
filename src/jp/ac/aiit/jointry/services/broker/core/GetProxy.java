package jp.ac.aiit.jointry.services.broker.core;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import jp.ac.aiit.jointry.services.broker.util.Util;

class GetProxy extends Proxy {
	public GetProxy(HttpInfo hinfo) { super(hinfo); }

	/**
	 * 連続する GET リクエストを処理する。
	 */
	@Override protected void handle() throws IOException {
		for(int i=1; httpInfo.dialogId.equals(HTTP_GET) && i<=REQUEST_MAX; i++) {
			assert trace(""+ i + ": "+ httpInfo);
			handleGET(httpInfo.getURI());
			httpInfo = receiveRequest(); // リクエストがないとIOExceptionが発生
		}
	}

	private void handleGET(String uri) throws IOException {
		if(msglog()) println("*** " + theClass() + " sent:");
		File file = new File(broker.getDocRoot(), uri);
		if(file.exists()) {
			String text = Util.readTextEx(file);
			send(HTTP200);
			send("Server: NSP");
			send("Date: " + new Date());
			send("Content-Type: text/html; charset=Shift-JIS");
			send("Last-modified: " + new Date(file.lastModified()));
			send("Content-length: " + file.length());
			send("Connection: close");
			send("");
			sender.print(text);
			sender.flush();
			if(msglog()) { print(text); println("=== End of Message"); }
		} else {
			reply(HTTP404);
		}
	}
}
