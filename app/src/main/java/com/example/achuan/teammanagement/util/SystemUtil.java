package com.example.achuan.teammanagement.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.achuan.teammanagement.R;
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

    /*跳转到网络设置界面的方法*/
    public static void setNetWork(Context context){
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


    /***
     * 判断当前设备的网络是否打开,并提示是否跳转到网络设置界面
     ***/
    public static void checkAndShowNetSettingDialog(final Context context){
        final Resources resources=context.getResources();
        final String str1=resources.getString(
                R.string.warn_bad_consequence_of_not_connect_network);
        if(!isNetworkConnected()){
            DialogUtil.createOrdinaryDialog(context,
                    resources.getString(R.string.warn),
                    resources.getString(R.string.warn_to_set_network),
                    resources.getString(R.string.set_network),
                    resources.getString(R.string.temporarily_not_connect_network),
                    false,//设置为无法取消对话框(警示)
                    new DialogUtil.OnAlertDialogButtonClickListener() {
                        @Override
                        public void onRightButtonClick() {
                            setNetWork(context);
                        }
                        @Override
                        public void onLeftButtonClick() {
                            /*TastyToastUtil.warning_long(
                                    context,
                                    resources.getString(
                                            R.string.warn_bad_consequence_of_not_connect_network
                                    ));*/
                            Toast.makeText(context, str1,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /***
     * 弹出对话框,确认是否退出App
     ***/
    public static void showExitDialog(final Context context) {
        final Resources resources=context.getResources();
        DialogUtil.createOrdinaryDialog(context,
                resources.getString(R.string.warn),
                resources.getString(R.string.whether_or_not_to_exitApp),
                resources.getString(R.string.confirm),
                resources.getString(R.string.cancel),
                true,//可以取消对话框(信息)
                new DialogUtil.OnAlertDialogButtonClickListener() {
                    @Override
                    public void onRightButtonClick() {
                        //点击确定后退出app
                        //将所有的活动依次出栈,然后回收所有的资源
                        App.getInstance().exitApp();
                    }
                    @Override
                    public void onLeftButtonClick() {
                        /*TastyToastUtil.info_short(
                                context,
                                resources.getString(R.string.almost_exitApp));*/
                    }
                });
    }




}
