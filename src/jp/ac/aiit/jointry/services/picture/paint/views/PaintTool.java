package jp.ac.aiit.jointry.services.picture.paint.views;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;
import jp.ac.aiit.jointry.services.picture.paint.ctrl.OptionController;

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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/" + optionCard));

        try {
            fxmlLoader.load();
            optionController = fxmlLoader.getController();
        } catch (IOException ex) {
            Logger.getLogger(PaintTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected OptionController getOptionController() {
        return optionController;
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public PaintTool() {
        PaintApplication.getModel().addPtoolListener(this);
    }
}
