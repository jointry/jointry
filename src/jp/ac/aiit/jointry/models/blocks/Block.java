package jp.ac.aiit.jointry.models.blocks;

import java.util.Map;
import java.util.UUID;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.services.broker.app.BlockDialog;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_CHANGE_STATE;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_REMOVE;

public abstract class Block extends AnchorPane {

    protected double anchorX;
    protected double anchorY;
    public Connector con;
    private String uuid = UUID.randomUUID().toString();
    private Sprite sprite;

    public Block() {
        setLayoutX(0);
        setLayoutY(0);

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.HAND);
            }
        });

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                toFront();
                anchorX = getLayoutX() - mouseEvent.getSceneX();
                anchorY = getLayoutY() - mouseEvent.getSceneY();
                setCursor(Cursor.MOVE);
            }
        });

        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.HAND);
                if (con != null) {
                    con.setFill(Color.TRANSPARENT);
                    setConnector(null);
                }
            }
        });

        setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent t) {
                remove();
                BlockDialog.sendMessage(M_BLOCK_REMOVE, Block.this);
            }
        });
    }

    public void remove() {
        BorderPane root = (BorderPane) getScene().getRoot();
        TabPane centerPane = (TabPane) root.getCenter();
        for (Tab t : centerPane.getTabs()) {
            if ("scriptPane".equals(t.getContent().getId())) {
                AnchorPane ap = (AnchorPane) t.getContent();
                ap.getChildren().remove(this);
            }
        }
        setVisible(false);
    }

    public void move(double dx, double dy) {
        setLayoutX(dx);
        setLayoutY(dy);
    }

    public double fetchAllHeight() {
        return getHeight();
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }
    
    public Sprite getSprite() {
        return sprite;
    }
    
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void initializeLink() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String intern() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map getStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setStatus(Status status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void show(){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Label getLabel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setConnector(Connector con) {
        this.con = con;
    }

    protected void setChangeListener(final TextField field) {
        field.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                if (!t1.equals(t)) {
                    BlockDialog.sendMessage(M_BLOCK_CHANGE_STATE, Block.this);
                }
            }
        });
    }

    protected void setChangeListener(final ComboBox cb) {
        cb.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                if (!t1.equals(t)) {
                    BlockDialog.sendMessage(M_BLOCK_CHANGE_STATE, Block.this);
                }
            }
        });
    }
}
