package jp.ac.aiit.jointry.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import jp.ac.aiit.jointry.models.Room;

/**
 * http://iconhoihoi.oops.jp/ 素材はこちらからお借りしました.
 */
public class RoomController {

    @FXML
    private Label userName;
    @FXML
    private Label id;
    @FXML
    private AnchorPane bg;
    private Room room;

    public void setRoom(int roomId, Room room) {
        this.room = room;
        this.userName.setText(room.getName());
        this.id.setText("" + roomId);
    }

    public Room getRoom() {
        return room;
    }
    
    public AnchorPane getBG() {
        return bg;
    }
}
