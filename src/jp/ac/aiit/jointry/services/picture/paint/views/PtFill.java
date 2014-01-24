package jp.ac.aiit.jointry.services.picture.paint.views;

import java.awt.Point;
import java.util.LinkedList;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;

public class PtFill extends PaintTool {

    @Override
    public void paint(Canvas canvas, Point2D start, Point2D end) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT); //透過色をそのまま取得するため
        WritableImage snapshot = canvas.snapshot(params, null);

        //編集のためRWを取得
        PixelWriter writer = snapshot.getPixelWriter();
        PixelReader reader = snapshot.getPixelReader();

        Color fillColor = PaintApplication.getModel().getColor();
        Color seedColor = reader.getColor((int) start.getX(), (int) start.getY());

        //塗潰す色と同じであればなにもしない
        if (fillColor.equals(seedColor)) return;

        //塗りつぶす最大範囲
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        //塗りつぶす位置リスト
        LinkedList<Point> queue = new LinkedList();
        queue.add(new Point((int) start.getX(), (int) start.getY()));

        while (queue.peek() != null) {
            Point point = queue.poll();

            //塗りつぶし対象の色ではないため
            if (!reader.getColor(point.x, point.y).equals(seedColor)) continue;

            writer.setColor(point.x, point.y, fillColor); //実際の塗りつぶし処理

            //上下左右のdotをキューへ登録し塗潰す色を検索
            if (point.x + 1 < width) queue.add(new Point(point.x + 1, point.y));
            if (point.y + 1 < height) queue.add(new Point(point.x, point.y + 1));
            if (point.x - 1 >= 0) queue.add(new Point(point.x - 1, point.y));
            if (point.y - 1 >= 0) queue.add(new Point(point.x, point.y - 1));
        }

        //加工済みの画像を再描画
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(snapshot, 0, 0);
    }
}
