package jp.ac.aiit.jointry.controllers;

import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.paint.FileExt;
import jp.ac.aiit.jointry.paint.PtClear;
import jp.ac.aiit.jointry.paint.Save;
import jp.ac.aiit.jointry.statics.TestData;

public class PaintController implements Initializable {

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

        TestData data = new TestData();
        data.put("paintImage", save.getImage());

        windowClose();
    }

    @FXML
    protected void handleExitButtonAction(ActionEvent event) {
        TestData data = new TestData();
        data.put("paintImage", null);

        windowClose();

    }

    @FXML
    protected void handleFileExtButtonAction(ActionEvent event) {
        FileExt fileExt = new FileExt();
        fileExt.showOpenDialog(null, canvas);
    }

    @FXML
    protected void handleCameraButtonAction(ActionEvent event) throws IOException {
        //Paintツール画面
        Stage camStage = new Stage();

        //オーナー設定
        camStage.initModality(Modality.APPLICATION_MODAL);
        camStage.initOwner((Stage) canvas.getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("Camera.fxml"));
        Parent root = (Parent) fxmlLoader.load();

        //カメラ撮り終えた
        camStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                TestData<Image> data = new TestData();
                if (data.get("cameraImage") != null) {
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.drawImage(data.get("cameraImage"), 0, 0);
                }
            }
        });

        // 新しいウインドウを表示
        camStage.setScene(new Scene(root));
        camStage.show();
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

        TestData<Image> data = new TestData();

        if (data.get("editImage") != null) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(data.get("editImage"), 0, 0);
        }

        color.setValue(Color.RED);
    }

    private void windowClose() {
        stage = (Stage) canvas.getScene().getWindow();
        stage.close();
    }
}
