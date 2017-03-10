package com.example.achuan.teammanagement.ui.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.base.SimpleActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 17-3-9.
 * 功能：展示我参与的群聊以及新建群组、搜索群等
 */

public class GroupChatActivity extends SimpleActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv)
    RecyclerView mRv;

    @Override
    protected int getLayout() {
        return R.layout.activity_group_chat;
    }

    @Override
    protected void initEventAndData() {
        //设置toolbar
        setToolBar(mToolbar,getString(R.string.group_chat),true);


        //设置颜色渐变的刷新条
        /*swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);*/




    }


    /*-----为工具栏创建菜单选项按钮-----*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().//获得MenuInflater对象
                inflate(R.menu.menu_toolbar_group_chat,//指定通过哪一个资源文件来创建菜单
                menu);
        return true;//返回true,表示允许创建的菜单显示出来
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                break;
            case R.id.add_group:
                Intent intent=new Intent(this,NewGroupActivity.class);
                startActivity(intent);
                break;
            default:break;
        }
        return true;//返回true,表示允许item点击响应
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
