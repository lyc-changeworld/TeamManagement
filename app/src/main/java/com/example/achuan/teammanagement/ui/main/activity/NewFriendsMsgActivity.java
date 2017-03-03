package com.example.achuan.teammanagement.ui.main.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.model.db.DBManager;
import com.example.achuan.teammanagement.model.db.InviteMessage;
import com.example.achuan.teammanagement.ui.main.adapter.NewFriendsMsgAdapter;
import com.example.achuan.teammanagement.widget.RyItemDivider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 17-3-2.
 */

public class NewFriendsMsgActivity extends SimpleActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv)
    RecyclerView mRv;

    NewFriendsMsgAdapter mNewFriendsMsgAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_new_friends_msg;
    }

    @Override
    protected void initEventAndData() {
        //设置标题工具栏
        setToolBar(mToolbar,getString(R.string.new_friends_msg),true);//有返回按钮

        List<InviteMessage> msgs= DBManager.getMessagesList();
        //创建联系人列表适配器对象实例
        mNewFriendsMsgAdapter=new NewFriendsMsgAdapter(this,msgs);

        /*---对列表的布局显示进行设置---*/
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(this);
        //设置方向(默认是垂直,下面的是水平设置)
        //linearlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv.setLayoutManager(linearlayoutManager);//为列表添加布局
        mRv.setAdapter(mNewFriendsMsgAdapter);//为列表添加适配器
        //添加自定义的分割线
        mRv.addItemDecoration(new RyItemDivider(this, R.drawable.di_item));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
