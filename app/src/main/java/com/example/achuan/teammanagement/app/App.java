package com.example.achuan.teammanagement.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by achuan on 17-1-25.
 */

public class App  extends Application {

    //单例模式定义变量,保证只会实例化一次
    private static App instance;
    private static Context sContext;//全局变量
    //声明一个数组来存储活动
    private static List<Activity> sActivities=new ArrayList<Activity>();


    //单例模式,避免内存造成浪费,需要实例化该类时才将其实例化
    public static synchronized App getInstance() {
        //网上说Application的instance不能用new
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sContext=getApplicationContext();//获得一个应用程序级别的Context
    }

    //1-获取全局Context的方法
    public static Context getContext() {
        return sContext;//返回这个全局的Context
    }
    /**********2-管理活动***********/
    //1添加活动到数组中
    public static void addActivity(Activity activity)
    {
        sActivities.add(activity);
    }
    //2从数组中移除活动
    public static void removeActivity(Activity activity)
    {
        sActivities.remove(activity);
    }
    //3退出APP的操作,杀光所有的进程
    public static void exitApp()
    {
        if(sActivities!=null)
        {
            //同步执行活动销毁
            synchronized (sActivities) {
                for (Activity activity:sActivities)
                {
                    if(!activity.isFinishing())
                    {
                        activity.finish();//该操作只会将活动出栈,并没有执行onDestory()方法
                        // onDestory()方法是活动生命的最后一步,将资源空间等回收
                        // 当重新进入此Activity的时候,必须重新创建,执行onCreate()方法.
                    }
                }
            }
        }
        //杀光所有的进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);//将整个应用程序的进程KO掉
    }
}
