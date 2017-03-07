package com.example.achuan.teammanagement.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by achuan on 17-1-25.
 * 功能：
 */

public class App  extends Application {

    //单例模式定义变量,保证只会实例化一次
    private static App instance;
    private static Context sContext;//全局变量
    //声明一个数组来存储活动
    private static List<Activity> sActivities=new ArrayList<Activity>();

    private boolean isHyphenateInited = false;

    //环信appkey
    private static final String appKey="1145161121178643#teammanagement";



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
        /*---Litepal数据库初始化---*/
        LitePal.initialize(this);
        /***环信-初始化SDK***/
        initHyphenate(sContext);
    }

    /***
     * 第一步：sdk的一些参数配置 EMOptions
     * 第二步：将配置参数封装类 传入SDK初始化
     * ***/
    public void initHyphenate(Context context) {
        EMOptions options = initChatOptions();//第一步
        boolean success = initSDK(context, options);//第二步
        if (success) {
            // 设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMClient.getInstance().setDebugMode(true);
            // 初始化数据库
            //initDbDao(context);
        }
    }
    /*-----第一步:设置EMOptions参数-----*/
    private EMOptions initChatOptions() {
        // 获取到EMChatOptions对象
        EMOptions options = new EMOptions();
        /*设置环信应用的AppKey*/
        options.setAppKey(appKey);
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);
        return options;
    }
    /*-----第二步:将配置参数封装类 传入SDK初始化-----*/
    public synchronized boolean initSDK(Context context, EMOptions options) {
        if (isHyphenateInited) {
            return true;
        }
        int pid = android.os.Process.myPid();//拿到当前进程的pid号
        String processAppName = getAppName(pid);//通过pid号获取到进程名称
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process
        // name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(
                sContext.getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return false;
        }
        if (options == null) {
            EMClient.getInstance().init(context, initChatOptions());
        } else {
            EMClient.getInstance().init(context, options);
        }
        isHyphenateInited = true;
        return true;
    }
    /*获取进程名的方法*/
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }


    /**
     * 退出登录
     *
     * @param unbindDeviceToken
     *            是否解绑设备token(使用GCM才有)
     * @param callback
     *            callback
     *            这里将方法封装了一下,并使用了接口回调机制
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
            }
            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }
            @Override
            public void onError(int code, String error) {
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }



    //1-获取全局Context的方法
    public static Context getContext() {
        return sContext;//返回这个全局的Context
    }
    /**********2-管理活动***********/
    //1添加活动到数组中
    public static void addActivity(Activity activity) {
        sActivities.add(activity);
    }
    //2从数组中移除活动
    public static void removeActivity(Activity activity) {
        sActivities.remove(activity);
    }
    //3退出APP的操作,杀光所有的进程
    public static void exitApp() {
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
