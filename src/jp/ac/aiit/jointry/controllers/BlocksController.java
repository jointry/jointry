package jp.ac.aiit.jointry.controllers;

import jp.ac.aiit.jointry.models.blocks.procedure.statement.Costume;
import jp.ac.aiit.jointry.models.blocks.arithmetic.condition.Condition;
import jp.ac.aiit.jointry.models.blocks.procedure.codeblock.If;
import jp.ac.aiit.jointry.models.blocks.procedure.codeblock.While;
import jp.ac.aiit.jointry.models.blocks.procedure.statement.Rotate;
import jp.ac.aiit.jointry.models.blocks.procedure.statement.Rebound;
import jp.ac.aiit.jointry.models.blocks.procedure.statement.Move;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import jp.ac.aiit.jointry.models.blocks.*;
import jp.ac.aiit.jointry.models.blocks.arithmetic.condition.Eq;

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
                new MenuItem(While.class),
                new Separator(),
                new MenuItem(If.class),
                new Separator(),
                new MenuItem(Costume.class),
                new Separator(),
                new MenuItem(Rebound.class),
                new Separator(),
                new MenuItem(Eq.class));
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
}
