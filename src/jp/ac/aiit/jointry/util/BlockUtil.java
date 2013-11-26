/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.statement.Statement;

public class BlockUtil {

    public static List<Map> getAllStatus(Statement procedure) {
        List<Map> blockList = new ArrayList();

        blockList.add(getBlockStatus(procedure)); //top block
        for (Statement statement : procedure.fetchAllNextBlocks()) {
            blockList.add(getBlockStatus(statement)); //next block
        }

        return blockList;
    }

    private static Map getBlockStatus(Block procedure) {
        Map<String, Object> blockStatus = new HashMap();
        blockStatus.put(procedure.getClass().getSimpleName(), procedure.getStatus());
        return blockStatus;
    }

    public static Block createBlock(Map params) {
        //マッピングされたパラメータからブロッククラスを生成
        Set<String> set = new HashSet(params.keySet());
        String cname = set.toString().substring(1, set.toString().length() - 1);

        return createBlock(cname);
    }

    public static Block createBlock(String className) {
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

                if (myClass != null) return myClass; //生成出来たら返す
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                continue; //失敗したら次
            }
        }

        return null; //ブロック生成失敗
    }
}
