package jp.ac.aiit.jointry.blocks;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BlockMenuItem extends Rectangle implements Serializable {

    private double anchorX;
    private double anchorY;
    private BlockMenuItem me;

    public BlockMenuItem() {
        this.me = this;
        this.setWidth(100);
        this.setHeight(30);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                anchorX = getLayoutX() - mouseEvent.getSceneX();
                anchorY = getLayoutY() - mouseEvent.getSceneY();

                Block b1 = new Block(0, 0, Color.RED);
                addToScriptPane(b1);
                setCursor(Cursor.MOVE);
            }
        });

        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.HAND);
            }
        });

        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double dx = mouseEvent.getSceneX() + anchorX;
                double dy = mouseEvent.getSceneY() + anchorY;
                me.move(dx, dy);
            }
        });

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.HAND);
            }
        });
    }

    private void move(double dx, double dy) {
        setLayoutX(dx);
        setLayoutY(dy);
    }

    private void addToScriptPane(Node node) {
        BorderPane root = (BorderPane) getScene().getRoot();
        TabPane centerPane = (TabPane) root.getCenter();
        for (Tab t : centerPane.getTabs()) {
            if (t.getContent().getId().equals("scriptPane")) {
                AnchorPane ap = (AnchorPane) t.getContent();
                ap.getChildren().add(node);
            }
        }
    }
}
