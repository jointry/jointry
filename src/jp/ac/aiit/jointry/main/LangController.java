package jp.ac.aiit.jointry.main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
import jp.ac.aiit.jointry.statics.TestData;
import jp.ac.aiit.jointry.paint.QuickPaint;

/**
 *
 * @author kanemoto
 */
public class LangController implements Initializable {

    @FXML
    private VBox vbox;
    @FXML
    private AnchorPane lang_pane;
    private CostumeCntroller ctrl;

    @FXML
    protected void handleAddBtnAct(ActionEvent event) throws Exception {
        ctrl.refresh();
        ProgramController.refresh();
    }

    @FXML
    protected void handlePaintBtnAct(ActionEvent event) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TestData data = new TestData();
                BufferedImage readImage = data.getCameraFile();

                QuickPaint paint = new QuickPaint(readImage);
                paint.main();
            }
        });
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

                    ctrl = (CostumeCntroller) fxmlLoader.getController();
                } catch (IOException ex) {
                }
            }
        });

        newStage.show();

    }

    @FXML
    protected void handleExecuteBtnAct(ActionEvent event) {
        execute();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Block b1 = new Block(0, 0, Color.RED);
        Block b2 = new Block(0, 150, Color.BLUE);
        Block b3 = new Block(0, 300, Color.YELLOW);
        lang_pane.getChildren().addAll(b1, b2, b3);
    }

    private void execute() {
        ImageView image = ProgramController.getImage();
        String code = "";
        for (Node node : lang_pane.getChildrenUnmodifiable()) {
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
}
