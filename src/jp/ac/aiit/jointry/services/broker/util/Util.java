package jp.ac.aiit.jointry.services.broker.util;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.*;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.net.URL;

/**
 * ユーティリティメソッドを定義したクラス。
 * <ul>
 * <li> リーダ／ライタの生成
 * <li> テキストの読込み・書出し
 * <li> ファイルサフィックスの操作
 * <li> 文字列配列の操作
 * <li> 日時・時刻・時間の文字列表記
 * <li> ポート番号・整数値取得
 * </ul>
 */
public class Util {

    static final String CRLF = "\n"; // "\r\n"

    /**
     * =================== リーダ／ライタの生成 ====================<br>
     * <ul>
     * <li> BufferedReaderの生成 - createReader(socket/stream/file/fname)
     * <li> PrintWriterの生成 - PrintWriter(socket [, charset])
     * </ul>
     */
    public static BufferedReader createReader(Socket socket) throws IOException {
        return createReader(socket.getInputStream());
    }

    public static BufferedReader createReader(Socket socket, String charset)
            throws IOException {
        InputStreamReader isr
                = new InputStreamReader(socket.getInputStream(), charset);
        return new BufferedReader(isr);
    }

    public static BufferedReader createReader(InputStream stream)
            throws IOException {
        return new BufferedReader(new InputStreamReader(stream));
    }

    public static BufferedReader createReader(String fname) throws IOException {
        return createReader(new File(fname));
    }

    public static BufferedReader createReader(File file) throws IOException {
        return new BufferedReader(new FileReader(file));
    }

