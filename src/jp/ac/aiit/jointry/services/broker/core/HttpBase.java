package jp.ac.aiit.jointry.services.broker.core;

import jp.ac.aiit.jointry.services.broker.core.PingDialog;
import jp.ac.aiit.jointry.services.broker.core.WorkerBase;
import jp.ac.aiit.jointry.services.broker.core.URLInfo;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.awt.image.BufferedImage;
import jp.ac.aiit.jointry.services.broker.util.Base64;
import jp.ac.aiit.jointry.services.broker.util.Util;

/**
 * HttpBaseは HTTPポートを使ってSoketによる双方向通信を行うための機能を
 * 集約したクラスであり、Proxyと、Agent、Dialogの共通のベースクラスである。
 * <p>
 * HttpBaseは、通信相手のHttpBaseオブジェクトから送信されるメッセージを
 * 待ち受ける処理をWorkerBaseから継承するスレッドに担当させている。 メッセージ待ち受け処理は、通信相手から送信されるメッセージに対して、
 * メッセージの種別に応じたメソッドを呼び出す。 メッセージは、QUERY, ANSWER, NOTIFY, COMMAND, PLAIN の5種類がある。
 * <p>
 * QUERYは ANSWERの返信を待つメッセージであり、 正常に処理されるとANSWERメッセージが返信される。
 * NOTIFYは一方向の送信を行うメッセージであり送達確認を必要としない。
 * COMMANDはShellコマンドとして評価され、その結果標準出力へ出力されたテキストが テキストコンテンツとして返される。
 * PLAINはメッセージとしての空白行をデータコンテンツの区切りを表す空白行と 区別するために導入した。
 * <p>
 * 各メッセージはそれぞれ先頭の１文字によって区別される。
 * `?'はQUERY、`%'はANSWER、`!'はNOTIFY、`>'はCOMMAND、`@'はPLAINと解釈される。
 * それ以外の文字のメッセージが連続する場合は最初のメッセージ以外は 通信不全による雑音として読み飛ばされる。
 * <p>
 * Brokerは QA型通信とNOTIFY型通信の２種類の通信形式を提供している。 これらのAPIはDialogインタフェースで定義されている。
 * QA型通信は、双方向通信のための基本機能として、QUERYの送信、 QUERYの受信とANSWERの返信、ANSWERの受信を行うAPIを提供する。
 * １つのQUERYの送信後は、そのQUERYのANSWERを受信し処理が完了するまで 新たなQUERYを送信することはできない。
 * HttpBaseはANSWERの受信待ち状態を管理するために送信したQUERYの情報を保持する。
 * NOTIFY型通信は送達確認を行わない通信方式であり、NOTIFYの送信とNOTIFYの受信を を行うAPIを提供する。
 * <p>
 * QUERY、ANSWER、NOTIFYの各メッセージは以下の書式に従う。
 * <pre>
 * ('?' | '%' | '!') dialog-name { key [ '=' value ] }
 * { data-string NL }
 * NL
 * </pre>
 *
 * @author C.Akiguchi
 */
public class HttpBase extends WorkerBase {

    public static final String BROKER_CHARSET = "Shift-JIS";	// "UTF-8";

    protected Socket socket;		 // 双方向通信を行うソケット
    protected BufferedReader reader; // ソケットの入力ストリームに対応するリーダ
    protected PrintWriter sender;	 // ソケットの出力ストリームに対応するライタ

    protected HttpBase() {
    }

    protected HttpBase(Socket socket) {
        setSocket(socket);
    }

    /**
     * HttpBaseの拡張クラスの種別を表す文字
     */
    public char mark() {
        return 'H';
    }

    public boolean isProxy() {
        return false;
    }

    public boolean isAgent() {
        return false;
    }

    /**
     * 持続的ダイアログの管理
     */
    private List<String> continuousDialogs;

    synchronized public void addDialog(String did) {
        if (continuousDialogs == null) {
            continuousDialogs = new ArrayList<String>();
        }
        continuousDialogs.add(did);
    }

