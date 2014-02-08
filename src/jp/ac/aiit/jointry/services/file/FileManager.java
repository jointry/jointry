package jp.ac.aiit.jointry.services.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import jp.ac.aiit.jointry.controllers.MainController;
import jp.ac.aiit.jointry.models.Jty;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.util.JsonUtil;

public class FileManager {

    private static final String JOINTRY_EXTENSION = ".jty";
    private static final String DEFAULT_TARGET_DIRECTORY = System.getProperty("user.home");
    private String targetDirectory = DEFAULT_TARGET_DIRECTORY;
    private final String[] matches = {
        ".+\\.jty",
        "sprite\\d+_script",
        "sprite\\d+_costume\\d+\\.png"};

    public void save(List<Sprite> sprites) throws IOException {
        FileChooser fc = createFileChooser("save");
        fc.getExtensionFilters().add(new ExtensionFilter("jointry設定ファイル", "*" + JOINTRY_EXTENSION));

        File chooser = fc.showSaveDialog(null);
        if (chooser == null) {
            return; //保存先が指定されなかった
        }

        int extensionPoint = chooser.toString().lastIndexOf(".");
        if (extensionPoint != -1 && chooser.exists()) {
            //ダブルクリック等による上書き保存
            targetDirectory = chooser.getParent();
            saveAsOverWrite(sprites);
        } else {
            targetDirectory = chooser.getPath();

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
    }

    public void saveAsOverWrite(List<Sprite> sprites) throws IOException {
        if (targetDirectory.equals(DEFAULT_TARGET_DIRECTORY)) {
            this.save(sprites);
            return;
        }

        File target = new File(targetDirectory);
        deleteDirectory(target);

        File file = new File(target.getPath(), target.getName() + JOINTRY_EXTENSION);

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

    public void load(MainController mainController) throws IOException {
        FileChooser fc = createFileChooser("open");
        fc.getExtensionFilters().add(new ExtensionFilter("jointry設定ファイル", "*" + JOINTRY_EXTENSION));

        File file = fc.showOpenDialog(null);
        if (file == null) {
            return; //読込先が指定されなかった
        }

        targetDirectory = file.getParent(); //指定されれば次回以降のパスに書き換え

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

    private void deleteDirectory(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isFile()) {
            for (String match : matches) {
                if (file.getName().matches(match)) {
                    file.delete();
                }
            }
        }

        if (file.isDirectory()) {
            for (File localFile : file.listFiles()) {
                deleteDirectory(localFile);
            }

            if (file.getName().equals("img")) {
                file.delete();
            }
        }
    }

    private FileChooser createFileChooser(String title) {
        FileChooser fc = new FileChooser();

        fc.setTitle(title); //title
        fc.setInitialDirectory(new File(targetDirectory));

        return fc;
    }
}
