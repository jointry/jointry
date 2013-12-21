package jp.ac.aiit.jointry.util;

import broker.util.Base64;
import broker.util.ImageUtil;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import jp.ac.aiit.jointry.models.Costume;
import jp.ac.aiit.jointry.models.Jty;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.expression.Expression;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;
import jp.ac.aiit.jointry.services.file.FileManager;

public class JsonUtil {

    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String SPRITE_TAG = "sprite";
    public static final String COSTUME_TAG = "costume";
    public static final String SCRIPT_TAG = "script";
    public static final String TYPE_BASE64 = "base64";
    public static final String TYPE_FILE = "file";

    public static String convertObjectToJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            return "";
        }
    }

    public static List convertJsonStringToList(String jsonString) {
        List list = new ArrayList();
        try {
            list = objectMapper.readValue(jsonString, ArrayList.class);
        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public static Map<String, String> processSprite(Sprite sprite) {
        Map<String, String> spriteMap = new HashMap();
        spriteMap.put("title", sprite.getName());
        spriteMap.put("layoutX", Double.toString(sprite.getTranslateX()));
        spriteMap.put("layoutY", Double.toString(sprite.getTranslateY()));
        spriteMap.put("costume", Integer.toString(sprite.getCostumeNumber()));
        spriteMap.put("rotate", Double.toString(sprite.getRotate()));

        String speech = sprite.getSpeech();
        if (speech != null) {
            spriteMap.put("speech", speech);
        }

        return spriteMap;
    }

    public static ArrayList<Map> processCostumes(Sprite sprite, String dir) {
        ArrayList<Map> costumes = new ArrayList();
        for (Costume costume : sprite.getCostumes()) {
            Map<String, String> costumeMap = new HashMap();
            costumeMap.put("title", costume.getTitle());
            String fileName = sprite.getName() + "_costume" + costume.getNumber() + ".png";
            costumeMap.put("img_src", fileName);
            saveImage(new File(dir, "img"), fileName, costume.getImage());
            costumeMap.put("img", Base64.encode(dir + "/img/" + fileName));
            costumes.add(costumeMap);
        }
        return costumes;
    }

    public static Map<String, String> processScript(Sprite sprite, String dir) {
        ArrayList<Map> source = new ArrayList();
        for (Node node : sprite.getScriptPane().getChildrenUnmodifiable()) {
            if (node instanceof Statement) {
                Statement procedure = (Statement) node;
                if (procedure.isTopLevelBlock()) {
                    Map<String, Object> blockInfo = new HashMap();
                    blockInfo.put("coordinate", procedure.getLayoutX() + " " + procedure.getLayoutY());
                    blockInfo.put("block", BlockUtil.getAllStatus(procedure));
                    source.add(blockInfo);
                }
            } else if (node instanceof Expression) {
                Expression exp = (Expression) node;
                if (!exp.hasMother()) {
                    Map<String, Object> blockInfo = new HashMap();
                    blockInfo.put("coordinate", exp.getLayoutX() + " " + exp.getLayoutY());
                    blockInfo.put("block", BlockUtil.getStatus(exp));
                    source.add(blockInfo);
                }
            }
        }

        Map<String, String> map = new HashMap<>();
        String name = sprite.getName() + "_script";
        String contents = convertObjectToJsonString(source);
        saveScriptFile(dir, name, contents);
        map.put("script_src", name);
        map.put("script", contents);
        return map;
    }

    public static Status parseJSONString(String jsonString) {
        Status status = null;

        TypeReference type = new TypeReference<Status>() {
        };

        try {
            status = objectMapper.readValue(jsonString, type);
        } catch (JsonGenerationException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return status;
    }

    public static Sprite parseJSONStringToSprite(String jsonString, File file) throws MalformedURLException, FileNotFoundException, IOException {
        Jty jty = objectMapper.readValue(jsonString, Jty.class);

        // load sprite
        Map<String, String> spriteMap = jty.getSprite();
        Sprite sprite = new Sprite();
        sprite.setName(spriteMap.get("title"));
        sprite.setTranslateX(Double.valueOf(spriteMap.get("layoutX")));
        sprite.setTranslateY(Double.valueOf(spriteMap.get("layoutY")));
        sprite.setRotate(Double.valueOf(spriteMap.get("rotate")));

        String speech = spriteMap.get("speech");
        if (speech != null) {
            sprite.setSpeechBubble(speech);
        }

        // load costume
        List<Map> costumes = jty.getCostume();
        parseJSONStringToCostumes(sprite, costumes, file);
        sprite.setSpriteCostume(Integer.parseInt(spriteMap.get("costume")));

        // load script
        Map<String, String> scriptMap = jty.getScript();

        String parentPath = file.getParent();
        File scriptFile = new File(parentPath, scriptMap.get("script_src"));

        if (scriptFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(scriptFile))) {
                String line = br.readLine();
                if (line != null) {
                    parseJSONStringToBlocks(line, sprite);
                }
            }
        } else {
            parseJSONStringToBlocks(scriptMap.get("script"), sprite);
        }

        return sprite;
    }

    public static void parseJSONStringToCostumes(Sprite sprite, List<Map> costumes, File file) throws MalformedURLException {
        for (Map<String, String> costumeMap : costumes) {
            String title = costumeMap.get("title");
            String parentPath = file.getParent();
            File imgFile = new File(parentPath + "/img", costumeMap.get("img_src"));

            if (imgFile.exists()) {
                sprite.addCostume(title, new Image(imgFile.toURI().toURL().toString()));
            } else {
                // decode Base64
                String value = costumeMap.get("img");
                List<String> list = new ArrayList();
                list.addAll(Arrays.asList(value.split("\n")));
                WritableImage img = SwingFXUtils.toFXImage(ImageUtil.createImage(list), null);
                sprite.addCostume(title, img);
            }
        }
    }

    private static void parseJSONStringToBlocks(String jsonString, Sprite sprite) {
        ArrayList<Map> source = null;

        try {
            source = objectMapper.readValue(jsonString, ArrayList.class);
        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        for (Map blocks_info : source) {
            Block topBlock = null;
            Block prevBlock = null;
            Object blocks = blocks_info.get("block");

            if (blocks instanceof ArrayList) {
                for (Map status_info : ((ArrayList<Map>) blocks)) {
                    Block block = BlockUtil.create(status_info);
                    block.setSprite(sprite);

                    Status status = BlockUtil.convertMapToStatus(status_info.get(block.getClass().getSimpleName()));
                    block.setStatus(status);

                    if (topBlock == null) {
                        topBlock = block;
                        prevBlock = topBlock;
                    } else if (prevBlock != null) {
                        ((Statement) prevBlock).addLink((Statement) block);
                        prevBlock = block;
                    }
                }
            } else {
                Map status_info = ((Map) blocks);
                Block block = BlockUtil.create(status_info);
                block.setSprite(sprite);

                Status status = BlockUtil.convertMapToStatus(status_info.get(block.getClass().getSimpleName()));
                block.setStatus(status);

                String cordinate = (String) blocks_info.get("coordinate");
                String[] pos = cordinate.split(" ");

                double x = Double.valueOf(pos[0]);
                double y = Double.valueOf(pos[1]);
                block.move(x, y); //子要素含めて全ての座標を設定

                block.show();
            }

            if (topBlock != null) {
                ((Statement) topBlock).fetchPrevTopBlock();

                //topblockの座標
                String cordinate = (String) blocks_info.get("coordinate");
                String[] pos = cordinate.split(" ");

                double x = Double.valueOf(pos[0]);
                double y = Double.valueOf(pos[1]);
                topBlock.move(x, y); //子要素含めて全ての座標を設定

                topBlock.show();
            }
        }
    }

    private static void saveImage(File dir, String name, Image image) {
        if (!dir.exists()) {
            dir.mkdirs(); //create img folder
        }
        File fileName = new File(dir.getPath(), name);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fileName);

        } catch (IOException ex) {
            Logger.getLogger(FileManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void saveScriptFile(String dir, String name, String content) {
        try (PrintWriter script = new PrintWriter(new File(dir, name))) {
            script.print(content);
            script.flush();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JsonUtil.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
