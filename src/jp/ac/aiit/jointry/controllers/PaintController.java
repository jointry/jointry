/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.controllers;

import java.awt.Point;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jp.ac.aiit.jointry.paint.FileExt;
import jp.ac.aiit.jointry.paint.PtClear;
import jp.ac.aiit.jointry.paint.Save;

/**
 *
 * @author kanemoto
 */
public class PaintController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private ColorPicker color;
    @FXML
    private PaintOptionController paintOptionController;
    private FrontStageController frontStageController;
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

        frontStageController.setSprite(save.getImage());

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
    protected void handleClearButtonAction(ActionEvent event) {
        PtClear clear = new PtClear(null, null);
        clear.paint(canvas, null, null, null);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PtClear clear = new PtClear(null, null);
        clear.paint(canvas, null, null, null);
        color.setValue(Color.RED);
    }

    private void windowClose() {
        stage = (Stage) canvas.getScene().getWindow();
        stage.close();
    }

    public void setController(FrontStageController ctrl) {
        this.frontStageController = ctrl;
    }
}
