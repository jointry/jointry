package jp.ac.aiit.jointry.services.broker.core;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import jp.ac.aiit.jointry.services.broker.util.Util;

/**
 * FTPDialogはファイルの送受信を行うダイアログクラスである。
 * <pre>
 * 使用法：
 *  ftp @file=<file-name> [@sink=<sink-name>]
 * 使用例:
 *  !ftp @file=BRK20121004.zip @sink=BRK.zip
 *  ?ftp @file=BRK20121004.zip @sink=BRK.zip
 *  /ftp @file=C:/work/AIIT.zip					# cp C:/work/AIIT.zip ./AIIT.zip
 *  /ftp @file=C:/work/AIIT.zip @sink=workroot	# cp C:/work/AIIT.zip workroot
 *  /ftp @file=C:/work/AIIT.zip @sink=AIIT0.zip	# cp C:/work/AIIT.zip AIIT0.zip
 * </pre>
 */
public class FTPDialog extends DialogBase {

    /**
     * ファイルの送信処理を行う。送達確認は行わない。
     *
     * @param dinfo ダイアログ情報(@file=fileA [@type=(text|image|binary)])
     * @return @fileプロパティが指定されていない場合はfalse
     */
    @Override
    public boolean query(DInfo dinfo) {
        if (dinfo.get(K_FILE) == null) {
            showError("@file not defined.");
            return false;
        }
        sendQuery(dinfo);
        return true;
    }

    /**
     * ファイル送信QUERYに対するANSWERを返信する。 ファイル送信の有無を@resultで送る(送信する場合はOK、それ以外はNG)。
     *
     * @param dinfo ダイアログ情報(@file/@type/@result,@size)
     */
    @Override
    public void onQuery(DInfo dinfo) {
        String fname = searchSourceName(dinfo);
        if (fname != null) {
            sendAnswer(dinfo, V_OK);
            sendFile(fname, dinfo.fileType(fname));
        } else {
            sendAnswer(dinfo, V_NG);
        }
    }

    /**
     * QUERYに対するANSWERの受信処理を行う。
     *
     * @param dinfo ダイアログ情報(@file, @sink, @type, @result, @size)
     */
    @Override
    public void onAnswer(DInfo dinfo) {
        if (dinfo.match(K_RESULT, V_OK)) {
            receiveFile(dinfo);
        } else {
            showError(dinfo.message());
        }
    }

    /**
     * ファイルの送信処理を行う。送達確認は行わない。
     *
     * @param dinfo ダイアログ情報(@file [@sink] [@type])
     * @return ファイルが存在しない場合はfalse
     */
    @Override
    public boolean notify(DInfo dinfo) {
        String fname = searchSourceName(dinfo);
        if (fname != null) {
            sendNotify(dinfo);
            sendFile(fname, dinfo.fileType(fname));
            return true;
        }
        return false;
    }

    /**
     * ファイルの受信処理を行う。
     *
     * @param dinfo ダイアログ情報(@file [@sink] @type @size)
     */
    @Override
    public void onNotify(DInfo dinfo) {
        receiveFile(dinfo);
    }

    /**
     * ファイルの送信処理を行う。
     *
     * @param fname ファイル名
     * @param type ファイル種別(text/binary/image)
     */
    protected void sendFile(String fname, String type) {
        if (type.equals(V_TEXT)) {
            sendTextFile(fname);
        } else {
            sendBinary(fname);
        }
    }

    /**
     * テキストファイルをPLAINメッセージ列として送信する。
     *
     * @param fname ファイル名
     */
    private void sendTextFile(String fname) {
        BufferedReader br = null;
        try {
            br = Util.createReader(fname);
            String line;
            while ((line = br.readLine()) != null) {
                sender.print(M_PLAIN + line + CRLF);
            }
        } catch (IOException e) {
            printStackTrace(e, null);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
            sendEOF();
        }
    }

    /**
     * データを受信しコンテンツに設定する。 終端の場合はファイルに保存する。
     *
     * @param dinfo ダイアログ情報(@file [@sink])
     */
    protected void receiveFile(DInfo dinfo) {
        readContent(dinfo);
        if (dinfo.isEndPoint && !dinfo.has(K_RELAY)) {
            String sname = searchSinkName(dinfo, docRoot());
            if (new File(sname).exists()) {
                showError("file already exists: " + sname);
            } else {
                storeContent(dinfo, sname);
            }
        }
    }

    /**
     * 送信するファイルのパス名を特定する。
     *
     * @param dinfo ダイアログ情報(転送ファイル名は@fileプロパティ)
     * @return 送信するファイルのパス名(存在しない場合はnull)
     */
    protected String searchSourceName(DInfo dinfo) {
        String fname = filePath(dinfo);
        File file;
        if (fname == null || !(file = new File(fname)).exists()) {
            showError("file not found: " + fname);
            return null;
        }
        dinfo.setDefault(K_TYPE, dinfo.fileType(fname));
        dinfo.set(K_SIZE, (int) file.length());
        return fname;
    }

    /**
     * 保存ファイル名を@fileと@sinkの値に基づいて決定する。
     *
     * @param dinfo ダイアログ情報(@file [@sink])
     * @return 保存ファイルのパス名
     */
    protected String searchSinkName(DInfo dinfo, String root) {
        String fname = new File(dinfo.get(K_FILE)).getName();
        String sname = dinfo.get(K_SINK);
        if (sname == null) {
            sname = fname;
        } else {
            if (new File(root + sname).isDirectory()) {
                sname += "/" + fname;
            }
        }
        return root + sname;
    }

    /**
     * 元ファイルのパス名を特定する。
     *
     * @param dinfo ダイアログ情報(@file)
     * @return ファイルのパス名
     */
    protected String filePath(DInfo dinfo) {
        return docRoot() + dinfo.get(K_FILE);
    }

    /**
     * ファイルのルートディレクトリのパス名を返す。
     *
     * @return ルートディレクトリのパス名
     */
    protected String docRoot() {
        return "./";
    }

}
