package com.example.achuan.teammanagement.base;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by achuan on 17-2-18.
 * 功能：将MvpFragment和SimpleFragment的耦合的方法放到它们父类中来,
 *      避免了代码的重复
 */

public class BaseFragment extends Fragment {

    /***---动态添加碎片到位置区域---
     * 用replace的效果就是：切换fragment时每次都会重新创建初始化。
     * ***/
    protected void replaceFragment(int viewId, Fragment fragment) {
        //在Fragment中获取FragmentManager时,就用下面的方法,否则用getSupportFragmentManager()
        //getChildFragmentManager().beginTransaction().replace(viewId,fragment).commit();
        //getSupportFragmentManager().beginTransaction().replace(viewId,fragment).commit();
        //对于android.app.Fragment使用下面的方法,否则用上面的两种方法
        getFragmentManager().beginTransaction()//开启事务
                .replace(viewId,fragment)//kill之前的碎片,并初始化加载当前碎片
                .commit();//提交事务
    }

    /***---添加碎片到内容区域中---***/
    protected void addFragment(int viewId, Fragment fragment){
        getFragmentManager().beginTransaction()//开启事务
                .add(viewId,fragment)//添加
                .commit();//提交事务
    }
    /***隐藏之前的以及显示当前的item对应的Fragment***/
    protected void showFragment( Fragment hideFragment,Fragment showFragment){
        getFragmentManager().beginTransaction()//开启事务
                .hide(hideFragment)//隐藏
                .show(showFragment)//显示
                .commit();//提交事务
    }

    //设置toolbar的方法,子类可以直接调用该方法
    protected void setToolBar(final Context mContext, Toolbar toolbar, String title, boolean isSetIcon) {
        //设置标题名称
        toolbar.setTitle(title);
        //用自定义的toolbar取代原始的toolbar
        ((AppCompatActivity) mContext).setSupportActionBar(toolbar);
        if(isSetIcon){
            //给左上角图标的左边加上一个返回的图标,默认设置为false(不显示)
            ((AppCompatActivity) mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(isSetIcon);
            //使左上角图标是否显示，如果设成false，则没有程序图标，仅仅就个标题，否则，显示应用程序图标
            //((AppCompatActivity)).setDisplayShowHomeEnabled(true);
            //对NavigationIcon添加点击
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AppCompatActivity) mContext).onBackPressed();
                }
            });
        }

    }

}
