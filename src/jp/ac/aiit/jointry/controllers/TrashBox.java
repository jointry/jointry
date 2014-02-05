package jp.ac.aiit.jointry.controllers;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.Pane;
import jp.ac.aiit.jointry.models.blocks.Block;

public class TrashBox extends ImageView {

    public TrashBox() {
        this.setImage(new Image(getClass().getResource("images/TrashBox.png").toString()));

        this.setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent e) {
                Lighting lighting = new Lighting();
                TrashBox.this.setEffect(lighting);
            }
        });
        this.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent e) {
                TrashBox.this.setEffect(null);
            }
        });
        this.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent e) {
                Node delNode = (Node) e.getGestureSource();

                if (delNode instanceof Block) {
                    //ブロックの場合は削除方法が異なる
                    ((Block) delNode).remove();
                } else {
                    Pane parentPane = (Pane) delNode.getParent();
                    parentPane.getChildren().remove(delNode); //直近の親から子を削除                  
                }
            }
        });
    }
}
