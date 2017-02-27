package com.example.achuan.teammanagement.ui.news.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.base.SimpleFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 17-2-1.
 * 功能：
 */

public class NewsMainFragment extends SimpleFragment {

    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.sw_rf)
    SwipeRefreshLayout mSwRf;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_main;
    }

    @Override
    protected void initEventAndData() {






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
