package com.example.achuan.teammanagement.util;

import android.content.Context;
import android.content.Intent;
import com.example.achuan.teammanagement.ui.main.activity.RegisterActivity;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * Created by achuan on 16-10-27.
 * 功能：Mob功能类（短信验证）
 */
public class MobUtil {

    //创建一个注册界面实例对象
    public static RegisterPage registerPage = new RegisterPage();
    /*1-执行注册流程的方法*/
    public static void registerBySms(final Context context){
        //打开注册页面
        registerPage.setRegisterCallback(new EventHandler() {
            @Override
            public void afterEvent(int i, int i1, Object o) {
                super.afterEvent(i, i1, o);
                // 解析注册结果
                if (i1 == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) o;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");
                    /*跳转到密码设置界面*/
                    Intent intent=new Intent(context, RegisterActivity.class);
                    //携带电话号码过去,用电话号码进行注册
                    intent.putExtra("phone",phone);
                    context.startActivity(intent);
                    //注册成功后才结束之前的activity
                    // 提交用户信息（此方法可以不调用,）
                    // 该方法体由自己实现,将验证成功的号码信息发送给服务器
                    //registerUser(country, phone);
                }
            }
        });
        registerPage.show(context);
    }


}
