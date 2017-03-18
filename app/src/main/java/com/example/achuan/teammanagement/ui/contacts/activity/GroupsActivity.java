package com.example.achuan.teammanagement.ui.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.app.Constants;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.ui.contacts.adapter.GroupAdapter;
import com.example.achuan.teammanagement.ui.conversation.activity.MyChatActivity;
import com.example.achuan.teammanagement.widget.RyItemDivider;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 17-3-9.
 * 功能：展示我参与的群聊以及新建群组、搜索群等
 */

public class GroupsActivity extends SimpleActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.sw_rf)
    SwipeRefreshLayout mSwRf;


    GroupAdapter mGroupAdapter;//列表适配器
    LinearLayoutManager linearlayoutManager;//列表布局管理者
    private List<EMGroup> mEMGroupList;


    @Override
    protected int getLayout() {
        return R.layout.activity_group;
    }

    @Override
    protected void initEventAndData() {
        //设置toolbar
        setToolBar(mToolbar, getString(R.string.group_chat), true);
        mEMGroupList=new ArrayList<EMGroup>();//创建集合对象,用来存储群列表数据

        /**1-对列表的布局显示进行设置*/
        //先获取群组对象集合
        mEMGroupList.addAll(EMClient.getInstance().groupManager().getAllGroups());
        /*mSwRf.setRefreshing(true);
        refresh();*/

        mGroupAdapter = new GroupAdapter(this, mEMGroupList);
        linearlayoutManager = new LinearLayoutManager(this);
        //设置方向(默认是垂直,下面的是水平设置)
        //linearlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv.setLayoutManager(linearlayoutManager);//为列表添加布局
        mRv.setAdapter(mGroupAdapter);//为列表添加适配器
        //添加自定义的分割线
        mRv.addItemDecoration(new RyItemDivider(this, R.drawable.di_item));

        /**2-添加刷新控件的下拉刷新事件监听接口*/
        //设置颜色渐变的刷新条
        mSwRf.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        mSwRf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();//刷新列表显示
            }
        });

        /**3-添加item点击监听事件*/
        mGroupAdapter.setOnClickListener(new GroupAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int postion) {
                // enter group chat
                Intent intent = new Intent(GroupsActivity.this, MyChatActivity.class);
                //传递信息,说明该为群聊
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                //传递群组ID号,用来加载对应群组信息
                intent.putExtra(EaseConstant.EXTRA_USER_ID,
                        mEMGroupList.get(postion).getGroupId());
                startActivityForResult(intent, Constants.CHAT_REQUEST_CODE);
            }
        });

    }

    /*刷新群组集合的方法*/
    private void refresh(){
        //开启子线程进行网络端群组数据获取
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //从服务器获取自己加入的和创建的群组列表，此api获取的群组sdk会自动保存到内存和db。
                    //需异步处理
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    //获取成功后去主线程进行UI更新
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭刷新动画条
                            if(mSwRf.isRefreshing()){
                                mSwRf.setRefreshing(false);
                            }
                            mEMGroupList.clear();
                            mEMGroupList.addAll(EMClient.getInstance().groupManager().getAllGroups());
                            //刷新列表显示
                            mGroupAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭刷新动画条
                            if(mSwRf.isRefreshing()){
                                mSwRf.setRefreshing(false);
                            }
                            Toast.makeText(GroupsActivity.this,
                                    R.string.Failed_to_get_group_chat_information,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    //获取下一个活动传递过来的数据的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //核对请求码,判断数据来源
        switch (requestCode) {
            case Constants.NEW_GROUP_REQUEST_CODE:
                //判断数据处理结果是否成功,然后会拿到另外一个活动传递过来的数据
                if (resultCode == RESULT_OK) {
                    //创建群组成功后,回到列表界面时需要进行刷新操作
                    mSwRf.setRefreshing(true);
                    refresh();
                }
                break;
            default:break;
        }
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
        switch (item.getItemId()) {
            case R.id.search:
                break;
            case R.id.add_group:
                Intent intent = new Intent(this, NewGroupActivity.class);
                startActivityForResult(intent, Constants.NEW_GROUP_REQUEST_CODE);
                //从创建群组界面成功创建群组回来后,执行刷新操作
                break;
            default:
                break;
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
