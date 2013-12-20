/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import broker.core.Account;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JointryAccount extends Account {

    //proxy単位にユーザーを管理
    private static final ObservableList<String> userList = FXCollections.observableArrayList();

    @Override
    public void save() {
        //機能無効
    }

    @Override
    public boolean certify(String name, String password) {
        return true; //パスワード認証は行わない
    }

    public static void addUser(String userName) {
        userList.add(userName);
    }

    public static void addAllUser(String[] users) {
        for (String userName : users) {
            addUser(userName);
        }
    }

    public static void removeUser(String userName) {
        userList.remove(userName); //最初に見つけたユーザのみを削除
    }

    public static ObservableList getUsers() {
        return userList;
    }
}
