package com.example.achuan.teammanagement.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;

/**
 * Created by achuan on 16-10-24.
 * 功能：MVP activity基类
 */
public abstract class MvpActivity<T extends BasePresenter> extends BaseActivity implements BaseView{

    protected T mPresenter;//引入操作者

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());//设置布局文件
        ButterKnife.bind(this);//初始化控件加载
        mPresenter=createPresenter();//创建操作者实例对象
        if(mPresenter!=null){
            mPresenter.attachView(this);//建立关联(prsenter持有activity对象)
        }
        initEventAndData();//初始化事件和数据操作
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter!=null){
            mPresenter.detachView();//取消关联(prsenter不再持有activity对象)
        }
    }


    protected abstract T createPresenter();//创建操作者实例
    protected abstract int getLayout();//添加布局文件
    protected abstract void initEventAndData();//初始化事件及数据
}
