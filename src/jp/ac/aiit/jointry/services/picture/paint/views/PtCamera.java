package jp.ac.aiit.jointry.services.picture.paint.views;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.services.picture.camera.CameraApplication;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;

public class PtCamera extends PaintTool {

    @Override
    public void changed(ObservableValue ov, PaintTool oldValue, PaintTool newValue) {
        if (this == newValue) {
            this.setEffect(AppEffect.RAISED);
            startCamera();
        } else {
            this.setEffect(AppEffect.LOWERED);
        }
    }

    @Override
    public void paint(Canvas canvas, Point2D start, Point2D end) {
        //なにもしない
    }

    private void startCamera() {
        final CameraApplication app = new CameraApplication();

        try {
            Stage stage = app.start(this.getScene().getWindow());

            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    if (app.getResult() != null) {
                        PaintApplication.getModel().setImage(app.getResult());
                    }

                    PtCamera.this.setEffect(AppEffect.LOWERED); //終わったら表示上は選択解除
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(PtCamera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
