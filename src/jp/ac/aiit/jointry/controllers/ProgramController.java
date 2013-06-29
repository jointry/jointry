package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import jp.ac.aiit.jointry.statics.TestData;

/**
 *
 * @author kanemoto
 */
public class ProgramController implements Initializable {

    @FXML
    private static ImageView image;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TestData data = new TestData();

        if (data.getCameraFile() != null) {
            WritableImage buf = SwingFXUtils.toFXImage(data.getCameraFile(), null);
            image.setImage(buf);
        }
    }

    public static ImageView getImage() {
        return image;
    }

    public static void refresh() {
        TestData data = new TestData();

        if (data.getCameraFile() != null) {
            WritableImage buf = SwingFXUtils.toFXImage(data.getCameraFile(), null);
            image.setImage(buf);
        }
    }
}
