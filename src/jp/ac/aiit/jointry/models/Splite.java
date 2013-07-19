package jp.ac.aiit.jointry.models;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import jp.ac.aiit.jointry.controllers.BackStageController;
import jp.ac.aiit.jointry.controllers.FrontStageController;
import jp.ac.aiit.jointry.controllers.MainController;

public final class Splite extends ImageView {

    private VBox costumeList = new VBox();
    private BackStageController backStageCtrl;
    private FrontStageController frontStageCtrl;
    private double mouseX, mouseY; //マウス位置 x, y
    private Node dragNode; //ドラッグ範囲をノードで指定

    public Splite(Image image, MainController mainCtrl) {
        super(image);
        backStageCtrl = mainCtrl.getBackStageController();
        frontStageCtrl = mainCtrl.getFrontStageController();

        addCostume(backStageCtrl.createCostume("costume", image));

        setMouseEvent();
        sendActiveSpliteEvent();
    }

    public VBox getCostumeList() {
        return costumeList;
    }

    public void addCostume(Parent costume) {
        if (costume != null) {
            costumeList.getChildren().add(costume);
        }
    }

    private void sendActiveSpliteEvent() {
        backStageCtrl.changeCurrentSplite(this);
        frontStageCtrl.setCurrentSplite(this);
    }

    private void setMouseEvent() {
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sendActiveSpliteEvent();
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
