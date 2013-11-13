/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import jp.ac.aiit.jointry.services.broker.core.Account;

public class JointryAccount extends Account {

    @Override
    public void save() {
        //機能無効
    }

    @Override
    public boolean certify(String name, String password) {
        return true; //パスワード認証は行わない
    }
}
