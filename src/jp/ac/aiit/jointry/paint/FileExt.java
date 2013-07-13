/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.paint;

import java.io.File;
import java.net.MalformedURLException;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * 外部ファイルのopen close.
 *
 * @author kanemoto
 */
public class FileExt {

    private FileChooser chooser = new FileChooser();

    public void showOpenDialog(Window window, Canvas canvas) {
        File choose = chooser.showOpenDialog(null);

        if (choose != null) {
            try {
                //ファイル選択されている場合
                String imageFile = choose.toURI().toURL().toExternalForm();

                Image image = new Image(imageFile);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.drawImage(image, 0, 0);
            } catch (MalformedURLException ex) {
                //ex.printStackTrace();
            }
        }
    }

    public FileExt() {
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
}
