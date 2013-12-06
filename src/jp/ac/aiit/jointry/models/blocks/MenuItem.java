package jp.ac.aiit.jointry.models.blocks;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jp.ac.aiit.jointry.services.broker.app.BlockDialog;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_CREATE;

public class MenuItem extends AnchorPane {

    public MenuItem(final Class blockClass) {
        Rectangle rect = new Rectangle();
        rect.setWidth(140);
        rect.setHeight(30);
        rect.setArcWidth(15);
        rect.setArcHeight(15);
        rect.setStroke(Color.GRAY);

        Block block = null;
        try {
            // TODO: Node#getStyleClassでObservableListにクラスaddすれば、
            // sytle.cssで色を管理できる……と思う。
            rect.setFill((Color) blockClass.getMethod("getColor").invoke(null));
            block = (Block) blockClass.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(MenuItem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MenuItem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MenuItem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MenuItem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(MenuItem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MenuItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        Label label = block.getLabel();

        AnchorPane.setTopAnchor(rect, 0.0);
        AnchorPane.setTopAnchor(label, 10.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        getChildren().addAll(rect, label);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    final Block block = (Block) blockClass.newInstance();
                    addToScriptPane(block);
                    BlockDialog.sendMessage(M_BLOCK_CREATE, block);
                    setCursor(Cursor.MOVE);
                } catch (InstantiationException ex) {
                    Logger.getLogger(MenuItem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(MenuItem.class.getName()).log(Level.SEVERE, null, ex);
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

    private void addToScriptPane(Block block) {
        BorderPane root = (BorderPane) getScene().getRoot();
        TabPane centerPane = (TabPane) root.getCenter();
        for (Tab t : centerPane.getTabs()) {
            if ("scriptPane".equals(t.getContent().getId())) {
                AnchorPane ap = (AnchorPane) t.getContent();
                ap.getChildren().add(block);
            }
        }
    }
}
