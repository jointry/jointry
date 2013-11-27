package jp.ac.aiit.jointry.models;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.controllers.MainController;
import broker.core.DInfo;
import jp.ac.aiit.jointry.services.broker.app.JointryCommon;
import jp.ac.aiit.jointry.services.broker.app.SpriteDialog;

public final class Sprite extends HBox implements JointryCommon {

    private String name = "";
    private List<Costume> costumes = new ArrayList<>();
    private AnchorPane scriptPane;
    private double mouseX, mouseY; //マウス位置 x, y
    private double pressX, pressY; //スプライトがクリックされた時の位置
    private Node dragRange; //ドラッグ範囲をノードで指定
    private MainController mainController;
    private int currentCostumeNumber = 1;
    private Integer direction = 1;
    private ImageView icon;
    private Label saying;

    public Sprite(MainController mainController) {
        //値初期化
        this.mainController = mainController;
        this.scriptPane = new AnchorPane();
        scriptPane.setId("scriptPane");

        icon = new ImageView();
        getChildren().add(this.icon);

        //リスナー設定
        setMouseEvent();
        SpriteDialog.putSprite(this);
    }

    public Sprite(String url, MainController mainController) {
        this(new Image(url), mainController);
    }

    public Sprite(Image image, MainController mainController) {
        this(mainController);

        icon = new ImageView(image);
        getChildren().add(this.icon);
        addCostume("costume", icon.getImage());
    }

    public Costume addCostume(String name, Image image) {
        Costume costume = new Costume(getNewNumber(), name, image);
        costumes.add(costume);

        return costume;
    }

    public void updateCostume(int number, Image image) {
        for (Costume cos : costumes) {
            if (cos.getNumber() == number) {
                cos.setImage(image);
                setSpriteCostume(cos);
                break;
            }
        }
    }

    public void setSpriteCostume(int number) {
        for (Costume cos : costumes) {
            if (cos.getNumber() == number) {
                setSpriteCostume(cos);
                break;
            }
        }
    }

    public void setSpriteCostume(Costume costume) {
        this.currentCostumeNumber = costume.getNumber();
        icon.setImage(costume.getImage());
    }

    public int getCostumeNumber() {
        return currentCostumeNumber;
    }

    public Iterable<Costume> getCostumes() {
        return costumes;
    }

    private int getNewNumber() {
        return costumes.size() + 1;
    }

    public AnchorPane getScriptPane() {
        return scriptPane;
    }

    public double moveBy(double x) {
        return x * this.direction;
    }

    public void setSpeechBubble(final String say) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clearSpeechBubble();
                saying = new Label(say);
                saying.getStyleClass().add("speech-bubble");
                getChildren().add(saying);
            }
        });
    }

    public void clearSpeechBubble() {
        if (saying != null) {
            saying.setVisible(false);
            getChildren().removeAll(saying);
        }
    }

    public double getX() {
        return icon.getX();
    }

    public double getY() {
        return icon.getY();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDragRange(Node node) {
        this.dragRange = node;
    }

    private void setMouseEvent() {
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mainController.getFrontStageController().setCurrentSprite(Sprite.this);
                mouseX = event.getSceneX() - getTranslateX();
                mouseY = event.getSceneY() - getTranslateY();
                pressX = event.getSceneX() - mouseX;
                pressY = event.getSceneY() - mouseY;

                //ドラッグ中のエフェクト効果
                if (mainController.getAgent() != null) {
                    DInfo dinfo = new DInfo(D_SPRITE);
                    dinfo.set(KC_SPRITE_NAME, name);
                    dinfo.set(KC_METHOD, VM_SELECT_SPRITE);
                    dinfo.set(KC_COLOR, Color.RED.toString());

                    mainController.getAgent().sendNotify(dinfo);
                } else {
                    InnerShadow is = new InnerShadow();
                    is.setOffsetX(4.0f);
                    is.setOffsetY(4.0f);
                    setEffect(is);
                }

                event.consume(); //イベントストップ
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
                if (!isInsideDragRange(event.getSceneX(), event.getSceneY())) {
                    setTranslateX(pressX);
                    setTranslateY(pressY);
                }

                if (mainController.getAgent() != null) {
                    DInfo dinfo = new DInfo(D_SPRITE);
                    dinfo.set(KC_METHOD, VM_MOVE_SPRITE);
                    dinfo.set(KC_SPRITE_NAME, name);
                    dinfo.set(KC_X1, (int) (event.getSceneX() - mouseX));
                    dinfo.set(KC_Y1, (int) (event.getSceneY() - mouseY));
                    dinfo.set(KC_COLOR, Color.ALICEBLUE.toString());

                    mainController.getAgent().sendNotify(dinfo);
                }

                setEffect(null);
                event.consume();
            }
        });
    }

    private boolean isInsideDragRange(double sceneX, double sceneY) {
        if (dragRange == null) {
            return true;
        }
        return dragRange.getLayoutBounds().contains(
                dragRange.sceneToLocal(sceneX, sceneY));
    }

    public MainController getMainController() {
        return mainController;
    }
}
