package ash.broker.util;
import java.util.List;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;

/**
 * Base64エンコード／デコード
 * <p>
 * Base64 はバイナリデータを 64個の印刷可能ASCII文字(A-Za-z0-9+/)で表現する
 * 符号化方式。3バイト（24ビット）を 4個の 6ビットに分け、各6ビットデータを
 * 64個の印刷可能文字にマップすることで、バイナリデータを印刷可能文字からなる
 * テキストとして表現できる。テキストの文字数は４の倍数となるように`='文字が
 * 最後に付加され、１行が最大で76文字になるように改行コード(CR+LF)が挿入される。
 */
public class Base64 {

	static final String CRLF = "\n"; // "\r\n"

	public static void encodeWithWriter(String infile, BufferedWriter writer)
		throws IOException {
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(infile));
			encode64(is, writer);
		} finally {
			if(is != null) is.close();
		}
	}

	public static void decodeB64File(String infile, String outfile)
		throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(infile));
			decodeWithReader(reader, outfile);
		} finally {
			if(reader != null) reader.close();
		}
	}

	public static void decodeWithReader(BufferedReader reader, String outfile)
		throws IOException {
		decodeToFile(new TextFileReader(reader), outfile);
	}

	public static void decodeTextBlock(List<String> textBlock, String outfile)
		throws IOException {
		decodeToFile(new TextBlockReader(textBlock), outfile);
	}
	public static void decode64(List<String> textBlock, OutputStream os)
		throws IOException {
		decode64(new TextBlockReader(textBlock), os);
	}

	private static void decodeToFile(LineReader reader, String outfile)
		throws IOException {
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(outfile));
			decode64(reader, os);
		} finally {
			if(os != null) os.close();
		}
	}

	interface LineReader { String readLine(); }
	static class TextBlockReader implements LineReader {
		List<String> textBlock;
		int lno = 0;
		TextBlockReader(List<String> textBlock) { this.textBlock = textBlock; }
		@Override public String readLine() {
			if(lno >= textBlock.size()) return null;
			return textBlock.get(lno++);
		}
	}
	static class TextFileReader implements LineReader {
		BufferedReader reader;
		TextFileReader(BufferedReader reader) { this.reader = reader; }
		@Override public String readLine() {
			try {
				return reader.readLine();
			} catch(IOException e) {
				return null;
			}
		}
	}

	static final int SIZE = 128 * 1024;
	static final int LINESIZE = 76;
	static final String BASE64 =
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	static final char[] base64 = new char[BASE64.length()];
	static final int[] reverse64 = new int['z' + 1];
	static {
		for(int i = 0; i < BASE64.length(); i++) {
			base64[i] = BASE64.charAt(i);
			reverse64[BASE64.charAt(i)] = i;
		}
	}

	/**
	 * バイナリデータをBase64形式で符号化しテキストデータとして書き出す。
	 * @param is エンコード対象となるバイナリデータのストリーム
	 * @param writer エンコード結果を書き出すテキストストリーム
	 */
	public static void encode64(InputStream is, BufferedWriter writer)
		throws IOException {
		byte[] bbuf = new byte[3*SIZE];
		int outlen = 0;
		int blen;
		while((blen = is.read(bbuf, 0, bbuf.length)) > 0) {
			int remainder = blen % 3;
			if(remainder > 0) {
				if(remainder == 1) bbuf[blen] = 0;
				bbuf[blen+1] = 0;
			}
			char[] c4 = new char[4];
			for(int i = 0; i < blen; i += 3) {
				int b0 = bbuf[i] & 0xFF;
				int b1 = bbuf[i+1] & 0xFF;
				int b2 = bbuf[i+2] & 0xFF;
				c4[0] = base64[b0 >> 2];
				c4[1] = base64[(b0 << 4 | b1 >> 4) & 0x3f];
				c4[2] = base64[(b1 << 2 | b2 >> 6) & 0x3f];
				c4[3] = base64[b2 & 0x3f];
				if(blen <= i + 2) {
					if(blen < i + 2) c4[2] = '=';
					c4[3] = '=';
				}
				for(int j = 0; j < 4; j++) {
					writer.write(c4[j]);
					outlen++;
					if(outlen >= LINESIZE) {
						writer.write(CRLF);
						outlen = 0;
					}
				}		
			}
		}
		writer.write(CRLF);
		writer.flush();
	}

	/**
	 * Base64形式で符号化したテキストデータを元のバイナリデータへ復号し書き出す。
	 * @param reader 符号化されたテキストデータを読み込むリーダ
	 * @param os 復号したバイナリデータを書き出すストリーム
	 */
	synchronized public static void decode64(LineReader reader, OutputStream os)
		throws IOException {
		String line;
		while((line = reader.readLine()) != null) {
			int tail = line.indexOf('=');
			int clen = (tail >= 0) ? tail : line.length(); // <= 76
			int clen4 = (clen + 3) & ~3;
			byte[] bbuf = new byte[clen4 * 3 / 4];
			int[] dec4 = new int[4];
			int j = 0;
			for(int i = 0; i < clen4; i++) {
				int i4 = i & 3; // = i % 4;
				try {
					dec4[i4] = (i < clen) ? reverse64[line.charAt(i)] : 0;
				} catch(IndexOutOfBoundsException e) {
					System.err.println("Illegal Base64 format: " + line);
					dec4[i4] = 0;
				}
				if(i4 == 3) {
					try {
						bbuf[j++] = (byte)(dec4[0]<<2 | dec4[1]>>4);
						bbuf[j++] = (byte)(dec4[1]<<4 | dec4[2]>>2);
						bbuf[j++] = (byte)(dec4[2]<<6 | dec4[3]);
					} catch(IndexOutOfBoundsException e) {
						break;
					}
				}
			}
			os.write(bbuf, 0, bbuf.length - (line.length() - clen));
		}
	}
}
