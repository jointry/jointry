package jp.ac.aiit.jointry.services.broker.core;

import jp.ac.aiit.jointry.services.broker.core.Monitor;
import jp.ac.aiit.jointry.services.broker.core.URLInfo;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.io.IOException;
import jp.ac.aiit.jointry.services.broker.util.Util;

/**
 * GetAgent: HTTP GET メソッド処理の実装
 */
public class GetAgent extends HttpBase {

    public GetAgent(Monitor monitor) {
        setMonitor(monitor);
    }

    /**
     * 指定したURLのコンテンツを URL#openStream() で取得し表示する。
     *
     * @param urlpath URLパス名
     */
    public void getContent(String urlpath) {
        try {
            URL url = new URL(urlpath);
            String text = filter(Util.readTextEx(url));
            monitor.viewText(url.getPath(), text);
            println("=== Get Content from: " + urlpath);
            if (msglog()) {
                print(text);
                println("===== EOF");
            }
        } catch (Exception e) {	// IOException, MalformedURLException
            monitor.showStatus("Not Found: " + urlpath);
            printStackTrace(e, null);
        }
    }

    private String filter(String text) {
        int index = text.indexOf("<html");
        if (index <= 0) {
            return text;
        }
        return text.substring(index);
    }

    /**
     * 指定したURLから HTTP の GET で テキストデータを取得し、
     * Monitor#viewText(path,text)を使って、モニタに通知する。
     *
     * @param urlpath URLパス名
     */
    public void handleHttpGet(String urlpath) {
        assert trace("==> handleHttpGet: " + urlpath);
        try {
            // 指定したURLからホスト名、ポート番号、パス名等を取得する
            URLInfo u = new URLInfo(urlpath);
            assert trace(u.toString());
            // HTTP の GET でテキストデータを取得する。
            String text = getTextData(u);
            if (msglog() && text != null) {
                println("*** Response Body:");
                print(text);
                println("=== End of Response Body");
            }
            if (text != null && !text.isEmpty()) {
                monitor.viewText(u.getPath(), text);
            } else {
                monitor.showStatus("Not Found: " + u.getHost() + ":"
                                   + u.getPort() + u.getPath());
            }
        } catch (MalformedURLException e) {
            printStackTrace(e, null);
        } finally {
            assert trace("<== handleHttpGet");
        }
    }

    /**
     * 指定された URL から HTTP GET でテキストデータを取得する。 レスポンスのヘッダ部とボディ部をモニタに表示することができる。
     *
     * @param u URL情報
     * @return レスポンスのボディ部のテキストデータ
     */
    private String getTextData(URLInfo u) {
        try {
            assert trace("==> getTextData");
            setSocketWith(u);
            sendRequest(HTTP_GET, null, u);

            // read the header info.
            StringBuilder header = new StringBuilder();
            String line;
            socket.setSoTimeout(RESPONSE_TIMEOUT);
            while ((line = receiveMessage()) != null && !line.isEmpty()) {
                header.append(line + "\n");
            }
            if (msglog()) {
                println("*** Response Header:");
                println(header.toString());
                println("==== End of Response Header");
            }
            if (line == null) {
                return null;
            }

            // filter elements after HTML 3.2
            assert trace("=== filter response body");
            while ((line = receiveMessage()) != null
                   && (line.startsWith("<?") || line.startsWith("<!"))) {
                if (msglog()) {
                    println("# " + line);
                }
            }
            if (line == null) {
                return null;
            }

            // read the message body
            StringBuilder body = new StringBuilder();
            body.append(line + "\n");
            assert trace("=== get response body");
            while ((line = receiveMessage()) != null) {
                body.append(line + "\n");
            }
            return body.toString();

        } catch (Exception e) {
            // UnknownHostException, SocketException, IOException
            printStackTrace(e, null);
        } finally {
            close();
            assert trace("<== getTextData");
        }
        return null;
    }

    /**
     * ソケットのリーダからメッセージを１行受信する。 相手のソケットがクローズされるか、タイムアウトの場合 null を返す。
     */
    private String receiveMessage() throws IOException {
        try {
            return reader.readLine();
        } catch (SocketTimeoutException e) {
            println("@@@ receiveMessage: " + e.getMessage());
            return null;
        }
    }
}
