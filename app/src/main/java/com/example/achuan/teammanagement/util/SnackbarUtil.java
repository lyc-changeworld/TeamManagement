package com.example.achuan.teammanagement.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by achuan on 16/9/3.
 * 功能：SnackBar的简单使用封装类
 */

public class SnackbarUtil {

    /*------------------------1-普通提示------------------------*/
    /*长时间*/
    public static void showLong(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }
    /*短时间*/
    public static void showShort(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }

    /*------------------------2-带交互按钮的提示------------------------*/
    public interface SnackbarButtonClickListener {
        //按钮点击事件
        void onClick();
    }
    /*长时间*/
    public static void showLongWithAction(View view, String msg, String bt_msg,
                                          final SnackbarButtonClickListener listener){
        Snackbar.make(view,msg, Snackbar.LENGTH_LONG)
                //前面三个参数和Toast类似
                //下面调用一个方法来设置一个动作按钮,使可以和用户进行交互
                .setAction(bt_msg, new View.OnClickListener() {
                    //点击动作按钮后,Snackbar会立马消失
                    @Override
                    public void onClick(View view) {
                        listener.onClick();//为按钮设置监听接口回调方法
                    }
                }).show();
    }
    /*短时间*/
    public static void showShortWithAction(View view, String msg, String bt_msg,
                                          final SnackbarButtonClickListener listener){
        Snackbar.make(view,msg, Snackbar.LENGTH_SHORT)
                //前面三个参数和Toast类似
                //下面调用一个方法来设置一个动作按钮,使可以和用户进行交互
                .setAction(bt_msg, new View.OnClickListener() {
                    //点击动作按钮后,Snackbar会立马消失
                    @Override
                    public void onClick(View view) {
                        listener.onClick();//为按钮设置监听接口回调方法
                    }
                }).show();
    }



}
