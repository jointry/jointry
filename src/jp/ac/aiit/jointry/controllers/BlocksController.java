package jp.ac.aiit.jointry.controllers;

import jp.ac.aiit.jointry.models.blocks.statement.procedure.Costume;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.If;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.While;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Rotate;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Move;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.ac.aiit.jointry.models.VariableLabel;
import jp.ac.aiit.jointry.models.blocks.MenuItem;
import jp.ac.aiit.jointry.models.blocks.MenuItemAdv;
import jp.ac.aiit.jointry.models.blocks.expression.Condition;
import jp.ac.aiit.jointry.models.blocks.expression.Rebound;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Assign;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Calculate;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Continue;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Flip;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Sleep;
import jp.ac.aiit.jointry.models.blocks.statement.procedure.Speech;
import jp.ac.aiit.jointry.services.broker.app.BlockDialog;
import static jp.ac.aiit.jointry.services.broker.app.JointryCommon.M_BLOCK_VARIABLE_CREATE;

public class BlocksController implements Initializable {

    @FXML
    private VBox blockMenu;
    @FXML
    private VBox blockMenuAdv;
    @FXML
    private Button createVariable;
    private MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockMenu.getChildren().addAll(
                new MenuItem(Move.class),
                new Separator(),
                new MenuItem(Flip.class),
                new Separator(),
                new MenuItem(Rotate.class),
                new Separator(),
                new MenuItem(Costume.class),
                new Separator(),
                //new MenuItem(Rebound.class),
                //new Separator(),
                new MenuItem(Speech.class),
                new Separator(),
                new MenuItem(Assign.class),
                new Separator(),
                new MenuItem(Calculate.class),
                new Separator(),
                new MenuItem(Continue.class),
                new Separator(),
                new MenuItem(Sleep.class),
                new Separator(),
                new Separator(),
                new Separator(),
                new MenuItem(While.class),
                new Separator(),
                new MenuItem(If.class),
                new Separator());

        blockMenuAdv.getChildren().addAll(
                new Separator(),
                new MenuItem(Rebound.class),
                new Separator(),
                new MenuItem(Condition.class),
                new Separator());
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    protected void createVariable(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateVariable.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        CreateVariableController controller = loader.getController();
        Scene scene = new Scene(root);
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setScene(scene);
        dialog.initOwner(createVariable.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(false);
        dialog.setTitle("へんすうをつくる");
        dialog.showAndWait(); // ダイアログが閉じるまでブロックされる

        switch (controller.getSelectedOption()) {
            case YES:
                String name = controller.getVariableName();
                addVariable(name, true);

                break;
        }
    }

    public void addVariable(String name, boolean sendMessage) {
        for (Node node : blockMenuAdv.getChildrenUnmodifiable()) {
            if (node instanceof MenuItemAdv) {
                VariableLabel variable = ((MenuItemAdv) node).getVariableLabel();
                if (variable.getName().equals(name)) {
                    return; //既にある変数名
                }
            }
        }

        // TODO: あとで考える
        VariableLabel vl = new VariableLabel(name, null);
        // this.mainController.getFrontStageController().addVariable(vl);

        blockMenuAdv.getChildren().add(new Separator());
        MenuItemAdv menu = new MenuItemAdv(name);
        menu.addVariableLabel(vl);
        blockMenuAdv.getChildren().add(menu);

        if (sendMessage) {
            BlockDialog.sendMessage(M_BLOCK_VARIABLE_CREATE, name);
        }
    }
}
