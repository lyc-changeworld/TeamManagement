package com.example.achuan.teammanagement.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.util.SharedPreferenceUtil;
import com.hyphenate.chat.EMClient;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 17-2-27.
 */

public class SplashActivity extends SimpleActivity {

    private static final int sleepTime = 2000;

    @BindView(R.id.splash_root)
    RelativeLayout mSplashRoot;
    @BindView(R.id.iv_splash)
    ImageView mIvSplash;

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initEventAndData() {
        //通过Glide来加载图片,避免图片过大造成的异常加载
        Glide.with(this).//传入上下文(Context|Activity|Fragment)
                load(R.drawable.em_splash).//加载图片,传入(URL地址｜资源id｜本地路径)
                into(mIvSplash);//将图片设置到具体某一个IV中
        //开屏设置动画
        setAnim();
        //跳转到登录界面或主界面
        jumpTo();
    }

    /*开屏动画效果*/
    private void setAnim() {
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        mSplashRoot.startAnimation(animation);
    }

    /*启动一个子线程进行延时加载,然后进行跳转*/
    private void jumpTo() {
        new Thread(new Runnable() {
            public void run() {
                if (EMClient.getInstance().isLoggedInBefore()) {
                    // ** 免登陆情况 加载所有本地群和会话
                    //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                    //加上的话保证进了主页面会话和群组都已经load完毕
                    long start = System.currentTimeMillis();//记录当前时间


                    /*-----自动登录状态下,通过存储的用户名来加载对应的数据库文件-----*/
                    String userName= SharedPreferenceUtil.getCurrentUserName();
                    /***---切换数据库文件到当前对应的用户---***/
                    /*创建一个名为xxx的数据库,而它的所有配置都会直接使用litepal.xml文件中配置的内容*/
                    LitePalDB litePalDB=LitePalDB.fromDefault(userName+"_EM");
                    LitePal.use(litePalDB);
                    //切换回litepal.xml中指定的默认数据库
                    //LitePal.useDefault();
                    //删除数据库文件
                    //LitePal.deleteDatabase("xxx");


                    //更好的办法应该是放在程序的开屏页,保证进入主页面后本地会话和群组都 load 完毕
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;//计算加载耗费的时间
                    //等待sleeptime时长
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);//接着再睡眠一段时间
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //进入主页面
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    //跳转到登录界面
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
