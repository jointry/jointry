package jp.ac.aiit.jointry.services.broker.core;
import java.io.File;
import java.io.IOException;

/**
 * テキストファイル、バイナリファイル、画像ファイルの転送と表示の処理を行う。
 * 元ファイルはdocRootディレクトリからの相対パスで指定し、
 * アクセスの範囲はこのディレクトリ内に制限される。
 * ファイルの種別は @type プロパティで明示的に指定することができる。
 * ファイル種別 @type の指定がない場合はファイル名の添え字から種別を判定する。
 * <pre>
 * 使用法：
 *  view @file=<file-name> [@sink[=<sink-name>]] [@title]
 * 使用例:
 * view @file=w.txt @sink=w1.txt @title=w
 * view @file=w.jpg @sink=w1.jpg @title=w
 * view @type=binary @file=w.zip @sink=w1.zip
 * </pre>
 */

public class ViewDialog extends FTPDialog {

	@Override protected String docRoot() { return docRoot + "/"; }

	/**
	 * 元ファイルのパス名を特定する。
	 * @param dinfo ダイアログ情報(@file) 
	 * @return 元ファイルのパス名(アクセス不可の場合はnull)
	 */
	@Override protected String filePath(DInfo dinfo) {
		String fname = super.filePath(dinfo);
		return isValidFile(fname) ? fname : null;
	}

	/**
	 * 指定された元ファイルがdocRootの下に存在するか判定する。
	 * @param fname 元ファイルのファイル名
	 * @return docRootの下のファイルならば true
	 */
	private boolean isValidFile(String fname) {
		// アクセス範囲は docRoot 内に制限する。
		String[] items = fname.split("/");
		for(String item : items)
			if(item.equals("..")) return false;
		return new File(fname).exists();
	}

	/**
	 * データの受信処理を行う。
	 * @param dinfo ダイアログ情報(K_TYPE, K_FILE, K_SINK, K_RELAY)
	 */
	@Override protected void receiveFile(DInfo dinfo) {
		// データコンテンツを受信しdinfoに保持する。
		readContent(dinfo);

		// ファイルに保存するのは、次の３つの条件をすべて満たす場合とする。
		// 1)@sinkが定義されている、2)受信側が終端である、3)中継点でない
		if(dinfo.has(K_SINK) && dinfo.isEndPoint && !dinfo.has(K_RELAY)) {
			try {
				String sname = makeFilePath(searchSinkName(dinfo, ""));
				if(new File(sname).exists()) {
					showError("file already exists: " + sname);
				} else {
					storeContent(dinfo, sname);
				}
			} catch(IOException e) {
				showError(D_VIEW + ": " + e.getMessage());
			}
		}
		String title = dinfo.get(K_SINK, dinfo.get(K_FILE));
		if(title == null) {
			showError("" + dinfo.message());
		} else {
			if(dinfo.viewable) viewContent(title, dinfo);
		}
	}

	private void viewContent(String fname, DInfo dinfo) {
		// 表示タイトルは、ファイルの名前とする。
		String title = new File(fname).getName();
		if(dinfo.weakMatch(K_TYPE, V_TEXT)) {
			viewTextContent(dinfo, title);
		} else if(dinfo.match(K_TYPE, V_IMAGE)) {
			viewImageContent(dinfo, title);
		} else {
			showStatus("data received: " + fname);
		}
	}

}
