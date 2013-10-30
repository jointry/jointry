package ash.broker.core;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import ash.broker.util.Util;
import ash.broker.util.ImageUtil;
import ash.broker.util.Base64;

/**
 * DInfoは、AgentとProxyの間の双方向通信のプロトコル情報、および Proxy間での
 * メッセージの転送やコンテンツの処理に必要な情報（コンテンツ情報、
 * コンテンツの表示制御、中継点・終端区別）を管理する。
 * <p>
 * Brokerのアプリケーションは、DialogBaseクラスを拡張してアプリケーションレベルの
 * プロトコルを実装する。DialogBaseの拡張クラスの各メソッドの実装に当たっては、
 * DInfo（とその親クラスのBPInfo）が提供するメソッドを使用して、
 * アプリケーションレベルのAPIとBrokerのプロトコルとの間のデータ変換を行う。
 *
 * @see Proxy
 * @see Agent
 * @see DialogBase
 */
// execute @fqcn=app.SampleDialog name=Takashi~Yamada\t45\\5 friend=Boss

public class DInfo extends BPInfo {

	static final long LARGE_LENGTH = 1L * 1024 * 1024;

	public DInfo() {}
	public DInfo(String dialogId) { this.dialogId = dialogId; }

	public String getDialogName() { return dialogId; }

	/** 受信したデータコンテンツを文字列Listとして保持する */
	private List<String> content = null;

	/** 受信したデータコンテンツを保存したファイルのパス名 */
	private String contentFile = null;

	/**
	 * メッセージの受信者がコンテンツをモニタに表示するか否かを判定するフラグ。
	 * メッセージの受信者は、コンテンツの種別に応じて、Monitorが提供する
	 * viewText(title, text)、viewImage(title, bimage)、println(msg)などの
	 * メソッドを使用して、コンテンツを表示することができるが、
	 * visibleを使ってこの表示を制御できる。
	 * コンテンツのモニタ表示を行わない場合はfalseとする。
	 * Proxy#answerReceived(dinfo)/notifyReceived(dinfo)で設定する。
	 * @see Monitor
	 */
	boolean viewable = true;

	/** 
	 * メッセージ受信時に通信の終点の場合はtrueとする。
	 * 受信側オブジェクトがServerProxyかClientProxyならばfalse、
	 * AgentまたはProxyならば trueとなる。
	 */
	boolean isEndPoint = true;

	/**
	 * 会話メッセージをパーズし、プロパティ情報を登録する。
	 * @param line 会話の１行のメッセージ文
	 */
	public static DInfo parse(String line) {
		String tokens[] = line.split("\\s+");
		if(tokens.length > 0 && DialogBase.containsDialogId(tokens[0])) {
			DInfo dinfo = new DInfo(tokens[0]);
			for(int i = 1; i < tokens.length; i++) {
				String token = tokens[i];
				int index = token.indexOf("=");
				if(index < 0) {
					dinfo.setEscapedValue(token, null);
				} else {
					String key = token.substring(0, index);
					String value = token.substring(index+1);
					dinfo.setEscapedValue(key, value);
				}
			}
			return dinfo;
		}
		return null;
	}

	private static String[] textSuffixArray = {
		"txt", "java", "scala", "mk", "rb", "brk", "log", "err", 
		"ash", "rash", "as", "tex",
	};

	/**
	 * K_TYPEプロパティとファイル名からファイル種別を判定する。
	 * @param fname ファイル名
	 * @return ファイル種別を示す文字列(text/binary/image)
	 */
	public String fileType(String fname) {
		String type = get(K_TYPE);
		if(type != null) return type;
		if(Util.isImageFile(fname)) return V_IMAGE;
		String suffix = Util.suffix(fname);
		for(String s : textSuffixArray)
			if(s.equals(suffix)) return V_TEXT;
		return V_BINARY;
	}

	/**
	 * テキストコンテンツを文字列形式で返す。
	 * 受信したテキストファイルはプレフィックスとして`@'文字を付けた
	 * 文字列のリストとして保持される。これを文字列に変換する。
	 * @return テキストコンテンツの文字列
	 */
	public String getTextContent() {
		if(content == null) return null;
		StringBuilder sb = new StringBuilder();
		for(String line : content) sb.append(line + CRLF);
		return sb.toString();
	}

