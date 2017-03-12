package com.example.achuan.teammanagement.model.db;

import com.example.achuan.teammanagement.util.StringUtil;

import org.litepal.crud.DataSupport;

/**
 * Created by achuan on 17-3-1.
 * 功能：创建表(使用的LitePal开源数据库)
 *      好友的数据库表结构
 */

public class ContactUser extends DataSupport implements Comparable{

    //id号
    private int id;
    //用户名
    private String userName;
    //昵称
    private String nick;
    //头像
    private String avatar;


    //昵称首字母(暂时就用用户名首字母吧,后续再添加昵称)
    //@Column(ignore = true)//忽略掉这一个元素
    private char initialLetter;

    public char getInitialLetter() {
        return initialLetter;
    }

    public void setInitialLetter(char initialLetter) {
        this.initialLetter = initialLetter;
    }



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


    //自定义比较规则,实现数据集合的排序
    @Override
    public int compareTo(Object o) {
        /**
         * compareTo()：大于0表示前一个数据比后一个数据大， 0表示相等，小于0表示前一个数据小于后一个数据
         * 相等时会走到equals()
         */
        if(o instanceof ContactUser){
            ContactUser that= (ContactUser) o;//获取到上一个数据
            int num=1;//默认为小于前一个
            //StringUtil.getHeadChar(getUserName()) == ' '代表属于"#"那一部分的内容
            if (StringUtil.getHeadChar(getUserName()) == '#') {
                num= 1;//小于
                if (StringUtil.getHeadChar(that.getUserName()) == '#'){
                    num= 0;//等于
                }
            }else if(StringUtil.getHeadChar(that.getUserName()) == '#'){
                num= -1;//大于
            } else if (StringUtil.getHeadChar(that.getUserName()) > StringUtil.getHeadChar(getUserName())) {
                num= -1;//大于
            } else if (StringUtil.getHeadChar(that.getUserName()) == StringUtil.getHeadChar(getUserName())) {
                num= 0;//等于
            }
            return num;
        }else {
            throw new ClassCastException();
        }
    }
}
