package paint;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.JColorChooser;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import static java.awt.Color.*;
import static javax.swing.ScrollPaneConstants.*;

/**
 * メインとサブの２種類の色の管理を行う。
 * ユーザは、色指定をしたい方をボタンで選択し、色選択ツール JColorChooser を
 * 使って変更できる。また、色選択ツールから setColorCell() を使って変更できる。
 * 変更の結果は、直ちにボタンの表示色に反映される。
 * メインとサブの現在の色は、それぞれ getMainColor() と getSubColor() で
 * 取得できる。
 */
public class ColorPanel extends JPanel implements IQuickPaint {
	private static final long serialVersionUID = 1L;
	private ColorCell mainColorCell = new ColorCell(BLACK,10,10,loweredBoder);
	private ColorCell subColorCell = new ColorCell(WHITE,42,42,raisedBoder);
	private ColorCell theColorCell = mainColorCell;
	private Color rgbBackground = new Color(0xFF, 0xFF, 0xA0);
	
	public ColorPanel() {
		JPanel paintBox = new JPanel(null); // 自由レイアウト
		paintBox.setPreferredSize(new Dimension(102, 102));
		paintBox.setBorder(loweredBoder);
		paintBox.add(mainColorCell);
		paintBox.add(subColorCell);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(paintBox);
		this.add(new ColorChooser());
	}

	public Color getMainColor() { return mainColorCell.color; }
	public Color getSubColor() { return subColorCell.color; }
	public void setColorCell(Color color) { theColorCell.setColor(color); }

	private class ColorCell extends JComponent {
		Color color;
		ColorCell(Color color, int x, int y, Border border) {
			this.color = color;
			this.setBounds(x, y, 50, 50);
			this.setBorder(border);
			this.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						selectCell((ColorCell)e.getSource());
					}
				});
		}
		private void selectCell(ColorCell cell) {
			if(cell == theColorCell) return;
			theColorCell.setBorder(raisedBoder);
			theColorCell = cell;
			theColorCell.setBorder(loweredBoder);
		}
		void setColor(Color color) {
			this.color = color;
			repaint();
		}
		@Override public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(color);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(rgbBackground);
			g.fillRect(24, 15, 22, 31);
			g.setColor(BLACK);
			drawInt(g, color.getRed(),   25);
			drawInt(g, color.getGreen(), 35);
			drawInt(g, color.getBlue(),  45);
		}
		private void drawInt(Graphics g, int v, int offset) {
			g.drawString(String.format("%3d", v), 25, offset);
		}
	}

	private class ColorChooser extends JScrollPane {
		ColorChooser() {
			// x = 18 + 11*31 + 1 + 68 = 428
			this.setPreferredSize(new Dimension(428, 106));
			this.setBorder(emptyBoder);
			this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
			this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);

			final JColorChooser colorChooser = new JColorChooser();
			colorChooser.getSelectionModel().
				addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							theColorCell.setColor(colorChooser.getColor());
						}
					});

			JViewport view = this.getViewport();
			view.setView(colorChooser);
			view.setViewPosition(new Point(64, 76));
			this.setViewportView(colorChooser);
		}
	}
}
