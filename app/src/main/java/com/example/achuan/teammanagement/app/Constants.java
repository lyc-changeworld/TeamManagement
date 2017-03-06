package com.example.achuan.teammanagement.app;

/**
 * Created by achuan on 17-1-25.
 */

public class Constants {

    //=================MODULE TYPE CODE====================
    public static final int TYPE_NEWS=100;
    public static final int TYPE_CONTACTS=200;
    public static final int TYPE_EXPLORE=300;
    public static final int TYPE_MYSELF=400;
    /*MAIN TYPE CODE*/
    public static final int TYPE_SETTINGS=001;

    /*MODULE_0 ITEM TYPE CODE*/


    //=================SHARED_PREFERENCE VALUE_NAME====================
    //创建的SharedPreferences文件的文件名
    public static final String PREFERENCES_NAME = "my_sp";
    //当前处于的模块
    public static final String CURRENT_ITEM = "current_item";
    //当前环信账号的用户名
    public static final String KEY_USERNAME = "username";

    //=================OTHER STRING====================
    //聊天消息的类型
    public static final int CHATTYPE_SINGLE = 1;//单人
    public static final int CHATTYPE_GROUP = 2;//群
    public static final int CHATTYPE_CHATROOM = 3;//聊天室

}
