package com.example.achuan.teammanagement.model.db;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by achuan on 17-3-2.
 * 功能：
 */

public class DBManager {

    /*--------------------------1-邀请消息相关--------------------------*/
    /**
     * 保存message到本地数据库
     * @param message
     * @return  返回这条messaged在db中的id
     */
    public static Integer saveMessage(InviteMessage message){
        int id=-1;
        InviteMessage inviteMessage=new InviteMessage();
        inviteMessage.setFrom(message.getFrom());
        inviteMessage.setTime(message.getTime());
        inviteMessage.setReason(message.getReason());
        //inviteMessage.setStatus(message.getStatus());//这一项无法存储到数据库中
        //存储对应状态在枚举类中的位置
        inviteMessage.setStatusOrdinal(message.getStatusOrdinal());//存储位置可以
        inviteMessage.setGroupId(message.getGroupId());
        inviteMessage.setGroupName(message.getGroupName());
        inviteMessage.setGroupInviter(message.getGroupInviter());
        inviteMessage.save();//保存
        //id=message.getId();
        return id;
    }

    /**
     * 从本地数据库获取全部的messges
     * @return InviteMessage集合数据
     */
    public static List<InviteMessage> getMessagesList(){
        List<InviteMessage> msgs = new ArrayList<InviteMessage>();
        //查询表格中的所有数据
        msgs= DataSupport.findAll(InviteMessage.class);
        return msgs;
    }

    /**
     * 更新message的状态(根据消息的id号进行更新)
     * @param msgId,mesageStatus
     */
    public static void updateMessage(int msgId,int ordinal){
        InviteMessage inviteMessage=new InviteMessage();
        inviteMessage.setStatusOrdinal(ordinal);//更新对应状态对应的序数
        inviteMessage.update(msgId);//更新对应id号的数据
    }

    /**
     * 删除要求消息(根据消息发起人名称进行删除)
     * @param from
     */
    public static void deleteMessage(String from){
        DataSupport.deleteAll(InviteMessage.class,"from=?",from);
    }

    /*--------------------------2-联系人相关--------------------------*/
    /**
     * 保存一个联系人
     * @param user
     */
    public static void saveContact(ContactUser user){
        ContactUser contactUser=new ContactUser();
        contactUser.setId(user.getId());
        contactUser.setUserName(user.getUserName());
        contactUser.setNick(user.getNick());
        contactUser.setAvatar(user.getAvatar());
        //调用该方法避免重复添加联系人
        contactUser.saveIfNotExist("userName=?",contactUser.getUserName());
    }

    /**
     * 保存好友list （先清空表格,后重新存储联系人数据）
     * @param contactList
     */
    public static void saveContactList(List<ContactUser> contactList) {
        //存储联系人之前清空联系人表
        DataSupport.deleteAll(ContactUser.class);
        //轮询存储用户人
        for (ContactUser contactUser:contactList){
            ContactUser user=new ContactUser();
            user.setId(contactUser.getId());
            user.setUserName(contactUser.getUserName());
            user.setNick(contactUser.getNick());
            user.setAvatar(contactUser.getAvatar());
            //user.save();
            //调用该方法避免重复添加联系人
            user.saveIfNotExist("userName=?",contactUser.getUserName());
        }
    }

    /**
     * 获取好友list
     * @return Map<String, ContactUser>
     */
    public static Map<String, ContactUser> getContactList() {
        //这里使用了Map集合:<key值,2.value值>
        Map<String, ContactUser> users = new Hashtable<String, ContactUser>();
        /*---查询表格中的所有数据---*/
        List<ContactUser> contactUsers = new ArrayList<ContactUser>();
        contactUsers= DataSupport.findAll(ContactUser.class);
        //轮询查询数据
        for (ContactUser user:contactUsers){
            users.put(user.getUserName(),user);
        }
        return users;
    }

    /**
     * 删除一个联系人
     * @param username
     */
    public static void deleteContact(String username){
        DataSupport.deleteAll(ContactUser.class,"userName=?",username);
    }



}
