package jp.ac.aiit.jointry.controllers;

import jp.ac.aiit.jointry.models.blocks.procedure.statement.Costume;
import jp.ac.aiit.jointry.models.blocks.procedure.codeblock.If;
import jp.ac.aiit.jointry.models.blocks.procedure.codeblock.While;
import jp.ac.aiit.jointry.models.blocks.procedure.statement.Rotate;
import jp.ac.aiit.jointry.models.blocks.procedure.statement.Rebound;
import jp.ac.aiit.jointry.models.blocks.procedure.statement.Move;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.ac.aiit.jointry.models.blocks.*;
import jp.ac.aiit.jointry.models.blocks.arithmetic.Variable;
import jp.ac.aiit.jointry.models.blocks.arithmetic.calculate.Calculate;
import jp.ac.aiit.jointry.models.blocks.arithmetic.condition.Eq;
import jp.ac.aiit.jointry.models.blocks.procedure.statement.Speech;

public class BlocksController implements Initializable {

    @FXML
    private VBox blockMenu;
    @FXML
    private Button createVariable;
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
                new MenuItem(Eq.class),
                new Separator(),
                new MenuItem(Speech.class),
                new Separator(),
                new MenuItem(Calculate.class));
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
                Variable v = new Variable(controller.getVariableName());
                this.mainController.getBackStageController().addBlock(v);
                break;
        }
    }
}
