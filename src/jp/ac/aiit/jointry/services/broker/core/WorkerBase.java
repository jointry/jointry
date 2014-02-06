package jp.ac.aiit.jointry.services.broker.core;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import jp.ac.aiit.jointry.services.broker.util.ImageUtil;
import jp.ac.aiit.jointry.services.broker.util.Util;

/**
 * WorkerBaseは、NSBrokerを構成する主要なコンポーネントであるBroker、Proxy、
 * Agent、Dialogの共通のベースクラスである。 これらのコンポーネントはSocketで接続された通信相手とメッセージの交換を行う。
 * すなわち相手側からのメッセージを待ちながら相手側へメッセージを送ることが できなければならない。二つのスレッドに送信と受信を分担させる必要がある。
 * そこでWorkerBaseにはThreadを継承させ相手側からのメッセージを待ち受ける 役割をもたせた。
 * <p>
 * 双方向通信を行うアプリケーションでは各種データコンテンツの送受信が 発生する。WorkerBaseでは、送信するデータコンテンツの配置と
 * 受信するデータコンテンツの配置のために docRoot と workRootの 二つのディレクトリを管理している。
 * <p>
 * 双方向通信のアプリケーション開発のテスト段階では、 送受信されるメッセージのシーケンスを容易に追跡できることが必要となる。
 * NSBrokerでは、通信基盤の内部の状態や受信したメッセージをアプリケーション側に 伝える仕組みとしてMonitorインタフェースを定義した。
 * WorkerBaseは、Monitorインタフェースを実装したMonitorクラスを保持し、 このメソッドを呼出すAPIを提供している。
 * <p>
 * その他に、WorkerBaseではデバッグ支援や性能測定のためのメソッドを提供している。
 */
public class WorkerBase extends Thread implements Common {

    /**
     * 現在のクラスのクラス名を返す。
     */
    protected String theClass() {
        return this.getClass().getSimpleName();
    }

    /**
     * ドキュメントのルートディレクトリ： HTTP GET や viewダイアログで送信するファイルはこのディレクトリに置かれる。
     */
    protected String docRoot = "./docRoot";

    /**
     * ドキュメントのルートディレクトリを取得する。
     */
    public String getDocRoot() {
        return docRoot;
    }

    /**
     * ドキュメントのルートディレクトリを設定する。
     */
    public void setDocRoot(String docRoot) {
        this.docRoot = docRoot;
    }

    /**
     * 作業用のルートディレクトリ： Broker, Proxy, Agent がファイルを保存する際に使用する。 それぞれで再定義しファイルを生成する
     * Dialogに引き継がなければならない。
     */
    protected String workRoot = "./workroot";

    /**
     * 作業用のルートディレクトリの下に指定されたファイル名のパス名を作る。 ファイルが生成される位置はworkRoot 内に制限される。
     *
     * @param fname ファイル名
     * @return ファイルのパス名
     * @exception IOException 不正なファイル名の場合
     */
    protected String makeFilePath(String fname) throws IOException {
        if (isValidFileName(fname)) {
            String filepath = workRoot + "/" + fname;
            File file = new File(filepath);
            File parent = file.getParentFile();
            if (parent.isDirectory() || parent.mkdirs()) {
                return filepath;
            }
        }
        throw new IOException("failed to make directory: " + fname);
    }

    private boolean isValidFileName(String fname) {
        // アクセス範囲を workRoot 内に制限する。
        String[] items = fname.split("/");
        for (String item : items) {
            if (item.equals("..")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 作業用のルートディレクトリの下に画像データファイルを作成する。
     *
     * @param title 画像ファイルのタイトル名
     * @param bimage 画像データ
     * @return 画像ファイルのパス名
     */
    protected String createImageFile(String title, BufferedImage bimage) {
        try {
            if (Util.suffix(title).isEmpty()) {
                title += ".jpg";
            }
            String filepath = makeFilePath(title);
            if (ImageUtil.writeBufferedImage(filepath, bimage)) {
                return filepath;
            }
        } catch (IOException e) {
            printStackTrace(e, null);
        }
        return null;
    }

    /**
     * Broker, Proxy, Agent, Dialogの実行状況を出力するためのモニタ
     */
    protected Monitor monitor = new DefaultMonitor();

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
    }

    /**
     * MonitorAPIの呼出し
     */
    public void msglog(boolean msglog) {
        monitor.msglog(msglog);
    }

    protected boolean msglog() {
        return monitor.msglog();
    }

    public void print(String msg) {
        monitor.print(msg);
    }

    public void println(String msg) {
        monitor.println(msg);
    }

    protected void showStatus(String status) {
        monitor.showStatus(status);
    }

    protected void showError(String status) {
        showStatus("★ " + status);
        printError(status);
    }

    protected void printError(String msg) {
        println("ERROR: " + msg);
    }

    protected void printError(String msg, Exception e) {
        printError(msg + ": " + e.getMessage());
    }

    /**
     * 入出力メッセージのログ表示用API
     */
    protected void logRead(String log) {
        println(">>>\t" + log);
    }

    protected void logSend(String log) {
        println("<<<\t" + log);
    }

    /**
     * デバッグ用トレース文
     */
    public static void pp(String msg) {
        System.err.println("◆" + msg);
    }

    protected boolean trace(String msg) {
        return debug("◆" + msg);
    }

    protected boolean debug(String msg) {
        println(msg);
        return true;
    }

    protected boolean printStackTrace(Throwable t, String msg) {
        //t.printStackTrace();
        if (msg == null) {
            msg = "Exception/Error thrown";
        }
        println(Util.date() + " " + msg);
        Debug.printStackTrace(t, monitor);
        return true;
    }

    protected boolean probe(String msg) {
        if (msg == null) {
            msg = "Debug.probeStack";
        }
        println(Util.date() + " " + msg);
        Debug.probeStack(monitor);
        return true;
    }

    /**
     * 性能測定用API
     */
    protected boolean timestamp(DInfo dinfo, String mark) {
        dinfo.append(K_TIMESTAMP, Util.timestamp(mark));
        return true;
    }

    protected boolean printTimestamp(String title, DInfo dinfo) {
        println(title + dinfo.getTimestamp());
        return true;
    }

    private long startTime = System.nanoTime();
    private long lastTime = startTime;

    protected boolean startTime(String title) {
        println(Util.date() + "\t" + title);
        startTime = lastTime = System.nanoTime();
        return true;
    }

    protected boolean time(String tag) {
        return time(tag, null);
    }

    protected boolean time(String tag, String unit) {
        long t = System.nanoTime();
        println(Util.date() + "\t" + tag + "\t" + Util.time(t - lastTime, unit)
                + "\ttotal=" + Util.time(t - startTime, unit));
        lastTime = t;
        return true;
    }
}
