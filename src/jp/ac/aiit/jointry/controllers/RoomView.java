package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import jp.ac.aiit.jointry.JointryMain;
import jp.ac.aiit.jointry.models.Room;

/**
 * http://iconhoihoi.oops.jp/ 素材はこちらからお借りしました.
 */
public class RoomView extends AnchorPane {

    private Label userName;
    private Room room;

    public RoomView(int roomId, Room room) {
        this.room = room;

        this.setPrefWidth(260.0);
        this.setPrefHeight(40.0);

        userName = new Label(room.getName());
        userName.setAlignment(Pos.CENTER);
        userName.setLayoutX(5.0);
        userName.setLayoutY(2.0);
        userName.prefWidth(38.0);
        userName.prefHeight(22.0);
        userName.setFont(new Font(36.0));
        userName.setTextFill(Color.WHITE);

        ImageView imageview = new ImageView(getClass().getResource("images/room.png").toString());
        imageview.setFitWidth(258.0);
        imageview.setFitHeight(38.0);
        imageview.setLayoutX(1.0);
        imageview.setLayoutY(1.0);
        this.getChildren().addAll(imageview, userName);
    }

    public void setRoom(int roomId, Room room) {
        this.room = room;
        this.userName.setText(room.getName());
    }

    public Room getRoom() {
        return room;
    }
}
