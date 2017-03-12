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
    public static final int TYPE_SETTINGS=101;



    //=================SHARED_PREFERENCE VALUE_NAME====================
    //创建的SharedPreferences文件的文件名
    public static final String PREFERENCES_NAME = "my_sp";
    //当前处于的模块
    public static final String CURRENT_ITEM = "current_item";
    //当前环信账号的用户名
    public static final String KEY_USERNAME = "username";


    //=================环信即时通讯相关====================
    //聊天消息的类型
    public static final String EXTRA_USER_ID="userId";//单人聊天的名称
    public static final int CHATTYPE_SINGLE = 10;//单人
    public static final String EXTRA_CHAT_TYPE="group_type";//群聊天类型
    public static final int CHATTYPE_GROUP = 20;//群
    public static final int CHATTYPE_CHATROOM = 30;//聊天室
    //群组
    public static final String GROUP_NAME="groupName";//创建群组时的群名称
    public static final String NEW_MEMBERS="newmembers";//创建群组时邀请的成员集合
    public static final String GROUP_ID="groupId";//选中群组时传递过来的群ID号


    //=================REQUEST CODE请求码====================
    public static final int GROUP_PICK_CONTACTS_REQUEST_CODE=0;


    //=================OTHER STRING====================


}
