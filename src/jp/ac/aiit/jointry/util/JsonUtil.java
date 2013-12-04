/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.ac.aiit.jointry.services.file.FileManager;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    public static Map parceMapJSONString(String jsonString) {
        Map jsonMap = null;

        try {
            jsonMap = objectMapper.readValue(jsonString, Map.class);
        } catch (JsonGenerationException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonMap;
    }

    public static ArrayList parceListJSONString(String jsonString) {
        ArrayList<Map> jsonMap = null;

        try {
            jsonMap = objectMapper.readValue(jsonString, ArrayList.class);
        } catch (JsonGenerationException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonMap;
    }
}
