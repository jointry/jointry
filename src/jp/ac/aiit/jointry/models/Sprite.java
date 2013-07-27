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
    private int costumeNumber;

    public Sprite(String url, MainController mainController) {
        super(url);
        initialize(mainController);
    }

    public Sprite(Image image, MainController mainController) {
        super(image);
        initialize(mainController);
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
        Costume costume = new Costume(getNewNumber(), "costume", image);
        addCostume(costume);
    }

    public void copyCostume(int number) {
        for (Costume cos : costumes) {
            if (cos.getNumber() == number) {
                Costume costume = new Costume(getNewNumber(),
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
                setCostume(cos);
                break;
            }
        }
    }

    public int getNewNumber() {
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

    private void initialize(MainController mainController) {
        this.mainController = mainController;
        this.costumeNumber = 1;
        createCostume(getImage());
        setMouseEvent();
        sendActiveSpriteEvent();
    }

    public Iterable<Costume> getCostumes() {
        return costumes;
    }

    public void changeCostume(int number) {
        for (Costume cos : costumes) {
            if (cos.getNumber() == number) {
                setCostume(cos);
                break;
            }
        }
    }

    public void changeNextCostume() {
        int nextNumber = this.costumeNumber + 1;
        if (nextNumber > costumes.size()) {
            nextNumber = 1; // initialize
        }
        setCostume(costumes.get(nextNumber - 1));
    }

    public void setCostume(Costume costume) {
        this.setImage(costume.getImage());
        this.costumeNumber = costume.getNumber();
    }
}
