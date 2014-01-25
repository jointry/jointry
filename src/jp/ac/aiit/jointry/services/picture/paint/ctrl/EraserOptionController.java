package jp.ac.aiit.jointry.services.picture.paint.ctrl;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class EraserOptionController extends OptionController {

    @FXML
    private CheckBox colorEraser;

    public boolean isColor() {
        return colorEraser.isSelected();
    }
}
