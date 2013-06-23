package jp.ac.aiit.jointry.paint;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import java.util.LinkedList;
import java.util.Queue;
import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;

/**
 * 「塗りつぶし」ツールの実装。
 * マウスをクリックした位置の色が連続する領域を描画色で塗りつぶす。
 * オプションパネルで５種類のモードの1つを選択する。
 * 「単一色」は描画色の１色で塗りつぶす。それ以外はグラデーションによる
 * 塗りつぶしであり、垂直方向と水平方向のそれぞれに対して、濃淡の向きを
 * 変えた４つのモードがある。
 */
public class PtFill extends PaintTool {
	private JRadioButton nomal_rb;
	private JRadioButton whiteTop_rb;
	private JRadioButton blackTop_rb;
	private JRadioButton whiteLeft_rb;
	private JRadioButton blackLeft_rb;

	public PtFill(String name, String tip) {
		super(name, tip);
		ButtonGroup bg = new ButtonGroup();
		nomal_rb     = super.addSelector(bg, "単一色", true);
		whiteTop_rb  = super.addSelector(bg, "下へ次第に濃く", false);
		blackTop_rb  = super.addSelector(bg, "下へ次第に薄く", false);
		whiteLeft_rb = super.addSelector(bg, "右へ次第に濃く", false);
		blackLeft_rb = super.addSelector(bg, "右へ次第に薄く", false);
	}

	private int gradationRate() {
		return
			nomal_rb.isSelected() ? 0 :
			(blackTop_rb.isSelected() || blackLeft_rb.isSelected()) ? 1 : -1;
	}
	private boolean verticalSelected() {
		return whiteTop_rb.isSelected() || blackTop_rb.isSelected();
	}

	private static int BUMMY_RGB = FillImage.DUMMY_COLOR.getRGB();

	@Override public Rectangle paint(BufferedImage img, Point p1, Point p2) {
		Graphics2D g2d = img.createGraphics();
		bounds.setBounds(0, 0, 0, 0);
		int x = p2.x;
		int y = p2.y;
		if(0 <= x && x < img.getWidth() && 0 <= y && y < img.getHeight()) {
			Color fillColor = getMainColor();
			int seedRGB = img.getRGB(x,y);
			boolean sameColor = (seedRGB == fillColor.getRGB());
			if(sameColor && (nomal_rb.isSelected() || seedRGB == BUMMY_RGB)) {
				// 同色で単一色の塗り潰しは意味が無い
				// 同色で、それが塗りつぶしのダミー色の場合は実行できない
			} else {
				FillImage fillImg = verticalSelected() ?
					new FillImage(g2d, img, fillColor, gradationRate(), x, y) :
					new FillImageH(g2d, img, fillColor, gradationRate(), x, y);
				fillImg.fill(bounds);
			}
		}
		return bounds;
	}
}

/**
 * ピクセル単位の塗り潰し処理と、塗り潰し領域の管理を行う。
 * グラデーションの指定のある場合は、scanImg に塗り潰し領域を記録する。
 * 描画の際に描画領域の上限と下限を記録し、終了時に再描画領域に設定する。
 * FillImage は垂直方向のグラデーションを実装する。
 */
class FillImage {
	private BufferedImage img;			// 塗り潰し対象イメージ
	protected Graphics2D g2d;			// 塗り潰し対象イメージ用のGC
	protected Color fillColor;			// 塗り潰し色
	private BufferedImage scanImg;		// 塗り潰し領域管理用
	protected Graphics2D scanG2d;		// 塗り潰し領域管理用のGC
	private int gradationRate;			// グラデーションの比率(0, -1, 1)
	private int clickY;					// 塗り潰しの開始Y座標
	private int minX, maxX, minY, maxY;	// 描画した領域の上限と下限
	private int seedRGB;				// 塗り潰し対象色(クリック位置の色)のRGB
	protected int width, height;		// 対象イメージのサイズ
	protected Queue<Point> queue = new LinkedList<Point>();
	public static Color DUMMY_COLOR = new Color(0, 0, 1);

