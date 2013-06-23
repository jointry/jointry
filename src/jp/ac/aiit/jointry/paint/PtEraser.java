package jp.ac.aiit.jointry.paint;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;

/**
 * 「消しゴム」ツールの実装。
 * 通常の消去は白色での描画とする。
 * 「カラ消し」を選択すると削除色（カラーマップのサブカラー）だけを
 * 描画色で作画する。
 */
public class PtEraser extends PaintTool {
	private JCheckBox isColorEraser = new JCheckBox("カラ消し");

	public PtEraser(String name, String tip) {
		super(name, tip);
		optionPanel.add(new EraserPanel());
	}

	@Override public Rectangle paint(BufferedImage img, Point p1, Point p2) {
		Graphics2D g2d = img.createGraphics();
		if(isColorEraser.isSelected()) {
			eraseColor(g2d, img, p2.x - penSize/2, p2.y - penSize/2);
			return bounds;
		} else {
			Rectangle2D.Double rect = new Rectangle2D.Double();
			return brushLine(g2d, rect, Color.WHITE, p1, p2);
		}
	}
	private void eraseColor(Graphics2D g2d, BufferedImage img, int px, int py) {
		int eraserRGB = getSubColor().getRGB();
		int x1 = Math.max(px, 0);
		int x2 = Math.min(px + penSize, img.getWidth());
		int y1 = Math.max(py, 0);
		int y2 = Math.min(py + penSize, img.getHeight());
		g2d.setColor(getMainColor());
		for(int x = x1; x < x2; x++) {
			for(int y = y1; y < y2; y++) {
				if(img.getRGB(x, y) == eraserRGB)
					g2d.drawLine(x, y, x, y);
			}
		}
		calcBounds(x1, y1, x2, y2);
	}

	class EraserPanel extends ToolOptionPanel {
		EraserPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(isColorEraser);
			addOption(new ToolOption("eraser_1", 4), true);
			addOption(new ToolOption("eraser_2", 6));
			addOption(new ToolOption("eraser_3", 8));
			addOption(new ToolOption("eraser_4", 10));
			addOption(new ToolOption("eraser_5", 12));
		}
	}
}
