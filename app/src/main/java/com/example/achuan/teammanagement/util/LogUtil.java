package com.example.achuan.teammanagement.util;

import android.util.Log;

/**
 * Created by achuan on 16-08-24.
 * 功能：对打印事件的自定义控制,上线后通过改变阶段变量的数值实现打印事件的屏蔽
 */

public class LogUtil
{
    public static final int VERBOSE=1,DEBUG=2,INFO=3,WARN=4,ERROR=5,NOTHING=6;
    public static final int LEVEL=VERBOSE;//开发阶段指定成1,上线后指定成6
    public static void v(String tag,String msg)
    {
        if(LEVEL<=VERBOSE)
        {
            Log.v(tag,msg);
        }
    }
    public static void d(String tag,String msg)
    {
        if(LEVEL<=DEBUG)
        {
            Log.d(tag,msg);
        }
    }
    public static void i(String tag,String msg)
    {
        if(LEVEL<=INFO)
        {
            Log.i(tag,msg);
        }
    }
    public static void w(String tag,String msg)
    {
        if(LEVEL<=WARN)
        {
            Log.w(tag,msg);
        }
    }
    public static void e(String tag,String msg)
    {
        if(LEVEL<=ERROR)
        {
            Log.e(tag,msg);
        }
    }
}