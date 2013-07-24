package jp.ac.aiit.jointry.models;

import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import jp.ac.aiit.jointry.controllers.MainController;

public final class Sprite extends ImageView {

    private List<Costume> costumes = new ArrayList<>();
    private double mouseX, mouseY; //マウス位置 x, y
    private double pressX, pressY; //スプライトがクリックされた時の位置
    private Node dragNode; //ドラッグ範囲をノードで指定
    private MainController mainController;

    public Sprite(String url, MainController mainController) {
        super(url);

        this.mainController = mainController;
        createCostume(getImage());
        setMouseEvent();
        sendActiveSpriteEvent();
    }

    public void setDragRange(Node node) {
        this.dragNode = node;
    }

    public void addCostume(Costume costume) {
        if (costume != null) {
            costumes.add(costume);
        }
    }

    public void createCostume(Image image) {
        Costume costume = new Costume(getNumber(), "costume", image);
        addCostume(costume);
    }

    public void copyCostume(int number) {
        for (Costume cos : costumes) {
            if (cos.getNumber() == number) {
                Costume costume = new Costume(getNumber(),
                        cos.getTitle() + "のコピー",
                        cos.getImage());
                addCostume(costume);
                break;
            }
        }
    }

    public void updateCostume(int number, Image image) {
        for (Costume cos : costumes) {
            if (cos.getNumber() == number) {
                cos.setImage(image);
                break;
            }
        }
    }

    public int getNumber() {
        return costumes.size() + 1;
    }

    private void sendActiveSpriteEvent() {
        mainController.getFrontStageController().setCurrentSprite(this);
        mainController.getBackStageController().setCurrentSprite(this);
    }

    private void setMouseEvent() {
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sendActiveSpriteEvent();
                mouseX = event.getSceneX() - getTranslateX();
                mouseY = event.getSceneY() - getTranslateY();
                pressX = event.getSceneX() - mouseX;
                pressY = event.getSceneY() - mouseY;

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
                    setTranslateX(pressX);
                    setTranslateY(pressY);
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

    public Iterable<Costume> getCostumes() {
        return costumes;
    }
}
