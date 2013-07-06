package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import jp.ac.aiit.jointry.blocks.BlockMenuItem;

public class BlocksController implements Initializable {

    @FXML
    private AnchorPane blockMenu;
    private MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockMenu.getChildren().addAll(new BlockMenuItem());
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
}
