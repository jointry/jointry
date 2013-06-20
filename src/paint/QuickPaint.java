package paint;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * 簡易ペイントツール
 * <applet codebase=../../build code="paint.QuickPaint" width=720 height=640>
 * </applet>
 */
public class QuickPaint extends JApplet implements IQuickPaint {

    private static final long serialVersionUID = 1L;
    private static boolean nativeLookAndFeel = true;

    public void main() {
        //String lookAndFeel = nativeLookAndFeel
        //        ? UIManager.getSystemLookAndFeelClassName()
        //        : UIManager.getCrossPlatformLookAndFeelClassName();

        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                makeQuickPaint();
            }
        });
    }
    static final int CANVAS_WIDTH = 720;
    static final int CANVAS_HEIGHT = 640;
    static final String FRAME_TITLE = "QuickPaint V0.1";

    public static void makeQuickPaint() {
        JFrame frame = new JFrame(FRAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new QuickPaint(CANVAS_WIDTH, CANVAS_HEIGHT));
        frame.pack();
        frame.setVisible(true);
    }
    private JLabel cursorPos = makeCursorPos();
    private PaintCanvas paintCanvas;
    private int width, height;
    
    private static BufferedImage img;

    public QuickPaint(int width, int height) {
        this.width = width;
        this.height = height;
        paintCanvas = new PaintCanvas(cursorPos, img);
        initPaintPanel();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    public QuickPaint(BufferedImage img) {
        this.img = img;
    }

    /**
     * アプレットから起動する場合の初期化処理
     */
    @Override
    public void init() {
        this.setSize(720, 600);
        JPanel p = initPaintPanel();
        p.setBorder(frameBoder);
    }

    /**
     * ペイントパネルの作成 PaintPanel WEST: toolPanel CENTER: paintCanvas SOUTH:
     * theColorPanel
     */
    private JPanel initPaintPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeToolPanel(), BorderLayout.WEST);
        p.add(paintCanvas, BorderLayout.CENTER);
        p.add(theColorPanel, BorderLayout.SOUTH);
        this.setContentPane(p);
        return p;
    }

    /**
     * ツールパネルの作成
     */
    private JPanel makeToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setPreferredSize(new Dimension(124, 460));
        toolPanel.add(theToolMgr);
        toolPanel.add(makeClearButton());
        setFileIOButtons(toolPanel);
        toolPanel.add(cursorPos);
        return toolPanel;
    }

    /**
     * ペイントキャンバスのクリア
     */
    private JButton makeClearButton() {
        JButton b = new JButton("クリア");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paintCanvas.clear();
            }
        });
        return b;
    }

    /**
     * ファイルからの読込みと書出し
     */
    private void setFileIOButtons(JPanel toolPanel) {
        final JTextField fname_tf = new JTextField("img.png");
        fname_tf.setPreferredSize(new Dimension(100, 20));

        JButton read_b = new JButton("読込");
        read_b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paintCanvas.readImage(fname_tf.getText());
            }
        });
        JButton write_b = new JButton("書出");
        write_b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paintCanvas.writeImage(fname_tf.getText());
            }
        });
        toolPanel.add(fname_tf);
        toolPanel.add(read_b);
        toolPanel.add(write_b);
    }

    /**
     * ペイントキャンバス上のカーソル位置表示
     */
    private JLabel makeCursorPos() {
        JLabel pos = new JLabel();
        pos.setPreferredSize(new Dimension(105, 18));
        pos.setHorizontalAlignment(SwingConstants.CENTER);
        pos.setBorder(loweredBoder);
        return pos;
    }
}
