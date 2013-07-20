package jp.ac.aiit.jointry.models;

import javafx.event.EventHandler;
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

public final class Sprite extends ImageView {

    private VBox costumeList = new VBox();
    private BackStageController backStageCtrl;
    private FrontStageController frontStageCtrl;
    private double mouseX, mouseY; //マウス位置 x, y
    private Node dragNode; //ドラッグ範囲をノードで指定

    public Sprite(Image image, MainController mainCtrl) {
        super(image);
        backStageCtrl = mainCtrl.getBackStageController();
        frontStageCtrl = mainCtrl.getFrontStageController();

        addCostume(backStageCtrl.createCostume(getNumber(), "costume", image));

        setMouseEvent();
        sendActiveSpriteEvent();
    }

    public VBox getCostumeList() {
        return costumeList;
    }

    public void addCostume(Parent costume) {
        if (costume != null) {
            costumeList.getChildren().add(costume);
        }
    }

    public void copyCostume(String title, Image image) {
        addCostume(backStageCtrl.createCostume(getNumber(), title, image));
    }

    public int getNumber() {
        return costumeList.getChildren().size() + 1;
    }

    private void sendActiveSpriteEvent() {
        backStageCtrl.changeCurrentSprite(this);
        frontStageCtrl.setCurrentSprite(this);
    }

    private void setMouseEvent() {
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sendActiveSpriteEvent();
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
