package jp.ac.aiit.jointry.blocks;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BlockMenuItem extends AnchorPane {

    public BlockMenuItem(final Class blockClass) {
        Rectangle rect = new Rectangle();
        rect.setWidth(100);
        rect.setHeight(30);
        rect.setFill(Color.AQUA);
        Block block = null;
        try {
            block = (Block) blockClass.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(BlockMenuItem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BlockMenuItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        Label lb = block.getLabel();

        AnchorPane.setTopAnchor(rect, 0.0);
        AnchorPane.setTopAnchor(lb, 10.0);
        AnchorPane.setLeftAnchor(lb, 0.0);
        getChildren().addAll(rect, lb);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    final Block block = (Block) blockClass.newInstance();
                    addToScriptPane(block);
                    setCursor(Cursor.MOVE);
                } catch (InstantiationException ex) {
                    Logger.getLogger(BlockMenuItem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(BlockMenuItem.class.getName()).log(Level.SEVERE, null, ex);
                }
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
            }
        });

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.HAND);
            }
        });

    }

    private void addToScriptPane(Node node) {
        BorderPane root = (BorderPane) getScene().getRoot();
        TabPane centerPane = (TabPane) root.getCenter();
        for (Tab t : centerPane.getTabs()) {
            if ("scriptPane".equals(t.getContent().getId())) {
                AnchorPane ap = (AnchorPane) t.getContent();
                ap.getChildren().add(node);
            }
        }
    }
}
