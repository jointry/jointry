package paint;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import static javax.swing.border.BevelBorder.RAISED;
import static javax.swing.border.BevelBorder.LOWERED;

public interface IQuickPaint {
	static Dimension TOOLBUTTON_SIZE = new Dimension(50, 50);
	static Dimension OPTIONBUTTON_SIZE = new Dimension(24, 24);
	static Border loweredBoder = BorderFactory.createBevelBorder(LOWERED);
	static Border raisedBoder = BorderFactory.createBevelBorder(RAISED);
	static Border emptyBoder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
	static Border frameBoder = BorderFactory.createLineBorder(Color.GRAY, 3);
	static ColorPanel theColorPanel = new ColorPanel();
	static ToolManager theToolMgr = new ToolManager();
}
