package jp.ac.aiit.jointry.models;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Status extends HashMap {

    private LinkedHashMap params;

    /**
     * @return the params
     */
    public LinkedHashMap getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(LinkedHashMap params) {
        this.params = params;
    }
}
