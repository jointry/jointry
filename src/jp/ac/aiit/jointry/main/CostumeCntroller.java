package jp.ac.aiit.jointry.main;

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
public class CostumeCntroller implements Initializable {

    @FXML
    private ImageView images;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TestData data = new TestData();

        if (data.getCameraFile() != null) {
            WritableImage buf = SwingFXUtils.toFXImage(data.getCameraFile(), null);
            images.setImage(buf);
        }
    }

    public void refresh() {
        TestData data = new TestData();

        if (data.getCameraFile() != null) {
            WritableImage buf = SwingFXUtils.toFXImage(data.getCameraFile(), null);
            images.setImage(buf);
        }
    }
}
