package com.example.achuan.teammanagement.util;

import android.content.Context;

import com.sdsmdg.tastytoast.TastyToast;

/**
 * Created by achuan on 17-2-2.
 * 功能：使用网上开源的特效Toast,进行简单的封装
 *   相关链接：https://github.com/yadav-rahul/TastyToast
 */

public class TastyToastUtil {
    /*-------------------LENGTH--------------------*/
    public static final int l_short = TastyToast.LENGTH_SHORT;//短时
    public static final int l_long = TastyToast.LENGTH_LONG;//长时
    /*--------------------TYPE---------------------*/
    public static final int t_s = TastyToast.SUCCESS;//成功
    public static final int t_w = TastyToast.WARNING;//警告
    public static final int t_e = TastyToast.ERROR;//错误
    public static final int t_i = TastyToast.INFO;//信息提示
    public static final int t_d = TastyToast.DEFAULT;//默认
    public static final int t_c = TastyToast.CONFUSING;//困惑

    //成功
    public static void success_short(Context context,String msg){
        TastyToast.makeText(context, msg,l_short ,t_s ).show();
    }
    public static void success_long(Context context,String msg){
        TastyToast.makeText(context, msg,l_long ,t_s ).show();
    }
    //警告
    public static void warning_short(Context context,String msg){
        TastyToast.makeText(context, msg,l_short ,t_w ).show();
    }
    public static void warning_long(Context context,String msg){
        TastyToast.makeText(context, msg,l_long ,t_w ).show();
    }
    //错误
    public static void error_short(Context context,String msg){
        TastyToast.makeText(context, msg,l_short ,t_e ).show();
    }
    public static void error_long(Context context,String msg){
        TastyToast.makeText(context, msg,l_long ,t_e ).show();
    }
    //信息提示
    public static void info_short(Context context,String msg){
        TastyToast.makeText(context, msg,l_short ,t_i ).show();
    }
    public static void info_long(Context context,String msg){
        TastyToast.makeText(context, msg,l_long ,t_i ).show();
    }
    //默认
    public static void default_short(Context context,String msg){
        TastyToast.makeText(context, msg,l_short ,t_d ).show();
    }
    public static void default_long(Context context,String msg){
        TastyToast.makeText(context, msg,l_long ,t_d ).show();
    }
    //困惑
    public static void confusing_short(Context context,String msg){
        TastyToast.makeText(context, msg,l_short ,t_c ).show();
    }
    public static void confusing_long(Context context,String msg){
        TastyToast.makeText(context, msg,l_long ,t_c ).show();
    }



}
