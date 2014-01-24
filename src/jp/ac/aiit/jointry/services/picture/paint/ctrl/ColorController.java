package jp.ac.aiit.jointry.services.picture.paint.ctrl;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;

public class ColorController implements Initializable, ChangeListener<Color> {

    @FXML
    private ColorPicker picker;

    @FXML
    protected void selectColor(ActionEvent event) {
        PaintApplication.getModel().setColor(picker.getValue());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PaintApplication.getModel().addColorListener(this);
        PaintApplication.getModel().setColor(Color.RED);
    }

    @Override
    public void changed(ObservableValue ov, Color oldValue, Color newValue) {
        if (!picker.getValue().equals(newValue)) {
            picker.setValue(newValue);
            picker.fireEvent(new ActionEvent()); //setValueだけではUIが更新されない       
        }
    }
}
