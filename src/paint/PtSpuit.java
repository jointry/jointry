package paint;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * 「色選択」ツールの実装。
 */
public class PtSpuit extends PaintTool {
	public PtSpuit(String name, String tip) { super(name, tip); }

	@Override public Rectangle paint(BufferedImage img, Point p1, Point p2) {
		int x = p2.x;
		int y = p2.y;
		if(0 <= x && x < img.getWidth() && 0 <= y && y < img.getHeight())
			theColorPanel.setColorCell(new Color(img.getRGB(x, y)));
		return calcBounds(0, 0, 0, 0);
	}
}
