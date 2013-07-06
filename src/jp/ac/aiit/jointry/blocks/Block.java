package jp.ac.aiit.jointry.blocks;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Block extends AnchorPane {

    private double anchorX;
    private double anchorY;
    private Block me;
    private Block prevBlock;
    private Block nextBlock;
    private TextField tf;

    public Block(double x, double y, Color color) {
        setLayoutX(x);
        setLayoutY(y);

        Rectangle rect = new Rectangle();
        rect.setWidth(250);
        rect.setHeight(60);
        rect.setFill(color);

        tf = new TextField();
        tf.setMaxWidth(50.0);
        Label label = new Label("度回転する");

        AnchorPane.setTopAnchor(rect, 0.0);
        AnchorPane.setTopAnchor(tf, 10.0);
        AnchorPane.setLeftAnchor(tf, 80.0);
        AnchorPane.setTopAnchor(label, 10.0);
        AnchorPane.setLeftAnchor(label, 150.0);

        getChildren().addAll(rect, tf, label);

        me = this;
        setStyle("-fx-background-color: green;");

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
            }
        });

        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // Initialize
                me.initializeLink();

                // Move
                double dx = mouseEvent.getSceneX() + anchorX;
                double dy = mouseEvent.getSceneY() + anchorY;
                me.move(dx, dy);

                // Check collision
                Block target = getCollision();
                if (target != null && target != nextBlock) {
                    target.setLink(target, me);
                    me.move(target.getLayoutX(),
                            target.getLayoutY() + me.getHeight());
                }
            }
        });

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.HAND);
            }
        });
    }

    private Block getCollision() {
        Block block = null;
        for (Node node : getParent().getChildrenUnmodifiable()) {
            if (node == this) {
                continue;
            }
            if (!(node instanceof Block)) {
                continue;
            }

            Block b = (Block) node;
            if (b.getBoundsInParent().intersects(this.getBoundsInParent())) {
                block = b;
            }
        }
        return block;
    }

    private void setLink(Block prev, Block next) {
        prev.nextBlock = next;
        next.prevBlock = prev;
    }

    private void move(double dx, double dy) {
        setLayoutX(dx);
        setLayoutY(dy);
        if (nextBlock != null) {
            nextBlock.move(dx, dy + getHeight());
        }
    }

    private void initializeLink() {
        if (prevBlock != null) {
            prevBlock.nextBlock = null;
            prevBlock = null;
        }
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("rotate");
        String arg = tf.getText();
        if (arg == null) {
            arg = "0";
        }
        sb.append(" " + arg + "\n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }

    public boolean existPrevBlock() {
        return prevBlock != null;
    }
}
