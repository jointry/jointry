package jp.ac.aiit.jointry.controllers;

import jp.ac.aiit.jointry.parser.LangReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import jp.ac.aiit.jointry.ast.ASTree;
import jp.ac.aiit.jointry.ast.NullStmnt;
import jp.ac.aiit.jointry.blocks.Block;
import jp.ac.aiit.jointry.parser.JoinTryParser;
import jp.ac.aiit.jointry.parser.Lexer;
import jp.ac.aiit.jointry.parser.ParseException;
import jp.ac.aiit.jointry.parser.Token;
import jp.ac.aiit.jointry.parser.env.BasicEnv;
import jp.ac.aiit.jointry.parser.env.Environment;

/**
 *
 * @author kanemoto
 */
public class BackStageController implements Initializable {

    @FXML
    private VBox vbox;
    @FXML
    private AnchorPane scriptPane;
    private CostumeCntroller costumeController;
    private MainController mainController;

    @FXML
    protected void handleAddBtnAct(ActionEvent event) throws Exception {
        costumeController.refresh();
        FrontStageController.refresh();
    }

    @FXML
    protected void handlePaintBtnAct(ActionEvent event) throws Exception {
        //Paintツール画面
        Stage paintStage = new Stage(StageStyle.TRANSPARENT);

        //オーナー設定
        paintStage.initModality(Modality.APPLICATION_MODAL);
        paintStage.initOwner((Stage) scriptPane.getScene().getWindow());

        Parent root = FXMLLoader.load(getClass().getResource("fxml/Paint.fxml"));

        // 新しいウインドウを表示
        paintStage.setScene(new Scene(root));
        paintStage.show();
    }

    @FXML
    protected void handleCamBtnAct(ActionEvent event) throws Exception {
        //ウィンドウ用ステージ
        Stage newStage = new Stage();

        // 元のウィンドウは操作できないように設定
        newStage.initModality(Modality.APPLICATION_MODAL);
        // オーナーを設定
        // Stage stageTheLabelBelongs = (Stage) code.getScene().getWindow();
        // newStage.initOwner(stageTheLabelBelongs);

        // 新しいウインドウ内に配置するコンテンツを生成
        Parent root = FXMLLoader.load(getClass().getResource("fxml/Camera.fxml"));

        // 新しいウインドウを表示
        newStage.setScene(new Scene(root));

        //新規コスチューム追加
        newStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                            .getResource("fxml/Costume.fxml"));
                    vbox.getChildren().add((Parent) fxmlLoader.load());

                    costumeController = (CostumeCntroller) fxmlLoader.getController();
                } catch (IOException ex) {
                }
            }
        });

        newStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // dummy blocks...
        //Block b1 = new Block(0, 0, Color.RED);
        //Block b2 = new Block(0, 150, Color.BLUE);
        //Block b3 = new Block(0, 300, Color.YELLOW);
        //scriptPane.getChildren().addAll(b1, b2, b3);
    }

    public void execute() {
        ImageView image = FrontStageController.getImage();
        String code = "";
        for (Node node : scriptPane.getChildrenUnmodifiable()) {
            if (node instanceof Block) {
                Block block = (Block) node;
                if (!block.existPrevBlock()) {
                    code += block.intern();
                }
            }
        }
        Lexer lexer = new Lexer(new LangReader(code));
        JoinTryParser parser = new JoinTryParser();

        Environment env = new BasicEnv();
        env.setImage(image);

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
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
}
