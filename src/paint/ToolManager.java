package paint;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;

/**
 * ToolManager は、複数個のペイントツールを管理するパネルである。
 * 各ペイントツールは PaintTool のサブクラスとして実装される。
 * ToolManager は PaintToolChooser と ペイントツールのオプションを表示する
 * CardDeck の２つのコンポーネントをもつ。
 * PaintToolChooser は、ペイントツールのアイコンボタンを２列の
 * グリッドレイアウトで配置し、排他的な選択を管理する。
 * 選択したペイントツールのオプションは対応するオプションパネルで設定する。
 * このオプションパネルは、CardDeck の領域に表示される。
 * オプションパネルの実装はペイントツールの実装の一部として行う。
 * ペイントツールはこのオプションで指定されたパラメータを使用して
 * 描画処理を実行する。
 */
public class ToolManager extends JPanel implements IQuickPaint {
	private static final long serialVersionUID = 1L;
	private PaintTool thePaintTool = null;	// 現在選択されているペイントツール
	private CardDeck optionCardDeck;		// ツールオプションを表示する領域

	public ToolManager() {
		this.setPreferredSize(new Dimension(124, 340));
		optionCardDeck = new CardDeck(105, 158);
		this.add(new PaintToolChooser());
		this.add(optionCardDeck);
	}

	public PaintTool getTool() { return thePaintTool; }

	/**
	 * CardDeck は、現在のペイントツールに対応するオプションパネルを
	 * カードレイアウトで切り替えて教示する。
	 * ペイントツールを PaintToolChooser に登録する時に、
	 * 対応するオプションパネルもこのパネルに登録され、
	 * ツールの選択変更の際に切り替わる。
	 */
	private static class CardDeck extends JPanel {
		private CardLayout cardLayout = new CardLayout();
		CardDeck(int width, int height) {
			setLayout(cardLayout);
			setPreferredSize(new Dimension(width, height));
			setBorder(loweredBoder);
		}
		void addTool(PaintTool ptool) {
			add(ptool.optionPanel, ptool.name);
		}
		void showCard(PaintTool ptool) {
			cardLayout.show(this, ptool.name);
		}
	}

	/**
	 * ペイントツールのアイコンボタンを２列のグリッドレイアウトで配置し、
	 * 排他的な選択を管理する。
	 */
	private class PaintToolChooser extends JPanel {
		PaintToolChooser() {
			setLayout(new GridLayout(0, 2, 4, 4));
			addTool(new PtPencil("pencil", "鉛筆"),  true);
			addTool(new PtEraser("eraser", "消しゴム"));
			addTool(new PtBrush("brush", "ブラシ"));
			addTool(new PtFill("fill", "塗りつぶし"));
			addTool(new PtAirbrush("airbrush", "エアブラシ"));
			addTool(new PtSpuit("spuit", "色の選択"));
		}
		private void addTool(final PaintTool ptool) { addTool(ptool, false); }
		private void addTool(final PaintTool ptool, boolean on){
			optionCardDeck.addTool(ptool);
			this.add(ptool);
			if(on) selectTool(ptool);
			ptool.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectTool(ptool);
					}
				});
		}
		private void selectTool(PaintTool ptool) {
			if(ptool == thePaintTool) return;
			if(thePaintTool != null) thePaintTool.setBorder(raisedBoder);
			ptool.setBorder(loweredBoder);
			thePaintTool = ptool;
			optionCardDeck.showCard(ptool);
		}
	}
}
