package jp.ac.aiit.jointry.services.picture.paint.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

public class FileManager {

    private static FileChooser fc = new FileChooser();
    private static String targetDirectory = System.getProperty("user.home");

    public static void save(String title, Image image) {
        refreshChooser(title);

        File file = fc.showSaveDialog(null);
        if (file == null) {
            return; //保存先が指定されなかった
        }

        targetDirectory = file.getParent();

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Image load(String title) {
        refreshChooser(title);

        File file = fc.showOpenDialog(null);
        if (file == null) {
            return null;
        }

        targetDirectory = file.getParent();

        Image resultImage = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            resultImage = SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return resultImage;
    }

    private static void refreshChooser(String title) {
        fc.setTitle(title);
        fc.setInitialDirectory(new File(targetDirectory));

        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"));
    }
    
    private static int getStartX(PixelReader reader, double canvasWidth, double canvasHeight) {
        //始点x座標
        for (int x = 0; x < canvasWidth; x++) {
            for (int y = 0; y < canvasHeight; y++) {
                if (!reader.getColor(x, y).equals(Color.TRANSPARENT)) {
                    return x;
                }
            }
        }

        return 0;
    }

    private static int getStartY(PixelReader reader, double canvasWidth, double canvasHeight) {
        //始点y座標
        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                if (!reader.getColor(x, y).equals(Color.TRANSPARENT)) {
                    return y;
                }
            }
        }

        return 0;
    }

    private static int getEndX(PixelReader reader, double canvasWidth, double canvasHeight) {
        int endX = 0;

        //終点x座標
        for (int x = 0; x < canvasWidth; x++) {
            for (int y = 0; y < canvasHeight; y++) {
                if (!reader.getColor(x, y).equals(Color.TRANSPARENT)) {
                    endX = x;
                }
            }
        }

        return endX;
    }

    private static int getEndY(PixelReader reader, double canvasWidth, double canvasHeight) {
        int endY = 0;

        //終点y座標
        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                if (!reader.getColor(x, y).equals(Color.TRANSPARENT)) {
                    endY = y;
                }
            }
        }

        return endY;
    }
}
