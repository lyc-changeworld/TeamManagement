package com.example.achuan.teammanagement.model.db;

import org.litepal.crud.DataSupport;

/**
 * Created by achuan on 17-3-1.
 * 功能：创建表(使用的LitePal开源数据库)
 *      好友的数据库表结构
 */

public class ContactUser extends DataSupport{

    //id号
    private int id;
    //用户名
    private String userName;
    //昵称
    private String nick;
    //头像
    private String avatar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