	/**
	 * テキスト形式のデータコンテンツを読み取り、ダイアログ情報に設定する。
	 * @param httpBase データコンテンツを読み取るリーダ
	 */
	synchronized public void readContent(HttpBase httpBase)
		throws IOException {
		if(getInt(K_SIZE,0) > LARGE_LENGTH) {
			// LARGE_LENGTHより大きいファイルは一時ファイルに保存し、
			// ファイル名をダイアログ情報に設定する。
			content = null;
			String fname = get(K_SINK, get(K_FILE));
			contentFile = readAndStore(httpBase, fname + ".tmp");
		} else {
			content = readTextBlock(httpBase.reader);
			contentFile = null;
		}
	}

	/**
	 * リーダからテキストを読み込み、ファイルに保存し、そのファイル名を返す。
	 * 読み込むテキストの終了はEOF(空白行)で判定する。
	 * @param httpBase データコンテンツを読み取り中の通信主体
	 * @param fname ファイル名
	 * @return 読込んだテキストを保存したファイルパス名（失敗した場合はnull）
	 * @exception IOException ファイル保存に失敗した場合
	 */
	private String readAndStore(HttpBase httpBase, String fname)
		throws IOException {
		BufferedWriter writer = null;
		try {
			BufferedReader reader = httpBase.reader;
			String filepath = httpBase.makeFilePath(fname);
			OutputStream os = new FileOutputStream(new File(filepath));
			writer = new BufferedWriter(new OutputStreamWriter(os));
			String line;
			while((line = reader.readLine()) != null && !line.isEmpty()) {
				line = internal(line) + CRLF;
				writer.write(line, 0, line.length());
			}
			return filepath;
		} finally {
			try { if(writer != null) writer.close(); } catch(IOException e) {}
		}
	}

	/**
	 * ソケットのリーダから複数行のテキストを読み込み、文字列Listとして返す。
	 * 読み込むテキストの終了はEOF(空白行)で判定する。
	 * @return 読み込んだ文字列List（読込みに失敗した場合はnull）
	 * @exception IOException 読み込みに失敗した場合
	 */
	private List<String> readTextBlock(BufferedReader reader)
		throws IOException {
		List<String> textBlock = new ArrayList<String>();
		String line;
		while((line = reader.readLine()) != null && !line.isEmpty())
			textBlock.add(internal(line));
		return textBlock;
	}

	private String internal(String line) {
		return match(K_TYPE, V_TEXT) ? line.substring(1) : line;
	}

	/**
	 * データコンテンツを送信する。
	 * @param sender ソケットの出力ストリームに対応するライタ
	 */
	synchronized public void sendContent(PrintWriter sender) {
		if(content != null) {
			for(String line : content)
				sender.print(transform(line) + CRLF);
			sender.println("");
			sender.flush();
		} else if(contentFile != null) {
			BufferedReader br = null;
			try {
				br = Util.createReader(contentFile);
				String line;
				while((line = br.readLine()) != null)
					sender.print(transform(line) + CRLF);
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				if(br != null) try { br.close(); } catch(IOException e) {}
			}
			sender.println("");
			sender.flush();
		}
	}

	private String transform(String line) {
		return match(K_TYPE, V_TEXT) ? M_PLAIN + line : line;
	}

	/**
	 * データコンテンツをファイルに書き出す。
	 * Base64でエンコードされたデータコンテンツはデコードしファイルに書き出す。
	 * @param fname データコンテンツを書出すファイル名
	 */
	synchronized public void storeContent(String fname) throws IOException {
		if(match(K_TYPE, V_TEXT)) {	// テキストファイルとして保存
			if(content != null) {
				Util.writeText(fname, content);
			} else {
				new File(contentFile).renameTo(new File(fname));
			}
		} else if(content != null) {	// バイナリファイルとして保存
			Base64.decodeTextBlock(content, fname);
		} else if(contentFile != null) {
			Base64.decodeB64File(contentFile, fname);
		}
	}

	/**
	 * 画像コンテンツから画像データを再現しその結果を返す。
	 * 画像コンテンツは、contentフィールドにその内容を保持するか、または
	 * ファイルに保存しそのファイル名をcontentFileに設定しておく必要がある。
	 * @return 画像コンテンツから再現された画像データ
	 */
	public BufferedImage getImageContent() {
		if(content != null) return ImageUtil.createImage(content);
		if(contentFile != null) return ImageUtil.createImage(contentFile);
		return null;
	}

	/**
	 * コンテンツの表示を行うか否かを返す。
	 * @return コンテンツを表示する場合はtrue
	 */
	public boolean isViewable() { return viewable; }

	/**
	 * 終端か否かを返す。
	 * @return 終端ならばtrue(中継点ならばfalse)
	 */
	public boolean isEndPoint() { return isEndPoint; }

}
