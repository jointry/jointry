package jp.ac.aiit.jointry.services.picture.paint.ctrl;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jp.ac.aiit.jointry.services.picture.paint.PaintApplication;
import jp.ac.aiit.jointry.services.picture.paint.model.FileManager;
import jp.ac.aiit.jointry.services.picture.paint.util.ImageUtil;
import jp.ac.aiit.jointry.services.picture.paint.views.PaintTool;

public class PaintController implements Initializable, ChangeListener<Image> {

    @FXML
    protected ScrollPane optionCardDeck;
    @FXML
    private PaintTool ptool;
    @FXML
    private Canvas canvas;
    private Point2D startPoint;
    private Point2D endPoint;
    private Image result;

    @FXML
    protected void selectTool(MouseEvent event) {
        ptool = (PaintTool) event.getTarget();
        setPaintTool(ptool);
    }

    @FXML
    protected void draw(MouseEvent event) {
        startPoint = new Point2D(event.getX(), event.getY());
        endPoint = startPoint;
        ptool.paint(canvas, startPoint, endPoint);
    }

    @FXML
    protected void drawing(MouseEvent event) {
        startPoint = endPoint;
        endPoint = new Point2D(event.getX(), event.getY());
        ptool.paint(canvas, startPoint, endPoint);
    }

    @FXML
    protected void load(ActionEvent event) {
        Image loadImage = FileManager.load("読込");
        if (loadImage != null) {
            canvas.getGraphicsContext2D().drawImage(loadImage, 0, 0);
        }
    }

    @FXML
    protected void save(ActionEvent event) {
        FileManager.save("保存", ImageUtil.justResize(canvas));
    }

    @FXML
    protected void clear(ActionEvent event) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.TRANSPARENT);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    protected void complete(ActionEvent event) {
        result = ImageUtil.justResize(canvas);
        windowClose();
    }

    @FXML
    protected void cancel(ActionEvent event) {
        windowClose();
    }

    @Override
    public void changed(ObservableValue<? extends Image> ov, Image oldValue, Image newValue) {
        //clear(null); //クリアはしない

        //描画内容が本コントローラー以外から更新された場合
        canvas.getGraphicsContext2D().drawImage(newValue, 0, 0);
    }

    private void setPaintTool(PaintTool ptool) {
        optionCardDeck.setContent((Node) ptool.getOptionCard());
        PaintApplication.getModel().setPtool(ptool);
        PaintApplication.getModel().addImageListener(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setPaintTool(ptool);
    }

    public void setInitImage(Image image) {
        if (image != null) {
            canvas.getGraphicsContext2D().drawImage(image, 0, 0);
        }
    }

    public Image getCompleteImage() {
        return result;
    }

    private void windowClose() {
        Stage stage = (Stage) canvas.getScene().getWindow();
        stage.close();
    }
}
