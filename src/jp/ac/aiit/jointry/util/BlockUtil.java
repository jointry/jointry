package jp.ac.aiit.jointry.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.ac.aiit.jointry.models.Status;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;

public class BlockUtil {

    public static Status convertMapToStatus(Object block_info) {
        return convertMapToStatus((Map) block_info);
    }

    public static Status convertMapToStatus(Map block_info) {
        String json = "";
        try {
            json = JsonUtil.objectMapper.writeValueAsString(block_info);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return JsonUtil.parseJSONString(json);
    }

    public static List<Status> getAllStatus(Statement procedure) {
        List<Status> blockList = new ArrayList();

        blockList.add(getStatus(procedure)); //top block
        for (Statement statement : procedure.fetchAllNextBlocks()) {
            blockList.add(getStatus(statement)); //next block
        }

        return blockList;
    }

    public static Status getStatus(Block procedure) {
        Status blockStatus = new Status();
        blockStatus.put(procedure.getClass().getSimpleName(), procedure.getStatus());
        return blockStatus;
    }

    public static Block create(Map params) {
        //マッピングされたパラメータからブロッククラスを生成
        Set<String> set = new HashSet(params.keySet());
        String cname = set.toString().substring(1, set.toString().length() - 1);

        return create(cname);
    }

    public static Block create(String className) {
        //ブロックのパッケージは複数あるため総当たりで生成してみる
        final String[] blockPath = {
            "jp.ac.aiit.jointry.models.blocks.statement.procedure.",
            "jp.ac.aiit.jointry.models.blocks.statement.codeblock.",
            "jp.ac.aiit.jointry.models.blocks.expression."
        };

        for (String path : blockPath) {
            try {
                Class clazz = Class.forName(path + className);
                Block myClass = (Block) clazz.newInstance();

                if (myClass != null) {
                    return myClass; //生成出来たら返す
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                continue; //失敗したら次
            }
        }

        return null; //ブロック生成失敗
    }
}
