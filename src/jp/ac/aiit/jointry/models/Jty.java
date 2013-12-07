package jp.ac.aiit.jointry.models;

import java.util.ArrayList;
import java.util.Map;

public class Jty {

    private Map<String, String> sprite;
    private String script;
    private ArrayList<Map> costume;

    /**
     * @return the sprite
     */
    public Map getSprite() {
        return sprite;
    }

    /**
     * @param sprite the sprite to set
     */
    public void setSprite(Map<String, String> sprite) {
        this.sprite = sprite;
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @param script the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @return the costume
     */
    public ArrayList<Map> getCostume() {
        return costume;
    }

    /**
     * @param costume the costume to set
     */
    public void setCostume(ArrayList<Map> costume) {
        this.costume = costume;
    }
}
