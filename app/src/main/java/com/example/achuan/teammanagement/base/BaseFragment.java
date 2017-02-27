package com.example.achuan.teammanagement.base;

import android.app.Fragment;

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

}
