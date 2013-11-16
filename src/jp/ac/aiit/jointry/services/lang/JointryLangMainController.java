package jp.ac.aiit.jointry.services.lang;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import jp.ac.aiit.jointry.services.lang.ast.ASTree;
import jp.ac.aiit.jointry.services.lang.ast.NullStmnt;
import jp.ac.aiit.jointry.services.lang.parser.Environment;
import jp.ac.aiit.jointry.services.lang.parser.JointryParser;
import jp.ac.aiit.jointry.services.lang.parser.LangReader;
import jp.ac.aiit.jointry.services.lang.parser.Lexer;
import jp.ac.aiit.jointry.services.lang.parser.ParseException;
import jp.ac.aiit.jointry.services.lang.parser.Token;

public class JointryLangMainController implements Initializable {

    @FXML
    Button btn;
    @FXML
    TextArea ta;
    @FXML
    TextArea result;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void eval(ActionEvent event) {
        Lexer lexer = new Lexer(new LangReader(ta.getText()));
        JointryParser parser = new JointryParser();
        Environment env = new Environment();
        try {
            while (lexer.peek(0) != Token.EOF) {
                ASTree t = parser.parse(lexer);
                if (!(t instanceof NullStmnt)) {
                    result.appendText(t.eval(env).toString());
                    result.appendText("\n");
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(JointryLangMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
