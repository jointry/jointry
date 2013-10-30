package jp.ac.aiit.jointry.services.broker.core;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * DefaultMonitorはMonitorインタフェースの最も簡易な実装であり、
 * 標準出力に監視結果を文字列として出力する。
 */
public class DefaultMonitor implements Monitor {
	private boolean msglog = false;
	@Override public void msglog(boolean msglog) { this.msglog = msglog; }
	@Override public boolean msglog() { return msglog; }
	@Override public void print(String msg) { System.out.print(msg); }
	@Override public void println(String msg) { System.out.println(msg); }
	@Override public void showStatus(String status) { println("@@@ " + status); }
	@Override public void viewText(String title, String text) {
		println("=== viewText title: " + title + " text:");
		print(text);
	}
	@Override public void viewImage(String title, BufferedImage bimage) {
		//println("=== viewImage title: " + title + " image: [" + bimage + "]");
		println("=== viewImage title: " + title);
	}
	@Override public void viewComponent(String title, JComponent compo) {
		//println("=== viewComponent title: "+title+"component: ["+compo+"]");
		println("=== viewComponent title: " + title);
	}
	@Override public void onClose() { println("@@@ onClose:"); }
}