    public void clearDialogs() {
        if (continuousDialogs == null) {
            return;
        }
        for (String did : continuousDialogs) {
            DialogBase.removeDialog(did);
        }
        continuousDialogs = null;
    }

    /**
     * 持続的ダイアログのIDを返す。
     */
    protected String continuousDialogID() {
        return "0";
    }

    /**
     * ====== HTTPプロトコルでの通信の確立と解除のためのAPI =======<br>
     * URLを使った通信の確立、ソケットの設定、他のHttpBaseオブジェクトからの 実行環境の引き継ぎ、通信の解除等を行う。
     */
    /**
     * URLInfoのホストとポートを使ってソケットを生成し、そのソケットの 入力ストリームと出力ストリームを設定する。
     *
     * @param u URLの情報（ホストとポートを含む）
     */
    void setSocketWith(URLInfo u) throws IOException {
        setSocket(new Socket(u.getHost(), u.getPort()));
    }

    /**
     * ソケットとソケットの入力ストリームと出力ストリームを設定する。
     *
     * @param socket 双方向通信で使用するソケット
     */
    synchronized protected void setSocket(Socket socket) {
        this.socket = socket;
        try {
            reader = Util.createReader(socket, BROKER_CHARSET);
            sender = Util.createWriter(socket, BROKER_CHARSET);
        } catch (IOException e) {
            printStackTrace(e, null);
        }
    }

    /**
     * 他のHttpBaseオブジェクトから実行環境を引き継ぐ。 引き継ぐ情報としては、実行監視モニタ、ソケット、入力／出力ストリーム、
     * 作業ファイルを格納するディレクトリパスがある。
     *
     * @param originator 環境引継ぎの対象となるHttpBaseオブジェクト
     */
    synchronized public void takeOver(HttpBase originator) {
        monitor = originator.monitor;
        socket = originator.socket;
        reader = originator.reader;
        sender = originator.sender;
        docRoot = originator.docRoot;
        workRoot = originator.workRoot;
    }

    /**
     * ソケットをクローズし通信を解除する。
     */
    synchronized public void close() {
        if (socket == null) {
            return;
        }
        try {
            socket.close();
            socket = null;
            clearDialogs();
        } catch (IOException e) {
            printStackTrace(e, null);
        }
    }

    /**
     * ============== HTTPプロトコル処理のためのAPI ===============<br>
     * HTTPリクエストの発行、受信、レスポンスの返信等を行う。
     */
    /**
     * HTTP リクエストを発行する。主にAgent が使用する。 発行したHTTP リクエストをモニタに表示することができる。
     *
     * @param method メソッド名
     * @param paramMap パラメータのマップ
     * @param u URL情報
     */
    synchronized void sendRequest(String method, Map<String, String> paramMap,
                                  URLInfo u) throws IOException {
        if (msglog()) {
            println("*** " + theClass() + " sends a request:");
        }
        send(method + " " + u.getPath() + " HTTP/1.1");
        send("Host: " + u.getHost() + ":" + u.getPort());
        send("");
        if (paramMap != null) {
            for (Map.Entry<String, String> e : paramMap.entrySet()) {
                send(e.getKey() + " = " + e.getValue());
            }
        } else {
            send("");
        }
        sender.flush();
    }

    /**
     * 要求メッセージを読み取り、HttpInfoとして返す。主にProxyが使用する。 HttpInfo
     * には、メソッドとURI、メッセージのヘッダとボディが含まれる。 応答メッセージをモニタ表示できる。
     *
     * @return 読み取った要求メッセージ
     * @exception IOException 要求が無い場合
     */
    protected HttpInfo receiveRequest() throws IOException {
        return new HttpInfo(receiveHTTP(REQUEST_TIMEOUT, "request"));
    }

