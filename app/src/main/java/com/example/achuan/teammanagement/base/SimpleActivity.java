package com.example.achuan.teammanagement.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;

/**
 * Created by achuan on 16-10-29.
 * 功能：无MVP的activity基类
 */
public abstract class SimpleActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());//设置布局文件
        ButterKnife.bind(this);//初始化加载控件
        initEventAndData();//初始化事件和数据
    }




    //设置接口方法,具体功能让子类来实现
    protected abstract int getLayout();
    protected abstract void initEventAndData();
}
