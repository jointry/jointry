package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.statics.TestData;

public class FrontStageController implements Initializable {

    @FXML
    private AnchorPane stage;
    @FXML
    private ImageView background;
    private ImageView currentSprite;
    private MainController mainController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //まだ初期化されていないので
        URL path = getClass().getResource("images/scratch_cat.png");
        Image image = new Image(path.toString());

        ImageView sprite = new DraggableImage(0, 0, image, stage);
        stage.getChildren().add(sprite);

        currentSprite = sprite;
    }

    @FXML
    protected void handleExecuteBtnAct(ActionEvent event) {
        this.mainController.getBackStageController().execute();
    }

    @FXML
    protected void handlePaintBtnAct(ActionEvent event) throws Exception {
        //Paintツール画面
        Stage paintStage = new Stage(StageStyle.TRANSPARENT);

        //オーナー設定
        paintStage.initModality(Modality.APPLICATION_MODAL);
        paintStage.initOwner((Stage) stage.getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("Paint.fxml"));
        Parent root = (Parent) fxmlLoader.load();

        //ペイント書き終えた
        paintStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                TestData<Image> data = new TestData();
                if (data.get("paintImage") != null) {
                    setSprite(data.get("paintImage"));
                }
            }
        });

        // 新しいウインドウを表示
        paintStage.setScene(new Scene(root));
        paintStage.show();
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public ImageView getBackground() {
        return background;
    }

    public ImageView getSprite() {
        return currentSprite;
    }

    private void setSprite(Image image) {
        if (image != null) {
            ImageView sprite = new DraggableImage(0, 0, image, stage);
            stage.getChildren().add(sprite);

            setCurrentSprite(sprite);
        }
    }

    private void setCurrentSprite(ImageView sprite) {
        currentSprite = sprite;
        mainController.getBackStageController().setCurrentCostume(currentSprite);
    }

    private class DraggableImage extends ImageView {

        /**
         * マウス位置(x,y)
         */
        private double mouseX, mouseY;
        /**
         * ドラッグ範囲をノードで指定
         */
        private Node dragNode;

        public DraggableImage(int x, int y, Image img, Node dragNode) {
            super(img);
            setTranslateX(x);
            setTranslateY(y);

            this.dragNode = dragNode;

            setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    FrontStageController.this.setCurrentSprite(DraggableImage.this);

                    mouseX = event.getSceneX() - getTranslateX();
                    mouseY = event.getSceneY() - getTranslateY();

                    //ドラッグ中のエフェクト効果
                    InnerShadow is = new InnerShadow();
                    is.setOffsetX(4.0f);
                    is.setOffsetY(4.0f);
                    setEffect(is);

                    //イベントストップ
                    event.consume();
                }
            });

            setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    setTranslateX(event.getSceneX() - mouseX);
                    setTranslateY(event.getSceneY() - mouseY);

                    event.consume();
                }
            });

            setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (!dragRange(event.getSceneX(), event.getSceneY())) {
                        setX(mouseX);
                        setY(mouseY);

                    }

                    setEffect(null);
                    event.consume();
                }
            });
        }

        private boolean dragRange(double sceneX, double sceneY) {
            if (dragNode == null) {
                return true;
            }
            return dragNode.getLayoutBounds().contains(
                    dragNode.sceneToLocal(sceneX, sceneY));
        }
    }
}
