package jp.ac.aiit.jointry.controllers;

import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.services.paint.FileExt;
import jp.ac.aiit.jointry.services.paint.PtClear;
import jp.ac.aiit.jointry.services.paint.Save;
import jp.ac.aiit.jointry.util.ParameterAware;
import jp.ac.aiit.jointry.util.StageUtil;

public class PaintController implements Initializable, ParameterAware<Image> {

    @FXML
    private Canvas canvas;
    @FXML
    private ColorPicker color;
    @FXML
    private PaintOptionController paintOptionController;
    private Stage stage;
    private Point windowPoint = new Point();
    private Point pS = new Point();
    private Point pE = new Point();
    private Image result;

    //画面移動
    @FXML
    protected void handleWindowMovePressed(MouseEvent event) {
        stage = (Stage) canvas.getScene().getWindow();
        Scene scene = stage.getScene();
        windowPoint.x = (int) (event.getSceneX() + scene.getX());
        windowPoint.y = (int) (event.getSceneY() + scene.getY());
    }

    @FXML
    protected void handleWindowMoveDragged(MouseEvent event) {
        stage.setX(event.getScreenX() - windowPoint.x);
        stage.setY(event.getScreenY() - windowPoint.y);
    }

    //マウス描画
    @FXML
    protected void handleCanvasPressed(MouseEvent event) {
        pS.setLocation(event.getX(), event.getY());
        pE.setLocation(pS);

        paintOptionController.getPaintTool().paint(canvas, pS, pE, color.getValue());
    }

    @FXML
    protected void handleCanvasDragged(MouseEvent event) {
        pE.setLocation(event.getX(), event.getY());
        paintOptionController.getPaintTool().paint(canvas, pS, pE, color.getValue());
        pS.setLocation(pE);
    }

    //ボタン
    @FXML
    protected void handleSaveButtonAction(ActionEvent event) {
        Save save = new Save();
        save.action(canvas);

        result = save.getImage();

        windowClose();
    }

    @FXML
    protected void handleExitButtonAction(ActionEvent event) {
        windowClose();

    }

    @FXML
    protected void handleFileExtButtonAction(ActionEvent event) {
        FileExt fileExt = new FileExt();
        fileExt.showOpenDialog(null, canvas);
    }

    @FXML
    protected void handleCameraButtonAction(ActionEvent event) throws IOException {
        Window owner = canvas.getScene().getWindow(); //画面オーナー
        URL fxml = getClass().getResource("Camera.fxml"); //表示するfxml
        final StageUtil cameraStage = new StageUtil(null, owner, fxml, null);

        //カメラ撮り終えた
        cameraStage.getStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                CameraController ctrl = (CameraController) cameraStage.getController();

                if (ctrl.getResult() != null) {
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.drawImage(ctrl.getResult(), 0, 0);
                }
            }
        });

        // 新しいウインドウを表示
        cameraStage.getStage().show();
    }

    @FXML
    protected void handleClearButtonAction(ActionEvent event) {
        PtClear clear = new PtClear(null, null);
        clear.paint(canvas, null, null, null);
    }

    @FXML
    protected void handleExpansionButtonAction(ActionEvent event) {
        if (canvas.getScaleX() < 2.0) {
            canvas.setScaleX(canvas.getScaleX() + 0.1);
            canvas.setScaleY(canvas.getScaleY() + 0.1);
        }
    }

    @FXML
    protected void handleReductionButtonAction(ActionEvent event) {
        if (canvas.getScaleX() > 1.0) {
            canvas.setScaleX(canvas.getScaleX() - 0.1);
            canvas.setScaleY(canvas.getScaleY() - 0.1);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PtClear clear = new PtClear(null, null);
        clear.paint(canvas, null, null, null);

        color.setValue(Color.RED);
    }

    @Override
    public void setParameter(Image param) {
        if (param != null) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(param, 0, 0);
        }
    }

    public Image getResult() {
        return result;
    }

    private void windowClose() {
        stage = (Stage) canvas.getScene().getWindow();
        stage.close();
    }
}
