package jp.ac.aiit.jointry.services.picture.paint.ctrl;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import jp.ac.aiit.jointry.services.picture.paint.views.ToolOption;

public class OptionController implements Initializable {

    @FXML
    protected ToolOption selectTool;
    @FXML
    protected Parent optionCard;

    @FXML
    protected void selectOption(MouseEvent event) {
        if (selectTool != event.getTarget()) {
            selectTool.setOff();

            selectTool = (ToolOption) event.getTarget();
            selectTool.setOn();
        }
    }

    public Parent getOptionCard() {
        return optionCard; //view自体を保存することでユーザ操作の結果を保持
    }

    public ToolOption getSelectTool() {
        return selectTool;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectTool.setOn();
    }
}
