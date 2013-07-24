package jp.ac.aiit.jointry.controllers;

import jp.ac.aiit.jointry.lang.parser.LangReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.SequentialTransition;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import jp.ac.aiit.jointry.lang.ast.ASTree;
import jp.ac.aiit.jointry.lang.ast.NullStmnt;
import jp.ac.aiit.jointry.lang.parser.JoinTryParser;
import jp.ac.aiit.jointry.lang.parser.Lexer;
import jp.ac.aiit.jointry.lang.parser.ParseException;
import jp.ac.aiit.jointry.lang.parser.Token;
import jp.ac.aiit.jointry.lang.parser.env.BasicEnv;
import jp.ac.aiit.jointry.lang.parser.env.Environment;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.statics.TestData;

public class BackStageController implements Initializable {

    @FXML
    private ScrollPane costumeList;
    @FXML
    private AnchorPane scriptPane;
    private MainController mainController;

    @FXML
    protected void handlePaintBtnAct(ActionEvent event) throws Exception {
        Stage paintStage = createStage("Paint.fxml", null);

        //新規コスチューム追加
        paintStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                TestData<Image> data = new TestData();
                if (data.get("paintImage") != null) {
                    Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
                    sprite.addCostume(createCostume(sprite.getNumber(),
                            "costume", data.get("paintImage")));
                }
            }
        });

        paintStage.show();
    }

    public Parent createCostume(int num, String title, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Costume.fxml"));

        Parent result = null;
        try {
            result = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(BackStageController.class.getName()).log(Level.SEVERE, null, ex);
        }

        CostumeCntroller controller = (CostumeCntroller) fxmlLoader.getController();
        controller.setFrontStageController(mainController.getFrontStageController());
        controller.setInfo(num, title, image);

        return result;
    }

    @FXML
    protected void handleCamBtnAct(ActionEvent event) throws Exception {

        Stage cameraStage = createStage("Camera.fxml", new Stage());

        //新規コスチューム追加
        cameraStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                TestData<Image> data = new TestData();
                if (data.get("cameraImage") != null) {
                    Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
                    sprite.addCostume(createCostume(sprite.getNumber(),
                            "costume", data.get("cameraImage")));
                }
            }
        });

        cameraStage.show();
    }

    @FXML
    protected void handleCostumeSelected(Event event) {
        //コスチューム更新
        Sprite sprite = mainController.getFrontStageController().getCurrentSprite();
        costumeList.setContent(sprite.getCostumeList());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void changeCurrentSprite(Sprite sprite) {
        //コスチューム更新
        costumeList.setContent(sprite.getCostumeList());
    }

    public void execute() {
        ImageView image = mainController.getFrontStageController().getCurrentSprite();
        StringBuilder code = new StringBuilder();
        for (Node node : scriptPane.getChildrenUnmodifiable()) {
            if (node instanceof Block) {
                Block block = (Block) node;
                if (block.isTopLevelBlock()) {
                    code.append(block.intern());
                }
            }
        }

        // Debug
        System.out.println(code);

        Lexer lexer = new Lexer(new LangReader(code.toString()));
        JoinTryParser parser = new JoinTryParser();

        Environment env = new BasicEnv();
        env.setImage(image);
        SequentialTransition st = new SequentialTransition();
        env.setSequentialTransition(st);

        try {
            while (lexer.peek(0) != Token.EOF) {
                ASTree t = parser.parse(lexer);
                if (!(t instanceof NullStmnt)) {
                    Object r = t.eval(env);
                    //Debug用
                    //System.out.println(r);
                }
            }
        } catch (ParseException ex) {
        }

        st.play();
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    private Stage createStage(String fxml, Stage stage) throws IOException {
        if (stage == null) {
            stage = new Stage(StageStyle.TRANSPARENT);
        }

        //オーナー設定
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner((Stage) scriptPane.getScene().getWindow());

        //UI読み込み
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage.setScene(new Scene(root));

        return stage;
    }
}
