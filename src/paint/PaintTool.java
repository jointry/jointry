package paint;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.ImageIcon;

/**
 * ペイントツールのベースクラス。
 * すべてのペイントツールはこのクラスを拡張して実装する。
 * オプションを指定したい場合は、optionPanelに必要なコンポーネントを登録する。
 * @see PtPencil, PtEraser, PtBrush, PtFill, PtAirbrush, PtSpuit
 */
abstract public class PaintTool extends JButton implements IQuickPaint {
	/**
	 * @param name ツール名。ツールボタンのイメージファイルの名前をかねる。
	 * @param tip PaintToolChooser 上で表示されるチップヘルプの文字列。
	 */
	public PaintTool(String name, String tip) {
		this.name = name;
		setPreferredSize(TOOLBUTTON_SIZE);
		setToolTipText(tip);
		setIcon(new ImageIcon(getClass().getResource("/img/"+name+".png")));
	}
	final protected JPanel optionPanel = new JPanel();	// オプションパネル
	final protected Rectangle bounds = new Rectangle(); // ダメージ領域の記録用
	final String name;					// ツール名
	protected int penSize = 1;			// ペンのサイズを示すツールパラメータ

	/**
	 * 排他的選択を行うためのラジオボタンをオプションパネルに登録する。
	 */
	protected JRadioButton addSelector(ButtonGroup bg, String name, boolean on) {
		JRadioButton b = new JRadioButton(name, on);
		bg.add(b);
		optionPanel.add(b);
		return b;
	}

	/**
	 * 指定されたイメージに対するペイント処理を二つのマウス座標に対して実行する。
	 * @param img 編集中のイメージ
	 * @param p1 直前のマウス座標
	 * @param p2 最新のマウス座標
	 * @return ダメージ領域
	 */
	abstract public Rectangle paint(BufferedImage img, Point p1, Point p2);

	/** カラーパネルからメインカラーを取得する。 */
	protected Color getMainColor() { return theColorPanel.getMainColor(); }
	/** カラーパネルからサブカラーを取得する。 */
	protected Color getSubColor() { return theColorPanel.getSubColor(); }

	/**
	 * ２つの座標点が作る矩形領域をダメージ領域に設定する。
	 * @return ダメージ領域
	 */
	protected Rectangle calcBounds(int x1, int y1, int x2, int y2) {
		bounds.x = Math.min(x1, x2);
		bounds.y = Math.min(y1, y2);
		bounds.width = Math.abs(x1 - x2) + 1;
		bounds.height = Math.abs(y1 - y2) + 1;
		return bounds;
	}
	/**
	 * ２つの座標点を指定の太さのペンで描画した場合のダメージ領域を計算する。
	 * @return ダメージ領域
	 */
	protected Rectangle calcBounds(int x1, int y1, int x2, int y2, int d, int z){
		calcBounds(x1, y1, x2, y2);
		bounds.x -= d;
		bounds.y -= d;
		bounds.width += z;
		bounds.height += z;
		return bounds;
	}
	protected Rectangle calcBounds(Point p1, Point p2, int d, int z) {
		return calcBounds(p1.x, p1.y, p2.x, p2.y, d, z);
	}

	/**
	 * 指定した形状のブラシを使い、２つの座標間で直線を引く。
	 * @param g2d 描画対象
	 * @param shape ブラシの形状
	 * @param color ブラシの色
	 * @param p1 開始点
	 * @param p2 終了点
	 * @return ダメージ領域
	 */
	protected Rectangle
	brushLine(Graphics2D g2d, RectangularShape shape, Color color,
			  Point p1, Point p2) {
		g2d.setColor(color);
		fillShape(g2d, shape, p2.x, p2.y);

		if(!p1.equals(p2)) {
			int dx = p2.x - p1.x;
			int dy = p2.y - p1.y;
			double delta = Math.sqrt(dx*dx + dy*dy);
			for(int i = 0; i < delta; i++) {
				int x = p1.x + (int)(dx*i/delta);
				int y = p1.y + (int)(dy*i/delta);
				fillShape(g2d, shape, x, y);
			}
		}
		return calcBounds(p1, p2, penSize/2, penSize);
	}
	private void fillShape(Graphics2D g2d, RectangularShape shape, int x, int y){
		shape.setFrame(x - penSize/2, y - penSize/2, penSize, penSize);
		g2d.fill(shape);
		//g2d.draw(shape);
	}

	/**
	 * 排他的選択機能をもつアイコンボタン群を管理するためのパネル。
	 * 消しゴムやブラシ等のオプションパネルを実装する場合のベースクラスとして
	 * 使用することを想定している。
	 */
	protected class ToolOptionPanel extends JPanel {
		private ToolOption theOption = null;

		protected void addOption(final ToolOption opt) { addOption(opt, false); }
		protected void addOption(final ToolOption opt, boolean on) {
			this.add(opt);
			if(on) selectOption(opt);
			opt.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectOption(opt);
					}
				});
		}

		/**
		 * ツールオプションを選択し、ペンサイズの値をツールパラメータに設定する。
		 * @param opt 選択されたツールオプションボタン
		 */
		protected void selectOption(ToolOption opt) {
			if(opt == theOption) return;
			if(theOption != null) theOption.setOff();
			opt.setOn();
			theOption = opt;
			PaintTool.this.penSize = opt.penSize;
		}

		/**
		 * オンとオフの２つの状態に対応したイメージアイコンをもつボタン。
		 * ツールのオプションパラメータとしてペンサイズの値を保持する。
		 * その他のパラメータを保持したい場合は本クラスを拡張して使用する。
		 * @see PtBrush.BrushPanel, PtEraser.EraserPanel, 
		 * PtAirbrush.AirbrushPanel
		 */
		protected class ToolOption extends JButton {
			int penSize;
			private ImageIcon onIcon;
			private ImageIcon offIcon;
			protected ToolOption(String name, int penSize) {
				this.penSize = penSize;
				super.setPreferredSize(OPTIONBUTTON_SIZE);
				this.onIcon = makeImageIcon(name+"_a");
				this.offIcon = makeImageIcon(name+"_n");
				super.setIcon(offIcon);
				super.setBorder(emptyBoder);
			}
			void setOn() { setIcon(onIcon); }
			void setOff() { setIcon(offIcon); }
		}
		private ImageIcon makeImageIcon(String name) {
			return new ImageIcon(getClass().getResource("/img/"+name+".png"));
		}
	}
}
