package jp.ac.aiit.jointry.services.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import jp.ac.aiit.jointry.controllers.MainController;
import jp.ac.aiit.jointry.models.Jty;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.util.JsonUtil;
import org.xml.sax.SAXException;

public class FileManager {

    private static final String JOINTRY_EXTENSION = ".jty";

    public void save(List<Sprite> sprites) throws IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        FileChooser fc = createFileChooser("save");
        fc.getExtensionFilters().add(new ExtensionFilter("All Files", "*")); //ディレクトリを指定

        File chooser = fc.showSaveDialog(null);
        if (chooser == null) {
            return; //保存先が指定されなかった
        }
        if (!chooser.exists()) {
            chooser.mkdir(); //create project folder
        }
        File file = new File(chooser.getPath(), chooser.getName() + JOINTRY_EXTENSION);

        try (PrintWriter script = new PrintWriter(file)) {
            for (Sprite sprite : sprites) {
                script.print(convertSpriteToJson(sprite, file.getParent()));
                script.print("\n");
            }
            script.flush();
        }
    }

    public static String convertSpriteToJson(Sprite sprite, String save_dir) {
        Jty wrap = new Jty();
        wrap.setSprite(JsonUtil.processSprite(sprite));
        wrap.setCostume(JsonUtil.processCostumes(sprite, save_dir));
        wrap.setScript(JsonUtil.processScript(sprite, save_dir));
        return JsonUtil.convertObjectToJsonString(wrap);
    }

    public void load(MainController mainController) throws ParserConfigurationException, SAXException, IOException {
        FileChooser fc = createFileChooser("open");
        fc.getExtensionFilters().add(new ExtensionFilter("jointry設定ファイル", "*" + JOINTRY_EXTENSION));

        File file = fc.showOpenDialog(null);
        if (file == null) {
            return; //読込先が指定されなかった
        }

        mainController.initWindow("load"); //読み込む前に画面を一旦クリア

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Sprite sprite = JsonUtil.parseJSONStringToSprite(line, file);
                sprite.setMainController(mainController);

                mainController.getFrontStageController().addSprite(sprite, true);

                if (mainController.getFrontStageController().getCurrentSprite() == null) {
                    mainController.getFrontStageController().setCurrentSprite(sprite);
                }
            }
        }
    }

    private FileChooser createFileChooser(String title) {
        FileChooser fc = new FileChooser();

        fc.setTitle(title); //title
        fc.setInitialDirectory(new File(System.getProperty("user.home"))); //home

        return fc;
    }
}
