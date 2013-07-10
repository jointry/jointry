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
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jp.ac.aiit.jointry.paint.IPaint;

/**
 *
 * @author kanemoto
 */
public class PaintController implements Initializable, IPaint {

    @FXML
    private Canvas canvas;
    @FXML
    private ColorPicker color;
    @FXML
    private PaintOptionController paintOptionController;
    private Stage stage;
    private Point window = new Point();
    private Point pS = new Point();
    private Point pE = new Point();
    private FrontStageController frontStageController;

    //画面移動
    @FXML
    protected void handleWindowMovePressed(MouseEvent event) {
        stage = (Stage) canvas.getScene().getWindow();
        Scene scene = stage.getScene();
        window.x = (int) (event.getSceneX() + scene.getX());
        window.y = (int) (event.getSceneY() + scene.getY());
    }

    @FXML
    protected void handleWindowMoveDragged(MouseEvent event) {
        stage.setX(event.getScreenX() - window.x);
        stage.setY(event.getScreenY() - window.y);
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
        System.out.println("Save the Dummy");
        //単純な透過処理
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        frontStageController.setSprite(canvas.snapshot(params, null));

        stage = (Stage) canvas.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void handleExitButtonAction(ActionEvent event) {
        stage = (Stage) canvas.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void handleFileExtButtonAction(ActionEvent event) {
        fileExt.showOpenDialog(null);
        fileExt.paint(canvas, null, null, null);
    }

    @FXML
    protected void handleClearButtonAction(ActionEvent event) {
        clear.paint(canvas, null, null, null);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clear.paint(canvas, null, null, null);
        color.setValue(Color.RED);
    }

    public void setController(FrontStageController ctrl) {
        this.frontStageController = ctrl;
    }
}
