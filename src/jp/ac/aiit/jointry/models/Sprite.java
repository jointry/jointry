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
import jp.ac.aiit.jointry.controllers.MainController;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_SPRITE_SELECT;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_SPRITE_DRAGGED;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_SPRITE_RELEASD;
import jp.ac.aiit.jointry.services.broker.app.SpriteDialog;

public final class Sprite extends HBox {

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

    public Sprite() {
        //値初期化
        this.scriptPane = new AnchorPane();
        scriptPane.setId("scriptPane");

        icon = new ImageView();
        getChildren().add(this.icon);

        setMouseEvent();
    }

    public Sprite(String url) {
        this(new Image(url));
    }

    public Sprite(Image image) {
        this();
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

    /**
     * 該当コスチュームを削除する.
     */
    public void deleteCostume(int number) {
        if (costumes.size() <= 1) {
            return; //コスチュームは1つ以上必要
        }

        for (Costume cos : costumes) {
            if (cos.getNumber() == number) {
                costumes.remove(cos);
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

    public void clearCostume() {
        costumes.clear();
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

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setSpeechBubble(final String say) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clearSpeechBubble();
                if (say != null && say.length() > 0) {
                    saying = new Label(say);
                    saying.getStyleClass().add("speech-bubble");
                    getChildren().add(saying);
                }
            }
        });
    }

    public void clearSpeechBubble() {
        if (saying != null) {
            saying.setVisible(false);
            getChildren().removeAll(saying);
            saying = null;
        }
    }

    public String getSpeech() {
        if (saying != null) {
            return saying.getText();
        }

        return null;
    }

    public double getX() {
        return icon.getX();
    }

    public double getY() {
        return icon.getY();
    }

    public Image getIcon() {
        return icon.getImage();
    }

    public void setIcon(Image image) {
        this.icon.setImage(image);
        addCostume("costume", icon.getImage());
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
                getMainController().getFrontStageController().setCurrentSprite(Sprite.this);
                mouseX = event.getSceneX() - getTranslateX();
                mouseY = event.getSceneY() - getTranslateY();
                pressX = event.getSceneX() - mouseX;
                pressY = event.getSceneY() - mouseY;

                //ドラッグ中のエフェクト効果
                InnerShadow is = new InnerShadow();
                is.setOffsetX(4.0f);
                is.setOffsetY(4.0f);
                setEffect(is);

                event.consume(); //イベントストップ

                SpriteDialog.sendSimpleMessage(M_SPRITE_SELECT, Sprite.this);
            }
        });

        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setTranslateX(event.getSceneX() - mouseX);
                setTranslateY(event.getSceneY() - mouseY);

                event.consume();

                SpriteDialog.sendSimpleMessage(M_SPRITE_DRAGGED, Sprite.this);
            }
        });

        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!isInsideDragRange(event.getSceneX(), event.getSceneY())) {
                    setTranslateX(pressX);
                    setTranslateY(pressY);
                }

                setEffect(null);
                event.consume();

                SpriteDialog.sendSimpleMessage(M_SPRITE_RELEASD, Sprite.this);
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

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
