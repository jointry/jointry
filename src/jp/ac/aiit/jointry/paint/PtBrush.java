package jp.ac.aiit.jointry.paint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.awt.GridLayout;

/**
 * 「ブラシ」ツールの実装。
 * ブラシの形状としては、円、正方形、スラッシュ型、バックスラッシュ型に対応する。
 * ブラシのサイズは、小、中、大の３種類に対応する。
 */
public class PtBrush extends PaintTool {
	public PtBrush(String name, String tip) {
		super(name, tip);
		optionPanel.add(new BrushPanel());
	}

	private RectangularShape shape;		// ブラシの形状を示すツールパラメータ

	@Override public Rectangle paint(BufferedImage img, Point p1, Point p2) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(getMainColor());

		if(shape != null) {
			return brushLine(g2d, shape, getMainColor(), p1, p2);

		} else {
			int z = Math.abs(penSize);	// penSize < 0 の場合は 右下り直線ブラシ
			int r = z / 2;
			int d = penSize / 2;

			paintLineBrush(g2d, p2.x, p2.y, r, d);

			if(!p1.equals(p2)) {
				int dx = p2.x - p1.x;
				int dy = p2.y - p1.y;
				double delta = Math.sqrt(dx*dx + dy*dy);
				for(int i = 0; i < delta; i++) {
					int x = p1.x + (int)(dx*i/delta);
					int y = p1.y + (int)(dy*i/delta);
					paintLineBrush(g2d, x, y, r, d);
				}
			}
			return calcBounds(p1, p2, r, z);
		}
	}
	private void paintLineBrush(Graphics2D g2d, int x, int y, int r, int d) {
		g2d.drawLine(x - r, y + d, x + r, y - d);
	}

	private static Rectangle2D.Double rect = new Rectangle2D.Double();
	private static Ellipse2D.Double ellipse = new Ellipse2D.Double();
	/**
	 * ブラシツールのオプションパネルの実装。
	 */
	class BrushPanel extends ToolOptionPanel {
		BrushPanel() {
			setLayout(new GridLayout(4, 3, 4, 4));
			addOption(new BrushOption("brush_00", 7, ellipse), true); // 円
			addOption(new BrushOption("brush_01", 5, ellipse));
			addOption(new BrushOption("brush_02", 2, ellipse));
			addOption(new BrushOption("brush_10", 8, rect)); // 正方形
			addOption(new BrushOption("brush_11", 5, rect));
			addOption(new BrushOption("brush_12", 2, rect));
			addOption(new BrushOption("brush_20", 8, null)); // スラッシュ
			addOption(new BrushOption("brush_21", 5, null));
			addOption(new BrushOption("brush_22", 2, null));
			addOption(new BrushOption("brush_30", -8, null)); // バックスラッシュ
			addOption(new BrushOption("brush_31", -5, null));
			addOption(new BrushOption("brush_32", -2, null));
		}

		/**
		 * サイズに加えてブラシ形状のツールパラメータを設定する。
		 */
		@Override protected void selectOption(ToolOption opt) {
			super.selectOption(opt);
			PtBrush.this.shape = ((BrushOption)opt).shape;
		}
		/**
		 * ブラシツールは、サイズに加えてブラシ形状をパラメータとしてもつ。
		 */
		class BrushOption extends ToolOption {
			RectangularShape shape;
			BrushOption(String name, int penSize, RectangularShape shape) {
				super(name, penSize);
				this.shape = shape;
			}
		}
	}
}