    public static PrintWriter createWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream());
    }

    public static PrintWriter createWriter(Socket socket, String charset)
            throws IOException {
        OutputStreamWriter osw
                = new OutputStreamWriter(socket.getOutputStream(), charset);
        return new PrintWriter(osw);
    }

    /**
     * ================= テキストの読込み・書出し ==================<br>
     * <ul>
     * <li> 文字列配列リスト読込 - readArray(fname)
     * <li> テキスト読込み - readText/readTextEx(file/url)
     * <li> テキスト書出し - writeText(fname/file, text)
     * </ul>
     */
    /**
     * テキストファイルから文字列配列のリストを作る。 テキストファイルの各行は、１個以上の空白文字で分けられた項目からなり、
     * 文字列配列配列に変換される。
     *
     * @param fname ファイル名
     * @return 文字列配列のリスト
     */
    public static List<String[]> readArray(String fname) {
        File file = new File(fname);
        if (file == null || !file.exists()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            List<String[]> data = new ArrayList<String[]>();
            String line;
            while ((line = reader.readLine()) != null) {
                int i = line.indexOf("#");
                if (i >= 0) {
                    line = line.substring(0, i);
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] items = line.split("\\s+");
                data.add(items);
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Illegal data format: " + fname);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String readText(String fname) {
        return readText(new File(fname));
    }

    public static String readText(File file) {
        try {
            return readTextEx(file);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String readTextEx(File file) throws IOException {
        return readTextEx(createReader(file));
    }

    public static String readTextEx(URL url) throws IOException {
        return readTextEx(createReader(url.openStream()));
    }

    private static String readTextEx(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line + CRLF);
        }
        reader.close();
        return sb.toString();
    }

    public static boolean writeText(String fname, List<String> text) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File(fname));
            for (String line : text) {
                writer.println(line);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static boolean writeText(String fname, String text) {
        return writeText(new File(fname), text);
    }

    public static boolean writeText(File file, String text) {
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(text, 0, text.length());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /*================== ファイルサフィックスの操作 ==================*/
    public static String suffix(String fname) {
        int index = fname.lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return fname.substring(index + 1).toLowerCase();
    }

    public static String imageSuffix(String fname) {
        String suffix = suffix(fname);
        if (suffix.equals("jpg") || suffix.equals("png")) {
            return suffix;
        }
        return null;
    }

    public static boolean isImageFile(String fname) {
        return imageSuffix(fname) != null;
    }

    /*======================= 文字列配列の操作 =======================*/
    public static String[] subarray(String[] args, String sep) {
        int i = 0;
        while (i < args.length && !args[i].equals("--")) {
            i++;
        }
        return copy(args, 0, i);
    }

    public static String[] shift(String[] tokens) {
        return shift(tokens, 1);
    }

    public static String[] shift(String[] tokens, int shift) {
        if (shift == 0) {
            return tokens;
        }
        int n = tokens.length;
        if (n < shift) {
            return null;
        }
        return copy(tokens, shift, n - shift);
    }

    public static String[] copy(String[] array, int offset, int length) {
        assert (array.length >= offset + length);
        String[] aa = new String[length];
        System.arraycopy(array, offset, aa, 0, length);
        return aa;
    }

    public static String join(String sep, Object... items) {
        StringBuilder sb = new StringBuilder();
        for (Object s : items) {
            if (sb.length() > 0) {
                sb.append(sep);
            }
            if (s != null) {
                sb.append(s.toString());
            }
        }
        return sb.toString();
    }

    public static String toString(String[] array) {
        return "Array[" + join(", ", (Object[]) array) + "]";
    }

    /*================= 日時・時刻・時間の文字列表記 =================*/
    final static String[] TIMEUNITS = {"sec", "msec", "usec", "nsec"};
    final static String DATE_FORMAT = "%tH:%<tM:%<tS.%<tL";
    final static String TIMESTAMP_FORMAT = "%s%tS.%<tL";

    private static int timeOffset = 0; // 通信相手のマシンとの時間補正(msec)

    public static int timeOffset() {
        return timeOffset;
    }

    public static void timeOffset(int offset) {
        timeOffset = offset;
    }

    private static Date getDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, timeOffset);
        return cal.getTime();
    }

    /**
     * 現在の時刻を文字列表記で返す。 書式: hh:mm:ss.msec
     *
     * @return 現在時刻の文字列表記
     */
    public static String date() {
        return String.format(DATE_FORMAT, getDate());
    }

    /**
     * 現在時刻の文字列表記をミリ秒で返す。
     *
     * @param date 現在時刻の文字列表記 --- 書式: hh:mm:ss.msec
     * @return 現在時刻のミリ秒
     */
    public static int parseMillisecond(String date) {
        String[] tt = date.split("[^\\d]+");
        int ms = 0;
        for (int i = 0; i < tt.length - 1; i++) {
            ms = ms * 60 + getInt(tt[i]);
        }
        ms = ms * 1000 + getInt(tt[tt.length - 1]);
        return ms;
    }

    /**
     * 経過時間を単位付の文字列表記で返す。<br>
     * 書式: dd.dd (sec|msec|usec|nsec)
     *
     * @param nanoTime 経過時間のナノ秒
     * @return 経過時間の単位付文字列表記
     */
    public static String time(long nanoTime) {
        return time(nanoTime, null);
    }

    public static String time(long nanoTime, String unit) {
        double time = nanoTime;
        int index;
        if (unit == null) {
            index = TIMEUNITS.length - 1;
            while (1000.0 <= time && 0 < index) {
                time /= 1000.0;
                index--;
            }
        } else {
            index = findIndex(unit);
            int n = TIMEUNITS.length - 1 - index;
            for (int i = 0; i < n; i++) {
                time /= 1000.0;
            }
        }
        time = (long) (time * 100 + 0.5) / 100.0; // 小数点以下２桁まで表示
        return ("" + time + " " + TIMEUNITS[index]);
    }

    private static int findIndex(String unit) {
        for (int i = 0; i < TIMEUNITS.length; i++) {
            if (TIMEUNITS[i].equals(unit)) {
                return i;
            }
        }
        return TIMEUNITS.length - 1;
    }

    /**
     * マーク付のタイムスタンプ文字列を返す。<br>
     * 書式: (Q|A|N)(>|=|<)ss.msec
	 * @par
     *
     * am mark マーク
     * @return マーク付タイムスタンプ文字列
     */
    public static String timestamp(String mark) {
        return String.format(TIMESTAMP_FORMAT, mark, getDate());
    }

    /*==================== ポート番号・整数値取得 ====================*/
    /**
     * ポート番号の整数値を求める。
     *
     * @param portArg ポート番号の文字列
     */
    public static int getPort(String portArg) {
        if (portArg == null) {
            return 80;
        }
        try {
            int port = Integer.parseInt(portArg);
            return (0 <= port || port <= 65535) ? port : -1;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 整数の文字列表記から整数値を求める。
     *
     * @param s 整数の文字列表記
     */
    public static int getInt(String s) {
        if (s != null) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.err.println("Util.getInt(s): " + e.getMessage());
            }
        }
        return 0;
    }

    /**
     * 指定された時間だけスリープする。
     *
     * @param interval スリープ時間(msec)
     */
    public static void sleep(int interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
        }
    }

}