	FillImage(Graphics2D g2d, BufferedImage img, Color fillColor,
			  int gradationRate, int x, int y) {
		this.g2d = g2d;
		this.img = img;
		this.fillColor = fillColor;
		this.gradationRate = gradationRate;
		this.clickY = y;
		this.width = img.getWidth();
		this.height = img.getHeight();
		seedRGB = img.getRGB(x, y);
		minX = width;  maxX = 0;
		minY = height; maxY = 0;
		queue.offer(new Point(x, y));

		assert p("type: %2d  seedRGB: %08x  fill: %08x\n",
				 img.getType(), seedRGB, fillColor.getRGB());

		if(gradationRate == 0) g2d.setColor(fillColor);
		if(gradationRate == 0 && !needScanImage()) {
			scanImg = img;
			scanG2d = null;
		} else {
			scanImg = new BufferedImage(width, height, TYPE_3BYTE_BGR);
			scanG2d = scanImg.createGraphics();
			scanG2d.drawImage(img, 0, 0, null);
			scanG2d.setColor(DUMMY_COLOR);
			assert p("start scanning image");
		}
	}
	private boolean needScanImage() {
		try {
			int fillRGB = fillColor.getRGB();
			return img.getColorModel().getRGB(fillRGB) != fillRGB; // && false;
		} catch(IllegalArgumentException e) {
			return true;
		}
	}
	static final int LOOP_LIMIT = 1000000;
	void fill(Rectangle bounds) {
		int n = 0;
		while(queue.size() > 0 && n++ < LOOP_LIMIT) {
			fillLine(queue.poll());
		}
		if(n >= LOOP_LIMIT) p("#calls of fillLine = " + n);
		assert p("filling terminated: queue.size() = " + queue.size() +
				 "\t#fillLine = " + n);
		bounds.setBounds(minX, minY, maxX-minX+1, maxY-minY+1);
	}

	protected void fillLine(Point pt) {
		int y = pt.y;
		int x1, x2;
		for(x1 = pt.x; 0 < x1; x1--) {
			if(!sameColorAsSeed(x1-1, y)) break;
		}
		for(x2 = pt.x; x2 < width - 1; x2++) {
			if(!sameColorAsSeed(x2+1, y)) break;
		}
		if(scanG2d != null) {
			g2d.setColor(gradateColor(fillColor, y - clickY));
			scanG2d.drawLine(x1, y, x2, y);
		}
		drawPixel(x1, y, x2, y);

		if(y > 0) scanLine(x1, x2, y-1);
		if(y < height-1) scanLine(x1, x2, y+1);
	}
	private void scanLine(int x1, int x2, int y) {
		boolean equalColor = false;
		for(int x = x1; x <= x2; x++) {
			boolean eq = sameColorAsSeed(x, y);
			if(eq && !equalColor)
				queue.offer(new Point(x, y));
			equalColor = eq;
		}
	}

	protected void drawPixel(int x1, int y1, int x2, int y2) {
		g2d.drawLine(x1, y1, x2, y2);
		if(minX > x1) minX = x1;
		if(maxX < x2) maxX = x2;
		if(minY > y1) minY = y1;
		if(maxY < y2) maxY = y2;
	}

	protected boolean sameColorAsSeed(int x, int y) {
		return scanImg.getRGB(x, y) == seedRGB;
	}

	protected Color gradateColor(Color c, int delta) {
		if(gradationRate == 0) return c;
		return new Color(byteCheck(c.getRed()   + delta*gradationRate),
						 byteCheck(c.getGreen() + delta*gradationRate), 
						 byteCheck(c.getBlue()  + delta*gradationRate));
	}
	private int byteCheck(int i) {
		if(i <= 0) return 1;
		if(i > 254) return 254;
		return i;
	}

	boolean p(String s) {
		System.out.println(s);
		return true;
	}
	boolean p(String f, Object... params) {
		System.out.printf(f, params);
		return true;
	}
}

/**
 * FillImageH は水平方向のグラデーションを実装する。
 */
class FillImageH extends FillImage {
	private int clickX;
	FillImageH(Graphics2D g2d, BufferedImage img, Color fillColor,
			   int gradationRate, int x, int y) {
		super(g2d, img, fillColor, gradationRate, x, y);
		clickX = x;
	}
	@Override protected void fillLine(Point pt) {
		int x = pt.x;
		int y1, y2;
		for(y1 = pt.y; 0 < y1; y1--) {
			if(!sameColorAsSeed(x, y1-1)) break;
		}
		for(y2 = pt.y; y2 < height - 1; y2++) {
			if(!sameColorAsSeed(x, y2+1)) break;
		}
		if(scanG2d != null) {
			g2d.setColor(gradateColor(fillColor, x - clickX));
			scanG2d.drawLine(x, y1, x, y2);
		}
		drawPixel(x, y1, x, y2);

		if(x > 0) scanLine(y1, y2, x-1);
		if(x < width-1) scanLine(y1, y2, x+1);
	}
	private void scanLine(int y1, int y2, int x) {
		boolean equalColor = false;
		for(int y = y1; y <= y2; y++) {
			boolean eq = sameColorAsSeed(x, y);
			if(eq && !equalColor)
				queue.offer(new Point(x, y));
			equalColor = eq;
		}
	}
}
