package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Costume;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.util.ParameterAware;
import jp.ac.aiit.jointry.util.StageUtil;

public class CostumeCntroller implements Initializable, ParameterAware<Costume> {

    @FXML
    private ImageView image;
    @FXML
    private TextField title;
    @FXML
    private Label number;
    private MainController mainController;

    @FXML
    protected void handleImageSelected(MouseEvent event) {
        mainController.getFrontStageController()
                .getCurrentSprite().setSpriteCostume(Integer.valueOf(number.getText()));
    }

    @FXML
    protected void handleCopyButtonAction(ActionEvent event) {
        Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
        sprite.addCostume(title.getText() + "のコピー", image.getImage());
        mainController.getBackStageController().showCostumes(sprite);
    }

    @FXML
    protected void handleEditButtonAction(ActionEvent event) throws Exception {
        Window owner = image.getScene().getWindow(); //画面オーナー
        URL fxml = getClass().getResource("Paint.fxml"); //表示するfxml
        final StageUtil paintStage = new StageUtil(null, owner, fxml, image.getImage());

        //新規コスチューム追加
        paintStage.getStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                PaintController ctrl = (PaintController) paintStage.getController();

                if (ctrl.getResult() != null) {
                    Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
                    sprite.updateCostume(Integer.valueOf(number.getText()), ctrl.getResult());

                    image.setImage(ctrl.getResult());
                }
            }
        });

        paintStage.getStage().show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @Override
    public void setParameter(Costume costume) {
        this.title.setText(costume.getTitle());
        this.image.setImage(costume.getImage());
        this.number.setText(Integer.toString(costume.getNumber()));
    }
}
