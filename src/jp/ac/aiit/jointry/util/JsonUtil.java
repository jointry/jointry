/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import jp.ac.aiit.jointry.models.Costume;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;
import jp.ac.aiit.jointry.services.file.FileManager;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String SPRITE_TAG = "SPRITE";
    private static final String COSTUME_TAG = "COSTUME";
    private static final String SCRIPT_TAG = "SCRIPT";
    public static final String TYPE_BASE64 = "base64";
    public static final String TYPE_FILE = "file";

    public static String makeJSONString(Sprite sprite, String fileType, File file) throws FileNotFoundException {
        Map<String, Object> projectMap = new HashMap();

        Map<String, String> spriteMap = new HashMap();
        spriteMap.put("title", sprite.getName());
        spriteMap.put("layoutX", Double.toString(sprite.getTranslateX()));
        spriteMap.put("layoutY", Double.toString(sprite.getTranslateY()));
        spriteMap.put("costume", Integer.toString(sprite.getCostumeNumber()));

        projectMap.put(SPRITE_TAG, spriteMap);

        //save as costume
        ArrayList<Map> costumes = new ArrayList();
        for (Costume costume : sprite.getCostumes()) {
            Map<String, String> costumeMap = new HashMap();

            costumeMap.put("title", costume.getTitle());
            switch (fileType) {
                case TYPE_BASE64:
                    costumeMap.put("img", "base64"); // base64で画像をエンコードして文字列を保存したい
                    break;

                case TYPE_FILE:
                    if (file != null) {
                        String fileName = sprite.getName() + "_costume" + costume.getNumber();
                        costumeMap.put("img", saveAsImage(file, fileName, costume.getImage()));
                    }
                    break;
                default:
                    break;
            }

            costumes.add(costumeMap);
        }

        projectMap.put(COSTUME_TAG, costumes);

        //save as script
        ArrayList<Map> source = new ArrayList();

        for (Node node : sprite.getScriptPane().getChildrenUnmodifiable()) {
            if (!(node instanceof Statement)) {
                continue;
            }

            Statement procedure = (Statement) node;
            if (procedure.isTopLevelBlock()) {
                Map<String, Object> blockInfo = new HashMap();

                blockInfo.put("coordinate", procedure.getLayoutX() + " " + procedure.getLayoutY());
                blockInfo.put("block", BlockUtil.getAllStatus(procedure));

                source.add(blockInfo);
            }
        }

        switch (fileType) {
            case TYPE_FILE:
                if (file != null) {
                    String ScriptFileName = sprite.getName() + "_script";
                    try (PrintWriter script = new PrintWriter(new File(file.getParent(), ScriptFileName))) {
                        script.print(JsonUtil.makeJSONString(source));
                        script.flush();
                    }
                    projectMap.put(SCRIPT_TAG, ScriptFileName);
                }
                break;

            default:
                projectMap.put(SCRIPT_TAG, JsonUtil.makeJSONString(source));
                break;
        }

        return makeJSONString(projectMap);
    }

    public static String makeJSONString(ArrayList<Map> valueMap) {
        String jsonString = null;

        try {
            jsonString = objectMapper.writeValueAsString(valueMap);
        } catch (JsonGenerationException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonString;
    }

    public static String makeJSONString(Map valueMap) {
        String jsonString = null;

        try {
            jsonString = objectMapper.writeValueAsString(valueMap);
        } catch (JsonGenerationException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonString;
    }

    public static ArrayList<Status> parseJSONString(String jsonString) {
        ArrayList<Status> jsonList = null;

        try {
            jsonList = objectMapper.readValue(jsonString, ArrayList.class);
        } catch (JsonGenerationException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonList;
    }

    public static Sprite parseJSONStringToSprite(Sprite sprite, String jsonString, String fileType, File file) throws MalformedURLException, FileNotFoundException, IOException {
        Map<String, Object> projectMap = objectMapper.readValue(jsonString, Map.class);

        //load as sprite
        Map<String, String> spriteMap = (Map) projectMap.get(SPRITE_TAG);
        sprite.setName(spriteMap.get("title"));
        sprite.setTranslateX(Double.valueOf(spriteMap.get("layoutX")));
        sprite.setTranslateY(Double.valueOf(spriteMap.get("layoutY")));

        //load as costume
        ArrayList<Map> costumes = (ArrayList) projectMap.get(COSTUME_TAG);

        for (Map<String, String> costumeMap : costumes) {
            String title = costumeMap.get("title");
            switch (fileType) {
                case TYPE_BASE64:
                    costumeMap.get("img"); // base64で画像をデコードしたい
                    break;

                case TYPE_FILE:
                    String parentPath = file.getParent();
                    File imgFile = new File(parentPath + "/img", costumeMap.get("img"));
                    sprite.addCostume(title, new Image(imgFile.toURI().toURL().toString()));
                    break;

                default:
                    break;
            }
        }

        sprite.setSpriteCostume(Integer.parseInt(spriteMap.get("costume")));

        //load as script
        switch (fileType) {
            case TYPE_FILE:
                if (file != null) {
                    //スクリプトファイル読み込み
                    String parentPath = file.getParent();
                    File scriptFile = new File(parentPath, (String) projectMap.get(SCRIPT_TAG));

                    try (BufferedReader br = new BufferedReader(new FileReader(scriptFile))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            parseJSONStringToBlocks(line, sprite);
                        }
                    }
                }
                break;

            default:
                parseJSONStringToBlocks((String) projectMap.get(SCRIPT_TAG), sprite);
                break;
        }

        return sprite;
    }

    private static void parseJSONStringToBlocks(String jsonString, Sprite sprite) {
        ArrayList<Status> source = JsonUtil.parseJSONString(jsonString);

        for (Status blocks : source) {
            Block topBlock = null;
            Block prevBlock = null;

            for (Status s : (ArrayList<Status>) blocks.get("block")) {
                Block block = BlockUtil.createBlock(s); //ブロック生成
                block.setStatus((Status) s.get(block.getClass().getSimpleName())); //パラメータ設定

                if (topBlock == null) {
                    topBlock = block;
                    prevBlock = topBlock;
                } else if (prevBlock != null) {
                    ((Statement) prevBlock).addLink((Statement) block);
                    prevBlock = block;
                }
            }

            if (topBlock != null) {
                ((Statement) topBlock).fetchPrevTopBlock();

                //topblockの座標
                String cordinate = (String) blocks.get("coordinate");
                String[] pos = cordinate.split(" ");

                double x = Double.valueOf(pos[0]);
                double y = Double.valueOf(pos[1]);
                topBlock.move(x, y); //子要素含めて全ての座標を設定

                topBlock.outputBlock(sprite); //scriptPaneへBlockを登録
            }
        }
    }

    private static String saveAsImage(File file, String name, Image image) {
        File folder = new File(file.getParent(), "img");
        if (!folder.exists()) {
            folder.mkdir(); //create img folder
        }
        name = name + ".png";
        File fileName = new File(folder.getPath(), name);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fileName);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return name;
    }
}
