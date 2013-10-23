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
import jp.ac.aiit.jointry.models.blocks.expression.Variable;

public class MenuItemAdv extends AnchorPane {

    public MenuItemAdv(final String variableName) {
        Rectangle rect = new Rectangle();
        rect.setWidth(140);
        rect.setHeight(30);
        rect.setArcWidth(15);
        rect.setArcHeight(15);
        rect.setStroke(Color.GRAY);
        rect.setFill(Variable.getColor());
        Label label = new Label(variableName);
        AnchorPane.setTopAnchor(rect, 0.0);
        AnchorPane.setTopAnchor(label, 10.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        getChildren().addAll(rect, label);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Variable v = new Variable();
                v.setName(variableName);
                addToScriptPane(v);
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
            }
        });

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.HAND);
            }
        });

    }

    private void addToScriptPane(Variable v) {
        BorderPane root = (BorderPane) getScene().getRoot();
        TabPane centerPane = (TabPane) root.getCenter();
        for (Tab t : centerPane.getTabs()) {
            if ("scriptPane".equals(t.getContent().getId())) {
                AnchorPane ap = (AnchorPane) t.getContent();
                ap.getChildren().add(v);
            }
        }
    }
}
