/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.paint;

import java.awt.Point;
import java.io.File;
import java.net.MalformedURLException;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 *
 * @author kanemoto
 */
public class PtFileExt extends PaintTool {

    FileChooser chooser = new FileChooser();
    File file = null;

    public void showOpenDialog(Window window) {
        file = chooser.showOpenDialog(null);
    }
    
    public PtFileExt(String resource, String tip) {
        super(resource, tip);

        //タイトル固定
        chooser.setTitle("select file");
        //ホーム指定
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));

        ObservableList<ExtensionFilter> filter = chooser.getExtensionFilters();
        filter.add(new ExtensionFilter("PNG", "*.png"));
        filter.add(new ExtensionFilter("JPG", "*.jpg"));
        filter.add(new ExtensionFilter("GIF", "*.gif"));
        filter.add(new ExtensionFilter("全てのファイル", "*"));
    }



    @Override
    public void paint(Canvas canvas, Point pS, Point pE, Color color) {
        if (file != null) {
            try {
                //ファイル選択されている場合
                String imageFile = file.toURI().toURL().toExternalForm();

                Image image = new Image(imageFile);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.drawImage(image, 0, 0);
            } catch (MalformedURLException ex) {
                //ex.printStackTrace();
            }
        }
    }
}