    /**
     * HTTPメッセージを読み取り、HttpInfoとして返す。 HttpInfo には、メソッドとURI、メッセージのヘッダとボディが含まれる。
     *
     * @return 読み取ったHTTPメッセージ
     * @exception IOException 要求が無い場合
     */
    protected List<String> receiveHTTP(int timeout, String message)
            throws IOException {
        // リクエスト行の受信をタイムアウトまで待つ。
        socket.setSoTimeout(timeout);
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("no message");
        }

        // 受信したリクエスト行をリストに保存する。
        List<String> contents = new ArrayList<String>();
        contents.add(line);
        // メッセージヘッダ以降は、受信済みのメッセージをすべて読み込む。
        while (reader.ready() && (line = reader.readLine()) != null) {
            contents.add(line);
        }
        if (msglog()) {
            println("*** " + theClass() + " received a " + message + ":");
            for (String s : contents) {
                logRead(s);
            }
        }
        return contents;
    }

    /**
     * HTTP レスポンスを送信する。主にProxyが使用する。
     *
     * @param response レスポンス文字列
     */
    synchronized protected void reply(String response) {
        send(response);
        sendMessage("");
    }

    /**
     * ============== メッセージ送信のための基本API ===============<br>
     * QUERY, ANSWER, NOTIFY, PLAINの各種メッセージを送信する。
     */
    /**
     * 指定されたダイアログ情報をQUERYとして送信する。
     *
     * @param dinfo ダイアログ情報
     */
    public void sendQuery(DInfo dinfo) {
        assert timestamp(dinfo, "Q>");
        sendMessage(M_QUERY + dinfo.message());
    }

    /**
     * 指定されたダイアログ情報をANSWERとして送信する。
     *
     * @param dinfo ダイアログ情報
     */
    public void sendAnswer(DInfo dinfo, String result) {
        dinfo.set(K_RESULT, result);
        assert timestamp(dinfo, "A>");
        sendMessage(M_ANSWER + dinfo.message());
    }

    /**
     * 指定されたダイアログ情報をNOTIFYとして送信する。
     *
     * @param dinfo ダイアログ情報
     */
    public void sendNotify(DInfo dinfo) {
        assert timestamp(dinfo, "N>");
        sendMessage(M_NOTIFY + dinfo.message());
    }

    /**
     * 指定されたメッセージをPLAINとして送信する。
     *
     * @param message メッセージ文字列
     */
    public void sendPlain(String message) {
        sendMessage(M_PLAIN + message);
    }

    /**
     * メッセージの送信： メッセージのモニタ表示と実際の送信を行う。
     *
     * @param message メッセージ文字列
     */
    synchronized public void sendMessage(String message) {
        send(message);
        sender.flush();
    }

    /**
     * メッセージ送信プリミティブ：
     * <p>
     * メッセージを１行の文字列として送信し、必要に応じてモニタに表示する。
     * flush()またはsendEOF()が実行されるまで、実際の送信は行われない。
     *
     * @param message 送信する文字列
     */
    synchronized protected void send(String message) {
        sender.println(message);
        if (msglog()) {
            logSend(message);
        }
    }

    /**
     * 送信データの終了を示す空白行を送信し、全体を強制送信する。
     */
    synchronized protected void sendEOF() {
        sender.println("");
        sender.flush();
    }

    /**
     * ============ コンテンツデータ送受信のためのAPI =============<br>
     * データ種別やファイル名等の属性情報をメッセージとして送信し、 それに続いてテキストデータや画像データ等のコンテンツデータを送信する。
     * <p>
     * テキストデータは各行を1つのPLAINとして連続して送信する。 画像データはBase64でエンコードしテキストデータとして送信する。
     * データの終了は空白行とする。
     * <p>
     * コンテンツデータの受信はこの逆の過程をとる。 即ちメッセージ受信後、コンテンツデータを空白行まで読み込み
     * 文字列リストを生成する。その後は受信メッセージに含まれる属性情報から 元のコンテンツを再現する。
     */
    /**
     * テキストをNOTIFY送信する。
     *
     * @param title テキストタイトル
     * @param text 送信するテキスト
     * @return 送信に成功したらtrue
     */
    synchronized public boolean notifyViewText(String title, String text) {
        notifyView(V_TEXT, title);
        sendText(text);
        return true;
    }

    /**
     * テキスト（改行コードを含む文字列）を送信する。 テキストの各行の行頭にはMS_PLAIN('@')をつける。 最終行の後に空白行を送信する。
     *
     * @param text 送信するテキストメッセージ
     */
    synchronized public void sendText(String text) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                sender.println(M_PLAIN + line);
            }
        } catch (IOException e) {
            printStackTrace(e, null);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
            }
            sendEOF();
        }
    }

    /**
     * NOTIFYによる画像データの送信処理： 画像データをjpg圧縮し、Base64でエンコードして送信する。
     *
     * @param title データタイトル
     * @param bimage 送信する画像データ
     * @return 送信に成功したらtrue
     */
    synchronized public boolean notifyViewImage(String title, BufferedImage bimage) {
        assert startTime("notifyViewImage starts");
        String fname = createImageFile(title, bimage);
        if (fname == null) {
            return false;
        }
        assert time("image written:");
        viewImageFile(fname);
        assert time("notifyViewImage done:");
        return true;
    }

    /**
     * 画像ファイルをNOTIFY送信する。
     *
     * @param fname 送信する画像ファイルのパス名
     */
    synchronized public void viewImageFile(String fname) {
        notifyView(V_IMAGE, fname);
        sendBinary(fname);
    }

    /**
     * バイナリファイルをBase64でエンコードし送信する。
     *
     * @param fname 送信するバイナリファイルのファイル名
     */
    synchronized public void sendBinary(String fname) {
        try {
            Base64.encodeWithWriter(fname, new BufferedWriter(sender));
        } catch (IOException e) {
            printStackTrace(e, null);
        } finally {
            sendEOF();
        }
    }

    /**
     * ファイルt転送・表示メッセージをNOTIFYとして送信する。
     *
     * @param type ファイルの種別(V_TEXT/V_IMAGE/V_BINARY)
     * @param fname ファイル名
     */
    private void notifyView(String type, String fname) {
        sendMessage(M_NOTIFY + D_VIEW + " "
                    + K_TYPE + "=" + type + " " + K_FILE + "=" + fname);
    }

    /**
     * ================ メッセージ受信のためのAPI =================<br>
     * メッセージ受信スレッドを起動し、メッセージの受付とメッセージ種別に 応じたディスパッチを行う。
     */
    /**
     * Thread#run() の実装：メッセージ受付とディスパッチを行う。
     */
    @Override
    public void run() {
        try {
            assert trace("*** starting " + theClass());
            handle();
        } catch (IOException e) { // socket closed
            println(this.toString() + ": " + e.getMessage());
        } catch (Exception e) {
            printStackTrace(e, "Agent#handle() terminated");
        } finally {
            assert trace("*** " + theClass() + " terminating");
            onClose();
        }
    }

    /**
     * メッセージ受付を終了した時に呼び出される。 拡張クラスでは、このメソッドをオーバライドすることによって セッション終了時の処理を記述できる。
     */
    protected void onClose() {
    }

    /**
     * メッセージの受付とメッセージ種別に応じたディスパッチを行う。
     */
    protected void handle() throws IOException {
        if (socket == null) {
            return;
        }
        socket.setSoTimeout(PROXY_TIMEOUT);
        String line;
        while ((line = reader.readLine()) != null) {
            if (msglog()) {
                logRead(line);
            }
            if (line.isEmpty()) {
                continue;
            }
            if (socket != null) {
                socket.setSoTimeout(RESPONSE_TIMEOUT);
            }
            switch (line.charAt(0)) {
                case M_QUERY:
                    queryReceived(line.substring(1));
                    break;	// ?
                case M_ANSWER:
                    answerReceived(line.substring(1));
                    break;	// %
                case M_NOTIFY:
                    notifyReceived(line.substring(1));
                    break;	// !
                case M_COMMAND:
                    onCommand(line.substring(1));
                    break;	// >
                case M_PLAIN:
                    onMessage(line.substring(1));
                    break;	// @
                default:
                    onMessage(line);
                    skipFollowers();
                    break;
            }
            if (socket != null) {
                socket.setSoTimeout(PROXY_TIMEOUT); // ★
            }
        }
    }

    /**
     * QUERYを受信しANSWERを送信する。
     * <p>
     * 受信した QUERYの正当性を確認し、正しい QUERYに対して ANSWERを返信する。 不正な QUERYに対してはエラーの種別を返信する。
     *
     * @param query 受信したQUERYメッセージ
     */
    private void queryReceived(String query) {
        DInfo dinfo = DInfo.parse(query);
        if (dinfo != null) {
            handleQA(dinfo);
        } else {
            sendMessage(M_ANSWER + "Bad Query: " + query);
        }
    }

    /**
     * 送信済みのQUERYに対する ANSWERの受信処理を行う。
     *
     * @param answer 受信したANSWERメッセージ
     */
    private void answerReceived(String answer) {
        DInfo dinfo = DInfo.parse(answer);
        if (dinfo != null) {
            if (dinfo.has(K_RELAY)) {	// K_RELAYの場合は素通しする。
                answerReceived(dinfo);
            } else {
                receiveAnswer(dinfo);
            }
        } else {
            printError("Bad Answer: " + answer);
        }
    }

    /**
     * NOTIFYの受信処理を行う。
     *
     * @param invoice 受信したNOTIFYメッセージ
     */
    private void notifyReceived(String invoice) {
        DInfo dinfo = DInfo.parse(invoice);
        if (dinfo != null) {
            notifyReceived(dinfo);
        } else {
            printError("Bad Notify: " + invoice);
        }
    }

    /**
     * COMMANDの受信処理を行う。 拡張クラスでオーバライドできる。
     *
     * @param command COMMAND行
     */
    protected void onCommand(String command) {
    }

    /**
     * 平文メッセージ(PLAIN)の受信処理を行う。 拡張クラスでオーバライドできる。
     *
     * @param message
     */
    protected void onMessage(String message) {
    }

    /**
     * 受信済みの後続するメッセージを読み飛ばす。 本通信方式では、メッセージは１行単位で断続的に送信されるものとしている。
     * 連続するメッセージは、メッセージの読みこぼしなどの送受信処理の失敗により、 コンテンツ(データ本体)が流出したものと考えられる。
     * 読み取りに失敗した大量のメッセージはここで読み捨てる。★
     */
    private void skipFollowers() throws IOException {
        int count = 0;
        String line;
        while (reader.ready() && (line = reader.readLine()) != null) {
            if (count < 1) {
                println(Util.date() + "\tunexpected message: " + line);
            } else if (count == 1) {
                println("...");
            }
            count++;
        }
        if (count != 0) {
            println(Util.date() + "\t" + count + " mesasge(s) skipped.\n");
        }
    }

    /**
     * ===== 自マシンと通信相手のマシンの時刻補正のためのAPI ======<br>
     */
    /**
     * 通信相手マシンの時刻を、自マシンの時刻に合わせる。
     */
    public void syncTime() {
        // 自マシンと通信相手マシンの時刻補正をクリアする。
        PingDialog.setTimeOffset(0);
        DInfo dinfo = new DInfo(D_PING);
        dinfo.set(K_TIME_OFFSET, 0);
        invoke(dinfo);

        // 通信相手マシンの時刻を自マシンの時刻に合わせる。
        dinfo.set(K_TIME_OFFSET, getTimelag());
        invoke(dinfo);
    }

    /**
     * 自マシンの時刻を、通信相手マシンの時刻に合わせる。
     */
    public void syncMyTime() {
        PingDialog.setTimeOffset(-getTimelag());
    }

    /**
     * 自マシンの時刻と通信相手のマシンの時刻の時間差をミリ秒で求める。
     *
     * @return (自マシンの時刻 - 通信相手のマシンの時刻) [msec]
     */
    public int getTimelag() {
        int minDelta = Integer.MAX_VALUE;
        int timelag = 0;
        for (int i = 0; i < 5; i++) {
            DInfo answer = invoke(new DInfo(D_PING));
            int query = answer.getTime(K_TIME_QUERY);
            int onquery = answer.getTime(K_TIME_ONQUERY);
            int onanswer = answer.getTime(K_TIME_ONANSWER);
            if (onanswer == query) {
                return query - onquery;
            }
            if (onanswer - query < minDelta) {
                minDelta = onanswer - query;
                timelag = query - onquery;
            }
        }
        // ping の発信時刻と受信時刻に
        println("timelag error: " + minDelta);
        return timelag;
    }

    /**
     * ==== QUERY/ANSWER/NOTIFY処理と同期呼び出しのためのAPI ======<br>
     * QUERYの発行後は対応するANSWERが返信されるまで次のQUERYを 発行できないように制御される。
     * <p>
     * evalQuery(query)メソッドを使うとこのHttpBaseから発行された各QUERYには
     * 連番が振られ、ANSWERがどのQUERYに対応するものかが判定できる。
     * evalQuery(query)メソッドによるQUERYの発行はANSWERを待つ制御をしない。
     * すなわちQUERY発行後は即座に制御が呼出し元に返る。 アプリケーション（あるいはそのユーザが）ANSWERの返信を待たずに
     * 次のQUERYを発行するとエラーとなる。
     * <p>
     * invoke(query)メソッドはevalQuery(query)メソッドを使用した
     * 同期型の呼出しであり、QUERY発行後ANSWERの返信を待って結果を返す。
     */
    private DInfo queryInfo;		// 発行し受信待ちのQUERYのダイアログ情報
    private int querySeq = 0;		// QUERY番号
    private Object invokeLock = new Object();
    private Thread invokerThread;
    private DInfo answerInfo;		// invokeに渡すANSWERのダイアログ情報
    private int invokeTimeout = INVOKE_TIMEOUT;	// ANSWER待ちのタイムアウト時間

    public void setInvokeTimeout(int timeout) {
        invokeTimeout = timeout;
    }

    public int getInvokeTimeout() {
        return invokeTimeout;
    }

    /**
     * ANSWER待ちのQUERYをリセットする。 ANSWERを取りこぼすと次のQUERYの発行ができなくなる。
     * このような場合に、再度QUERY発行が可能な状態にできる。
     */
    public void resetQuery() {
        queryInfo = null;
        answerInfo = null;
    }

    /**
     * ANSWER待ちのQUERYをリセットし、QUERY番号を指定した値に設定する。
     */
    public void resetQuery(int querySeq) {
        this.querySeq = querySeq;
        resetQuery();
    }

    /**
     * invokeの戻り値のダイアログ情報を表示可否フラグを設定する
     */
    public void setReturnFlag(boolean b) {
        returnFlag = b;
    }
    private boolean returnFlag = false;

    /**
     * QUERYを同期呼び出しで実行し、その実行結果を表示する。
     *
     * @param query QUERY
     * @return QUERYの実行結果
     */
    synchronized public boolean invokeQuery(String query) {
        DInfo answer = invoke(query);
        if (answer != null) {
            if (returnFlag) {
                println(answer.params("\t= ", "\n"));
            }
            return answer.match(K_RESULT, V_OK);
        }
        printError("Bad Invoke: " + query);
        return false;
    }

    /**
     * Shellコマンドを同期呼び出しで実行し、その実行結果を表示する。
     *
     * @param command Shellコマンド
     * @return Shellコマンドの実行結果
     */
    synchronized public boolean invokeShell(String command) {
        DInfo dinfo = new DInfo(D_SHELL);
        dinfo.set(K_COMMAND, command);
        dinfo.set(K_ID, "shell." + continuousDialogID());
        DInfo answer = invoke(dinfo);
        if (answer == null) {
            println("invoke failed: " + command);
            return false;
        }
        String exitCode = answer.get(K_EXITCODE);
        String content = answer.get(K_CONTENT);
        if (!content.isEmpty()) {
            println(content);
        }
        if (!exitCode.equals("0")) {
            println("exitCode: " + exitCode);
        }
        if (returnFlag) {
            println(answer.params("\t= ", "\n"));
        }
        return true;
    }

    /**
     * スクリプトを同期呼び出しで実行し、その実行結果を表示する。
     *
     * @param script スクリプト
     * @return スクリプトの実行結果
     */
    synchronized public boolean invokeScript(String script) {
        DInfo dinfo = new DInfo(D_EVAL);
        dinfo.set(K_SCRIPT, script);
        dinfo.set(K_ID, "eval." + continuousDialogID());
        DInfo answer = invoke(dinfo);
        if (answer == null) {
            println("invoke failed: " + script);
            return false;
        }
        String returnValue = answer.get(K_RETURN);
        String content = answer.get(K_CONTENT);
        if (content != null && !content.isEmpty()) {
            println(content);
        }
        if (returnValue != null) {
            println("=> " + returnValue);
        }
        if (returnFlag) {
            println(answer.params("\t= ", "\n"));
        }
        return true;
    }

    /**
     * 同期呼出し: QUERYを発行し、ANSWERの返信を待って結果を返す。
     *
     * @param query 送信するQUERYメッセージ
     * @return QUERYに対するANSWER情報
     */
    public DInfo invoke(String query) {
        return invoke(DInfo.parse(query));
    }

    /**
     * 同期呼出し: QUERYを発行し、ANSWERの返信を待って結果を返す。
     *
     * @param dinfo 送信するQUERY情報
     * @return QUERYに対するANSWER情報
     */
    public DInfo invoke(DInfo dinfo) {
        if (dinfo == null) {
            return null;
        }
        if (queryInfo != null) {
            return null;
        }
        if (!queryD(dinfo)) {
            return null;
        }
        invokerThread = Thread.currentThread();
        synchronized (invokeLock) {
            boolean invoked = false;
            try {
                invokeLock.wait(invokeTimeout);
            } catch (InterruptedException e) {
                invoked = true;
            }
            if (!invoked) {
                printError("invoke timeout");
            }
        }
        invokerThread = null;
        return answerInfo;
    }

    /**
     * ANSWERの受信と同期処理を行う。
     *
     * @param dinfo ANSWERのダイアログ情報
     */
    private void receiveAnswer(DInfo dinfo) {
        if (queryInfo == null) {
            printError("unknown answer");

        } else if (dinfo.match(K_QUERY_SEQ, queryInfo)) {
            // ANSWERの返信待ちで同一QUERY_SEQなら正しいANSWERと判断する。
            answerReceived(dinfo);
            queryInfo = null;
            if (invokerThread != null) {
                answerInfo = dinfo;
                invokerThread.interrupt();
            } else {
                answerInfo = null;
            }
        } else {
            resetQuery();
            printError("invoke unmatched");
        }
    }

    /**
     * QUERYメッセージからダイアログ情報を作りQUERYの送信処理を実行する。
     * <p>
     * ANSWER待ちのQUERYがなければ、ANSWERの受信受付の準備を行い、 QUERYを発行する。不正な
     * QUERYに対してはエラーメッセージを表示する。
     *
     * @param query 送信するQUERYメッセージ
     * @return 不正な QUERYの場合false
     */
    synchronized public boolean evalQuery(String query) {
        DInfo dinfo = DInfo.parse(query);
        if (dinfo == null) {
            printError("Bad Query: " + query);
            return false;
        }
        if (queryInfo != null) {		// ANSWERの返信待ち
            showError("Previous Query Not Completed.");
            return false;
        }
        return queryD(dinfo);
    }

    /**
     * 送り状(NOTIFYメッセージ)ダイアログ情報を作りの送信処理を実行する。 送達確認は行わない。
     * 不正な送り状に対してはエラーメッセージを表示する。 NOTIFYの送信は以下の書式に従う。
     *
     * @param invoice 送り状
     * @return エラーの場合false
     */
    synchronized public boolean evalNotify(String invoice) {
        DInfo dinfo = DInfo.parse(invoice);
        if (dinfo == null) {
            printError("Bad Notify: " + invoice);
            return false;
        }
        notifyD(dinfo);
        return true;
    }

    /**
     * ======== Dialogクラスのメソッド呼び出しのためのAPI =========<br>
     * ダイアログ情報に基づいて動的にダイアログオブジェクトを生成し Dialogインタフェースで定義されたメソッドを呼び出す。
     * ダイアログ情報には、ダイアログ識別子、プロパティリスト、 コンテンツ情報、ダイアログの制御情報等が含まれる。
     */
    /**
     * ダイアログ情報に基づきQUERYの送信処理を呼び出す。 送信するQUERYには連番を振る。この連番は受信時に確認される。
     *
     * @param dinfo ダイアログ情報
     */
    synchronized private boolean queryD(DInfo dinfo) {
        queryInfo = dinfo;
        dinfo.set(K_QUERY_SEQ, querySeq++);
        assert startTime("1.query starts(" + mark() + "): " + dinfo.message());
        boolean result = DialogBase.query(dinfo, this);
        assert time("2.query done(" + mark() + "):   " + dinfo.dialogId, "msec");
        if (!result) {
            queryInfo = null;
        }
        return result;
    }

    /**
     * ダイアログ情報に基づきQUERYの受信処理を呼び出す。 拡張クラスでこのメソッドを再定義し、他のProxyへの転送処理などを 行うことができる。
     *
     * @param dinfo ダイアログ情報
     */
    protected void handleQA(DInfo dinfo) {
        assert timestamp(dinfo, "Q<");
        assert startTime("3.onQuery starts(" + mark() + "): " + dinfo.message());
        DialogBase.onQuery(dinfo, this);
        assert time("5.onQuery done(" + mark() + "):   " + dinfo.dialogId, "msec");
    }

    /**
     * ダイアログ情報に基づきANSWERの受信処理を呼び出す。 拡張クラスでこのメソッドを再定義し、他のProxyへの転送処理などを 行うことができる。
     *
     * @param dinfo ダイアログ情報
     */
    protected void answerReceived(DInfo dinfo) {
        assert timestamp(dinfo, "A<");
        assert time("4.onAnswer starts(" + mark() + "): " + dinfo.dialogId, "msec");
        DialogBase.onAnswer(dinfo, this);
        assert time("6.onAnswer done(" + mark() + "):   " + dinfo.dialogId, "msec");
        assert printTimestamp("answerReceived: ", dinfo);
    }

    /**
     * ダイアログ情報に基づきNOTIFYの送信処理を呼び出す。送達確認は行わない。 送信プロパティで宛先を設定したい場合は、
     * 拡張クラスでこのメソッドをオーバライドすれば良い。
     *
     * @param dinfo ダイアログ情報
     * @see Agent
     */
    protected void notifyD(DInfo dinfo) {
        assert startTime("1.notify starts(" + mark() + "): " + dinfo.message());
        DialogBase.notify(dinfo, this);
        assert time("3.notify done(" + mark() + "):   " + dinfo.dialogId, "msec");
    }

    /**
     * ダイアログ情報に基づきNOTIFYの受信処理を呼び出す。 拡張クラスでこのメソッドを再定義し、他のProxyへの転送処理などを 行うことができる。
     *
     * @param dinfo ダイアログ情報
     */
    protected void notifyReceived(DInfo dinfo) {
        assert timestamp(dinfo, "N<");
        assert startTime("2.onNotify starts(" + mark() + "): " + dinfo.message());
        DialogBase.onNotify(dinfo, this);
        assert time("4.onNotify done(" + mark() + "):   " + dinfo.dialogId, "msec");
        assert printTimestamp("notifyReceived: ", dinfo);
    }

}
