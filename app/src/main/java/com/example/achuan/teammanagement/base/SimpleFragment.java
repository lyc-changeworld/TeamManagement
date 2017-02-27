package com.example.achuan.teammanagement.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by achuan on 16-10-29.
 * 无MVP的Fragment基类
 * 注意：这里使用的是android.app.Fragment,是为了和PreferenceFragment的父类保持一致
 *
 */
public abstract class SimpleFragment extends BaseFragment {

    private boolean isInited = false;

    //当碎片和活动建立关联的时候调用
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    //为碎片创建视图（加载布局）时调用
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(getLayoutId(),container,false);
        return view;
    }
    //确保与碎片相关联的活动一定已经创建完毕的时候调用
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);//初始化加载布局控件
        //如果是视图第一次创建,而且没有被挡住,才初始化事件和数据
        if (savedInstanceState == null) {
            if (!isHidden()) {
                isInited = true;//已经初始化过一次了
                initEventAndData();
            }
        }
    }
    //碎片隐藏状态改变时调用该方法(用来确保非隐藏状态时完成初始化)
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isInited && !hidden) {
            isInited = true;
            initEventAndData();
        }
    }
    //当与碎片关联的视图被移除的时候调用
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    protected abstract int getLayoutId();//添加布局文件
    protected abstract void initEventAndData();//初始化事件及数据
}
