package paint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/**
 * 「鉛筆」ツールの実装。
 * １ピクセルの細い鉛筆と３ピクセルの太い鉛筆を選択可能とする。
 */
public class PtPencil extends PaintTool {
	private JRadioButton thin_rb = null;
	private JRadioButton fat_rb = null;

	public PtPencil(String name, String tip) {
		super(name, tip);
		ButtonGroup bg = new ButtonGroup();
		thin_rb = super.addSelector(bg, "細い線", true);
		fat_rb  = super.addSelector(bg, "太い線", false);
	}

	@Override public Rectangle paint(BufferedImage img, Point p1, Point p2) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(getMainColor());
		if(thin_rb.isSelected()) {
			g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
			return calcBounds(p1.x, p1.y, p2.x, p2.y);

		} else {
			return boldLine(g2d, p1, p2);
		}
	}
	private Rectangle boldLine(Graphics2D g2d, Point p1, Point p2) {
		for(int i = -1; i <= 1; i++)
			g2d.drawLine(p1.x+i, p1.y, p2.x+i, p2.y);
		g2d.drawLine(p1.x, p1.y-1, p2.x, p2.y-1);
		g2d.drawLine(p1.x, p1.y+1, p2.x, p2.y+1);
		return calcBounds(p1, p2, 1, 2);
	}
}
