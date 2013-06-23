package jp.ac.aiit.jointry.paint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * マウス操作によるイメージのペイント処理を実行する。 マウスボタンの押下、マウスの移動、ドラッグのイベントを受け、
 * マウスカーソルの位置の表示と、現在のペイントツールを使用した ペイント処理を実行する。
 *
 */
public class PaintCanvas extends JComponent
        implements IQuickPaint, MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 1L;
    private JLabel cursorPos;			// マウス座標の表示領域
    private Point p1 = new Point();		// 直前のマウス座標
    private Point p2 = new Point();		// 最新のマウス座標
    private BufferedImage img = null;	// 編集中のイメージ

    public PaintCanvas(JLabel cursorPos) {
        this.cursorPos = cursorPos;
        //setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public PaintCanvas(JLabel cursorPos, BufferedImage img) {
        this.cursorPos = cursorPos;
        //setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
        this.img = img;
    }

    /**
     * MouseListenerの実装
     */
    @Override
    public void mousePressed(MouseEvent e) {
        p1.setLocation(e.getPoint());
        p2.setLocation(p1);
        editImage();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        cursorPos.setText(""); // 座標表示をオフにする
    }

    /**
     * MouseMotionListenerの実装
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        showPos(e);
        p2.setLocation(e.getPoint());
        editImage();
        p1.setLocation(p2);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        showPos(e);
    }

    private void showPos(MouseEvent e) {
        cursorPos.setText(e.getX() + ", " + e.getY());
    }

    /**
     * キャンバス上への描画
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img == null) {
            clear();
        }
        assert startTime("paintComponent: ");
        g.drawImage(img, 0, 0, this);
        assert lapTime();
    }

    /**
     * 作画ツールを実行し、その結果を描画する。
     */
    private void editImage() {
        assert startTime("editImage: ");
        Rectangle b = theToolMgr.getTool().paint(img, p1, p2);

        assert lapTime();

        Graphics g = getGraphics();
        g.setClip(0, 0, img.getWidth(), img.getHeight());
        int x2 = b.x + b.width;
        int y2 = b.y + b.height;
        g.drawImage(img, b.x, b.y, x2, y2, b.x, b.y, x2, y2, this);

        assert lapTime();
        assert drawBounds(g, b);
    }

    /**
     * デバッグ用： -ea オプションの指定があったら、作画ツールで変更された矩形領域を描画する。
     */
    private boolean drawBounds(Graphics g, Rectangle b) {
        if (b != null) {
            g.setColor(Color.RED);
            g.drawRect(b.x - 1, b.y - 1, b.width, b.height);
        }
        return true;
    }
    /**
     * 描画時間測定用
     */
    private long startTime;

    private boolean startTime(String msg) {
        this.startTime = System.nanoTime();
        print("\n" + msg);
        return true;
    }

    private boolean lapTime() {
        long time = System.nanoTime();
        long laptime = (time - startTime) / 1000; // micro sec.
        print("\t" + (laptime / 1000.0) + " msec");
        startTime = time;
        return true;
    }

    /**
     * キャンバスの初期化
     */
    public void clear() {
        img = (BufferedImage) createImage(getSize().width, getSize().height);
        repaint();
    }

    /**
     * 画像ファイルの読込み
     */
    public void readImage(String fileName) {
        assert printImageFormats();
        try {
            img = ImageIO.read(new File(fileName));
            repaint();
        } catch (Exception e) {
        }
    }

    /**
     * 画像ファイルへの書出し
     */
    public void writeImage(String fileName) {
        assert printImageFormats();
        try {
            int index = fileName.lastIndexOf(".");
            String suffix = "png";
            if (index > 0) {
                suffix = fileName.substring(index + 1);
            }
            ImageIO.write(img, suffix, new File(fileName));
        } catch (Exception e) {
        }
    }

    boolean printImageFormats() {
        print("\n==== reader format\n");
        for (String name : ImageIO.getReaderFormatNames()) {
            print("\t" + name);
        }
        print("\n==== writer format\n");
        for (String name : ImageIO.getWriterFormatNames()) {
            print("\t" + name);
        }
        print("\n");
        return true;
    }

    void print(String msg) {
        System.out.print(msg);
    }
}
