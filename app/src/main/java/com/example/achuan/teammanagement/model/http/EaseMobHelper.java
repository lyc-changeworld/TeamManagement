package com.example.achuan.teammanagement.model.http;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.exceptions.HyphenateException;

import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by achuan on 17-3-22.
 * 功能："环信"相关操作方法的封装类
 * 注明：采用创建实例对象的形式来调用方法
 */

public class EaseMobHelper {

    private static EaseMobHelper instance = null;
    /**
     * application context
     */
    private Context appContext = null;

    //环信appkey
    private static final String appKey="1145161121178643#teammanagement";
    private boolean isHyphenateInited = false;//环信初始化的标志

    int defaultErrorCode=-1;//失败了就为-1
    String defaultErrorString="操作失败...";


    /*单例模式构造实例*/
    public synchronized static EaseMobHelper getInstance(){
        if(instance == null){
            instance=new EaseMobHelper();
        }
        return instance;
    }


    /*------------------------环信SDK初始化配置--------------------------*/
    /***
     * 第一步：sdk的一些参数配置 EMOptions
     * 第二步：将配置参数封装类 传入SDK初始化
     * ***/
    public void init(Context context) {
        appContext=context;

        EMOptions options = initChatOptions();//第一步
        boolean success = initSDK(context, options);//第二步
        if (success) {
            // 设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMClient.getInstance().setDebugMode(true);
            /***初始化使用EaseUI***/
            EaseUI.getInstance().init(context,options);//该操作就是初始化SDK
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
                context.getPackageName())) {
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
        ActivityManager am = (ActivityManager) appContext.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
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


    /*------------------------注册、登录、登出--------------------------*/
    /**
     * 注册处理
     * @param userName  用户名
     * @param password  登录密码
     * @param callback  这里将方法封装了一下,并使用了接口回调机制
     * */
    public void register(String userName, String password,final EMCallBack callback){
        try {
            EMClient.getInstance().createAccount(userName, password);
            callback.onSuccess();
        } catch (HyphenateException e) {
            //e.printStackTrace();
            callback.onError(e.getErrorCode(),e.getDescription());
        }
    }

    /**
     * 登录处理
     * @param userName  用户名
     * @param password  登录密码
     * @param callback  这里将方法封装了一下,并使用了接口回调机制
     * */
    public void login(String userName, String password,final EMCallBack callback){
        EMClient.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
            }
            @Override
            public void onError(int code, String error) {
                if (callback != null) {
                    callback.onError(code,error);
                }
            }
            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress,status);
                }
            }
        });
    }

    /**
     * 退出登录
     * @param unbindDeviceToken 是否解绑设备token(使用GCM才有)
     * @param callback  这里将方法封装了一下,并使用了接口回调机制
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


    /*-------------------------------------------------------------*/
    /**
     * 删除会话
     * @param userName  用户名或者群聊id
     * @param deleteMessages 是否删除历史消息
     * @param callBack  这里将方法封装了一下,并使用了接口回调机制
     */
    public void deleteConversation(String userName,boolean deleteMessages,final EMCallBack callBack){
        //删除和某个user会话，如果需要保留聊天记录，传false
        //删除失败或者不存在此user的conversation返回false
        if(EMClient.getInstance().chatManager().deleteConversation(userName,deleteMessages)){
            callBack.onSuccess();
        }else {
            callBack.onError(defaultErrorCode,defaultErrorString);
        }
    }

    /**
     * 删除联系人
     * @param username  用户名
     * @param callBack  这里将方法封装了一下,并使用了接口回调机制
     */
    public void deleteContact(String username,final EMCallBack callBack){
        //执行删除好友的操作
        try {
            EMClient.getInstance().contactManager().deleteContact(username);
            callBack.onSuccess();
        } catch (HyphenateException e) {
            e.printStackTrace();
            callBack.onError(e.getErrorCode(),e.getDescription());
        }
    }

    /**
     * 创建群组
     * @param groupName  群名称
     * @param desc      群组简介
     * @param members   群组初始成员，如果只有自己传空数组即可
     * @param reason    邀请成员加入的reason
     * @param option   群组类型选项
     * @param callBack  这里将方法封装了一下,并使用了接口回调机制
     */
    public void createGroup(String groupName,String desc,String[] members,String reason,
                            EMGroupManager.EMGroupOptions option,final EMCallBack callBack){
        //执行创建群组的方法
        try {
            EMClient.getInstance().groupManager().createGroup(
                    groupName,desc,members,reason,option);
            callBack.onSuccess();
        } catch (HyphenateException e) {
            e.printStackTrace();
            callBack.onError(e.getErrorCode(),e.getDescription());
        }
    }

    /**
     * 添加联系人
     * @param userName   用户名
     * @param reason     加好友理由
     * @param callBack  这里将方法封装了一下,并使用了接口回调机制
     */
    public void addContact(String userName, String reason,final EMCallBack callBack){
        try {
            EMClient.getInstance().contactManager().addContact(userName, reason);
            callBack.onSuccess();
        } catch (HyphenateException e) {
            e.printStackTrace();
            callBack.onError(e.getErrorCode(),e.getDescription());
        }
    }



}
