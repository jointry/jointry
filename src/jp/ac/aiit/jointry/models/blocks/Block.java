package jp.ac.aiit.jointry.models.blocks;

import java.util.List;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public abstract class Block extends AnchorPane {

    protected double anchorX;
    protected double anchorY;
    public Connector con;

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

    public String intern() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void blockIntern(List codeList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Label getLabel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setConnector(Connector con) {
        this.con = con;
    }
}
