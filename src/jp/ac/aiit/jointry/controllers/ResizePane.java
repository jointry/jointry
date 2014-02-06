package jp.ac.aiit.jointry.controllers;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class ResizePane extends ScrollPane {

    private Pane content;

    public void setContent(Pane content) {
        this.content = content;

        content.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizeContent();
            }
        });

        this.setContent((Node) content);
    }

    private void resizeContent() {
        double maxWidth = 0;
        double maxHeight = 0;

        for (Node node : content.getChildrenUnmodifiable()) {
            Bounds bounds = node.getLayoutBounds();

            double width = node.getLayoutX() + bounds.getWidth();
            if (maxWidth < width) {
                maxWidth = width;
            }

            double height = node.getLayoutY() + bounds.getHeight();
            if (maxHeight < height) {
                maxHeight = height;
            }
        }

        content.setPrefWidth(maxWidth);
        content.setPrefHeight(maxHeight);
    }
}
