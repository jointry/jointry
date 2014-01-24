package jp.ac.aiit.jointry.services.picture.paint.views;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;

public abstract class PaintTool extends ImageView implements ChangeListener<PaintTool> {

    private OptionController optionController;

    abstract public void paint(Canvas canvas, Point2D start, Point2D end);

    @Override
    public void changed(ObservableValue ov, PaintTool oldValue, PaintTool newValue) {
        if (this == newValue) {
            this.setEffect(AppEffect.RAISED);
        } else {
            this.setEffect(AppEffect.LOWERED);
        }
    }

    public Object getOptionCard() {
        if (optionController != null) {
            return optionController.getOptionCard();
        }

        return null;
    }

    @Deprecated //JavaFXライブラリからのみ使用
    public void setOptionCard(Object optionCard) {
        optionController = getOptionController();
        optionController.setOptionCard((String) optionCard);
    }

    protected OptionController getOptionController() {
        if (optionController == null) {
            optionController = new OptionController();
        }
        return optionController;
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public PaintTool() {
        PaintApplication.getModel().addPtoolListener(this);
    }

    protected class OptionController {

        @FXML
        protected ToolOption selectTool;
        private Parent optionCard;

        /**
         * fxmlのonMouseClickedでコンパイルエラーが出ているが、文法は正しいはず 後からcontrollerを指定する場合
         * "#setOptionCard" でfxml側に指定する必要がある
         */
        @FXML
        protected void selectOption(MouseEvent event) {
            if (selectTool != event.getTarget()) {
                selectTool.setOff();

                selectTool = (ToolOption) event.getTarget();
                selectTool.setOn();
            }
        }

        private Parent getOptionCard() {
            return optionCard; //view自体を保存することでユーザ操作の結果を保持
        }

        private void setOptionCard(String fxml) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/" + fxml));
            fxmlLoader.setController(this);

            try {
                fxmlLoader.load();
            } catch (IOException ex) {
                Logger.getLogger(PaintTool.class.getName()).log(Level.SEVERE, null, ex);
            }

            selectTool.setOn();
            optionCard = fxmlLoader.getRoot();
        }
    }
}
