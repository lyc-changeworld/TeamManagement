package com.example.achuan.teammanagement.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.achuan.teammanagement.app.App;
import com.example.achuan.teammanagement.app.Constants;


/**
 * Created by achuan on 16-9-10.
 * 功能：存储设置及一些全局的信息到SharedPreferences文件中
 */
public class SharedPreferenceUtil {

    /***设置默认模式***/
    //默认显示的item布局
    private static final int DEFAULT_CURRENT_ITEM = Constants.TYPE_NEWS;


    //1-创建一个SharedPreferences文件
    /*---由于使用了PreferenceFragment并将它那边的界面的状态保存到了该键值存储文件下,
    *    这样就可以通过该文件获取设置的数据了*/
    public static  SharedPreferences getAppSp() {
        return App.getInstance().getSharedPreferences(
                Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /***定义get和set方法,实现对SharedPreferences文件中属性值的读取和修改***/
    //2-当前显示对应的item的布局
    public static int getCurrentItem() {
        return getAppSp().getInt(Constants.CURRENT_ITEM, DEFAULT_CURRENT_ITEM);
    }
    public static void setCurrentItem(int item) {
        getAppSp().edit().putInt(Constants.CURRENT_ITEM, item).commit();
    }
    //3-当前环信用户信息
    public static String getCurrentUserName() {
        return getAppSp().getString(Constants.KEY_USERNAME,null);
    }
    public static void setCurrentUserName(String username) {
        getAppSp().edit().putString(Constants.KEY_USERNAME, username).commit();
    }




}
