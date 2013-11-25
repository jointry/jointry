/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.file;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jp.ac.aiit.jointry.controllers.MainController;
import jp.ac.aiit.jointry.models.Costume;
import jp.ac.aiit.jointry.models.Sprite;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;
import jp.ac.aiit.jointry.util.BlockUtil;
import jp.ac.aiit.jointry.util.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FileManager {

    private static final String JOINTRY_EXTENSION = ".jty";
    private static final String PROJECT_TAG = "PROJECT";
    private static final String SPRITE_TAG = "SPRITE";
    private static final String COSTUME_TAG = "COSTUME";
    private static final String SCRIPT_TAG = "SCRIPT";

    public void save(List<Sprite> sprites) throws IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        FileChooser fc = createFileChooser("save");
        File chooser = fc.showSaveDialog(null);

        if (chooser == null) return; //保存先が指定されなかった

        if (!chooser.exists()) chooser.mkdir(); //create project folder
        File file = new File(chooser.getPath(), chooser.getName());

        //xml形式で各種情報の保存
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Element projectElm = document.createElement(PROJECT_TAG);
        document.appendChild(projectElm);

        for (Sprite sprite : sprites) {
            //save as sprite
            Element spriteElm = document.createElement(SPRITE_TAG);

            spriteElm.setAttribute("title", sprite.getName());
            spriteElm.setAttribute("layoutX", Double.toString(sprite.getTranslateX()));
            spriteElm.setAttribute("layoutY", Double.toString(sprite.getTranslateY()));
            spriteElm.setAttribute("costume", Integer.toString(sprite.getCostumeNumber()));
            projectElm.appendChild(spriteElm);

            //save as costume
            for (Costume costume : sprite.getCostumes()) {
                Element costumeElm = document.createElement(COSTUME_TAG);
                costumeElm.setAttribute("title", costume.getTitle());
                costumeElm.setAttribute("img", saveAsImage(file, sprite.getName() + "_costume" + costume.getNumber(), costume.getImage()));
                spriteElm.appendChild(costumeElm);
            }

            //save as script
            ArrayList<Map> source = new ArrayList();

            for (Node node : sprite.getScriptPane().getChildrenUnmodifiable()) {
                if (!(node instanceof Statement)) continue;

                Statement procedure = (Statement) node;

                //ブロックとの紐付 ≠ コード
                if (procedure.isTopLevelBlock()) {
                    Map<String, Object> codeMap = new HashMap();

                    codeMap.put("coordinate", procedure.getLayoutX() + " " + procedure.getLayoutY());

                    ArrayList<Map> blockList = new ArrayList();
                    procedure.blockIntern(blockList);
                    codeMap.put("block", blockList);

                    source.add(codeMap);
                }
            }

            String ScriptFileName = sprite.getName() + "_script";
            try (PrintWriter script = new PrintWriter(new File(file.getParent(), ScriptFileName))) {
                script.print(makeJSONString(source));
                script.flush();
            }

            //スプライトとの関連付け
            Element scriptElm = document.createElement(SCRIPT_TAG);
            scriptElm.setAttribute("script", ScriptFileName);
            spriteElm.appendChild(scriptElm);
        }

        //javaで保存できる形式に変換
        DOMSource source = new DOMSource(document);

        File xmlFile = new File(file.getParent(), file.getName() + JOINTRY_EXTENSION);
        try (FileOutputStream fo = new FileOutputStream(xmlFile)) {
            StreamResult result = new StreamResult(fo);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //エレメントごとの改行

            transformer.transform(source, result); //変換して保存
        }
    }

    public void open(File file, MainController mainController) throws ParserConfigurationException, SAXException, IOException {
        String parentPath = file.getParent(); //ファイル参照するためのファイルパス

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

        //spriteタグのみなめる
        NodeList sprites = document.getFirstChild().getChildNodes();
        for (int i = 0; i < sprites.getLength(); i++) {
            if (!sprites.item(i).getNodeName().equals(SPRITE_TAG)) continue;

            //環境設定
            Environment env = new Environment();
            env.setMainController(mainController);
            env.setSprite(new Sprite(mainController));

            //spriteのレイアウト
            NamedNodeMap spriteMap = sprites.item(i).getAttributes();
            String layoutX = spriteMap.getNamedItem("layoutX").getNodeValue();
            String layoutY = spriteMap.getNamedItem("layoutY").getNodeValue();
            env.getSprite().setLayoutX(Double.valueOf(layoutX));
            env.getSprite().setLayoutX(Double.valueOf(layoutY));

            //内部解析
            NodeList childs = sprites.item(i).getChildNodes();
            for (int j = 0; j < childs.getLength(); j++) {
                org.w3c.dom.Node node = childs.item(j);

                //コスチューム
                if (node.getNodeName().equals(COSTUME_TAG)) {
                    NamedNodeMap itemMap = node.getAttributes(); //マッピング読み込み

                    //各アイテム
                    String title = itemMap.getNamedItem("title").getNodeValue();
                    String img = itemMap.getNamedItem("img").getNodeValue();

                    //イメージファイルを読み込むためのパス
                    File imgFile = new File(parentPath + "/img", img);
                    env.getSprite().addCostume(title, new Image(imgFile.toURI().toURL().toString()));
                }

                //ブロック
                if (node.getNodeName().equals(SCRIPT_TAG)) {
                    NamedNodeMap itemMap = node.getAttributes(); //マッピング読み込み

                    //スクリプトファイル読み込み
                    String script = itemMap.getNamedItem("script").getNodeValue();
                    File scriptFile = new File(parentPath, script);

                    BufferedReader br = new BufferedReader(new FileReader(scriptFile));

                    String jsonBlocks = br.readLine();
                    parseJsonBlocks(jsonBlocks, env);
                }
            }

            //spriteのコスチューム設定
            int number = Integer.parseInt(spriteMap.getNamedItem("costume").getNodeValue());
            env.getSprite().setSpriteCostume(number);

            mainController.getFrontStageController().addSprite(env.getSprite());
        }
    }

    private void parseJsonBlocks(String jsonString, Environment env) throws IOException {
        //jackson使ってparse
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Map> blockList = objectMapper.readValue(jsonString, ArrayList.class);

        //ブロックの座標と内容のマッピング
        for (Map blockInfo : blockList) {
            Block topBlock = null;

            for (Map map : (ArrayList<Map>) blockInfo.get("block")) {
                //ブロックのクラス名抽出
                Set<String> set = new HashSet(map.keySet());
                String cname = set.toString().substring(1, set.toString().length() - 1);

                Block myBlock = BlockUtil.createBlock(cname); //ブロック生成

                env.setValues((HashMap) map.get(cname));

                //生成したブロックへパラメータを設定
                myBlock.setParams(env);
                env.getSprite().getScriptPane().getChildren().add(myBlock); //ブロックの表示

                if (topBlock == null) {
                    topBlock = myBlock;
                } else {
                    ((Statement) topBlock).addLink((Statement) myBlock);
                }
            }

            //topblockの座標
            String cordinate = (String) blockInfo.get("coordinate");
            String[] pos = cordinate.split(" ");

            double x = Double.valueOf(pos[0]);
            double y = Double.valueOf(pos[1]);

            //生成時に実際のmoveは行っていない
            if (topBlock != null) topBlock.move(x, y);
        }
    }

    public FileChooser createFileChooser(String title) {
        FileChooser fc = new FileChooser();

        fc.setTitle(title); //title
        fc.setInitialDirectory(new File(System.getProperty("user.home"))); //home
        fc.getExtensionFilters().add(new ExtensionFilter("jointry設定ファイル", "*" + JOINTRY_EXTENSION));

        return fc;
    }

    private String saveAsImage(File file, String name, Image image) {
        File folder = new File(file.getParent(), "img");
        if (!folder.exists()) folder.mkdir(); //create img folder

        name = name + ".png";
        File fileName = new File(folder.getPath(), name);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fileName);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return name;
    }

    private String makeJSONString(ArrayList<Map> valueMap) {
        ObjectMapper objectMapper = new ObjectMapper();
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
}
