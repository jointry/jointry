package jp.ac.aiit.jointry.controllers;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class ResizePane extends ScrollPane {

    private Pane content;
    private double contentHeight = 0;
    private double contentWidth = 0;

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

    public void resizeContent() {
        for (Node node : content.getChildrenUnmodifiable()) {
            Bounds bounds = node.getLayoutBounds();

            double width = node.getLayoutX() + bounds.getWidth();
            if (getContentWidth() < width) {
                setContentWidth(width);
            }

            double height = node.getLayoutY() + bounds.getHeight();
            if (getContentHeight() < height) {
                setContentHeight(height);
            }
        }

        content.setPrefWidth(getContentWidth());
        content.setPrefHeight(getContentHeight());

        setHvalue(getContentWidth());
        setVvalue(getContentHeight());
    }

    /**
     * @return the contentHeight
     */
    public double getContentHeight() {
        return contentHeight;
    }

    public void setContentHeight(double h) {
        this.contentHeight = h;
    }

    public double getContentWidth() {
        return contentWidth;
    }

    public void setContentWidth(double x) {
        this.contentWidth = x;
    }
}
