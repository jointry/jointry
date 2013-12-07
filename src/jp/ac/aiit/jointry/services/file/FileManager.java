/*
 * To change this template, choose Tools | Templates
 * and load the template in the editor.
 */
package jp.ac.aiit.jointry.services.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import jp.ac.aiit.jointry.controllers.MainController;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.util.JsonUtil;
import org.xml.sax.SAXException;

public class FileManager {

    private static final String JOINTRY_EXTENSION = ".jty";
    private static final String PROJECT_TAG = "PROJECT";
    private static final String SPRITE_TAG = "SPRITE";
    private static final String COSTUME_TAG = "COSTUME";
    private static final String SCRIPT_TAG = "SCRIPT";

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
                script.print(JsonUtil.makeJSONString(sprite, JsonUtil.TYPE_FILE, file));
                script.print("\n");
            }

            script.flush();
        }
    }

    public void load(MainController mainController) throws ParserConfigurationException, SAXException, IOException {
        FileChooser fc = createFileChooser("open");
        fc.getExtensionFilters().add(new ExtensionFilter("jointry設定ファイル", "*" + JOINTRY_EXTENSION));

        File file = fc.showOpenDialog(null);
        if (file == null) {
            return; //読込先が指定されなかった
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Sprite sprite = JsonUtil.parseJSONStringToSprite(line, JsonUtil.TYPE_FILE, file);
                sprite.setMainController(mainController);

                mainController.getFrontStageController().addSprite(sprite);

                if (mainController.getFrontStageController().getCurrentSprite() == null) {
                    mainController.getFrontStageController().setCurrentSprite(sprite);
                }
            }
        }
    }

    public FileChooser createFileChooser(String title) {
        FileChooser fc = new FileChooser();

        fc.setTitle(title); //title
        fc.setInitialDirectory(new File(System.getProperty("user.home"))); //home

        return fc;
    }

    private String saveAsImage(File file, String name, Image image) {
        File folder = new File(file.getParent(), "img");
        if (!folder.exists()) {
            folder.mkdir(); //create img folder
        }
        name = name + ".png";
        File fileName = new File(folder.getPath(), name);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fileName);

        } catch (IOException ex) {
            Logger.getLogger(FileManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return name;
    }
}
