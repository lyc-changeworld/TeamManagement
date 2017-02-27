package com.example.achuan.teammanagement.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.achuan.teammanagement.app.App;


/**
 * Created by achuan on 17-2-2.
 * 功能：系统相关的工具类
 */

public class SystemUtil {

    /*--------------------------1-网络相关-----------------------*/
    /**
     * 检查WIFI是否连接
     */
    public static boolean isWifiConnected() {
        //获取连接服务管理者
        ConnectivityManager connectivityManager = (ConnectivityManager) App.getInstance().
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取网络的状态
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            }
        } else {
            // not connected to the internet
        }
        return false;
    }
    /**
     * 检查手机网络(4G/3G/2G)是否连接
     */
    public static boolean isMobileNetworkConnected() {
        //获取连接服务管理者
        ConnectivityManager connectivityManager = (ConnectivityManager) App.getInstance().
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取网络的状态
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        } else {
            // not connected to the internet
        }
        return false;
    }
    /**
     * 检查是否有可用网络 true有网  false无网
     */
    public static boolean isNetworkConnected() {
        boolean isConnFlag=false;
        //获取连接服务管理者
        ConnectivityManager connectivityManager = (ConnectivityManager) App.getInstance().
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取网络的状态
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(activeNetwork!=null){
            isConnFlag= activeNetwork.isAvailable();
        }
        return isConnFlag;
    }

    /*-------------------------2-集成功能相关--------------------------*/
    /***
     * 判断当前设备的网络是否打开,并提示是否跳转到网络设置界面
     ***/
    public static void checkAndShowNetSettingDialog(final Context context){
        if(!isNetworkConnected()){
            DialogUtil.createOrdinaryDialog(context,"提示","网络未连接,请设置网络",
                    "设置网络", "暂不联网",false,//无法取消对话框(警示)
                    new DialogUtil.OnAlertDialogButtonClickListener() {
                        @Override
                        public void onRightButtonClick() {
                            // 跳转到系统的网络设置界面
                            Intent intent;
                            //判断手机系统的版本  即API大于10 就是3.0或以上版本
                            if(android.os.Build.VERSION.SDK_INT > 10 ){
                                //3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
                                intent=new Intent(android.provider.Settings.ACTION_SETTINGS);
                            }else {
                                intent = new Intent();
                                ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                                intent.setComponent(component);
                                intent.setAction("android.intent.action.VIEW");
                            }
                            context.startActivity(intent);
                        }
                        @Override
                        public void onLeftButtonClick() {
                            TastyToastUtil.warning_long(
                                    context,
                                    "不联网部分功能将无法体验! ! !");
                        }
                    });
        }
    }
    /***
     * 弹出对话框,确认是否退出App
     ***/
    public static void showExitDialog(final Context context) {
        DialogUtil.createOrdinaryDialog(context,
                "提示", "确定退出应用吗", "确定", "取消",true,//可以取消对话框(信息)
                new DialogUtil.OnAlertDialogButtonClickListener() {
                    @Override
                    public void onRightButtonClick() {
                        //点击确定后退出app
                        //将所有的活动依次出栈,然后回收所有的资源
                        App.getInstance().exitApp();
                    }
                    @Override
                    public void onLeftButtonClick() {
                        TastyToastUtil.info_short(
                                context,
                                "你差点就去了火星");
                    }
                });
    }




}
