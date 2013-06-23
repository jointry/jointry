package jp.ac.aiit.jointry.main;

import java.io.IOException;
import java.io.Reader;

/**
 * lang read.
 * 文字列形式のバッファを単語単位で読み出す
 * 
 * @author kanemoto
 */
public class LangReader extends Reader {

    private String buffer = null;
    private int pos = 0;

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (buffer == null) {
            return -1;
        }
        
        pos = 0;

        int size = 0;
        int length = buffer.length();
        while (pos < length && size < len) {
            cbuf[off + size++] = buffer.charAt(pos++);
        }
        if (pos == length) {
            buffer = null;
        }
        return size;
    }

    @Override
    public void close() throws IOException {
    }

    public LangReader(String buffer) {
        this.buffer = buffer;
    }
}
