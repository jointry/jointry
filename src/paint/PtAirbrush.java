package paint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.BoxLayout;

/**
 * 「エアーブラシ」ツールの実装。
 * ブラシのサイズは、小、中、大の３種類に対応する。
 */
public class PtAirbrush extends PaintTool {
	private static final int AMOUNT_OF_LIQUID = 5;
	private Random rand = new Random();

	public PtAirbrush(String name, String tip) {
		super(name, tip);
		optionPanel.add(new AirbrushPanel());
	}

	@Override public Rectangle paint(BufferedImage img, Point p1, Point p2) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(getMainColor());
		int r = super.penSize;
		int n = r * AMOUNT_OF_LIQUID; // エアブラシのドット量
		int airbrushRR = r * r;
		for(int i = 0; i < n; i++) {
			int x, y;
			// XY方向に一定距離で散布する。
			// エアブラシの半径内に収まっていなければ再散布する。
			do {
				x = rand.nextInt(2 * r + 1) - r;
				y = rand.nextInt(2 * r + 1) - r;
			} while (x*x + y*y > airbrushRR);
			g2d.drawRect(p2.x + x, p2.y + y, 0, 0);
		}
		return calcBounds(p2, p2, r, 2*r);
	}

	private class AirbrushPanel extends ToolOptionPanel {
		AirbrushPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			addOption(new ToolOption("airbrush_1", 5), true);
			addOption(new ToolOption("airbrush_2", 10));
			addOption(new ToolOption("airbrush_3", 15));
		}
	}
}
