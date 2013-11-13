/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.models;

public class Room {

    private String name;
    private String proxyId;

    public Room(String server) {
        String values = server.substring(server.indexOf("[") + 1, server.indexOf("]"));

        name = values.substring(values.indexOf(" ") + 1, values.length());
        proxyId = values.substring(0, values.indexOf("."));
    }

    public String getName() {
        return name;
    }

    public String getProxyId() {
        return proxyId;
    }
}
