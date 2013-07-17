package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import jp.ac.aiit.jointry.models.blocks.MenuItem;
import jp.ac.aiit.jointry.models.blocks.Move;
import jp.ac.aiit.jointry.models.blocks.Rotate;
import jp.ac.aiit.jointry.models.blocks.While;

public class BlocksController implements Initializable {

    @FXML
    private VBox blockMenu;
    private MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockMenu.getChildren().addAll(
                new MenuItem(Move.class),
                new Separator(),
                new MenuItem(Rotate.class),
                new Separator(),
                new MenuItem(While.class));
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
}
