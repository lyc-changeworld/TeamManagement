package com.example.achuan.teammanagement.ui.main.activity;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.app.Constants;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.ui.contacts.fragment.ContactsMainFragment;
import com.example.achuan.teammanagement.ui.explore.fragment.ExploreMainFragment;
import com.example.achuan.teammanagement.ui.myself.fragment.MyselfMainFragment;
import com.example.achuan.teammanagement.ui.news.fragment.NewsMainFragment;
import com.example.achuan.teammanagement.util.SharedPreferenceUtil;
import com.example.achuan.teammanagement.util.SystemUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends SimpleActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //需要装载到主活动中的Fragment的引用变量
    NewsMainFragment mNewsMainFragment;
    ContactsMainFragment mContactsMainFragment;
    ExploreMainFragment mExploreMainFragment;
    MyselfMainFragment mMyselfMainFragment;


    //定义变量记录需要隐藏和显示的fragment的编号
    private int hideFragment = Constants.TYPE_NEWS;
    private int showFragment = Constants.TYPE_NEWS;

    //记录左侧navigation的item点击
    MenuItem mLastMenuItem;//历史
    int contentViewId;//内容显示区域的控件的id号,后面用来添加碎片使用

    @BindView(R.id.btm_nav)
    BottomNavigationView mBtmNav;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData() {
        /********************检测并打开网络****************/
        SystemUtil.checkAndShowNetSettingDialog(this);
        contentViewId = R.id.fl_main_content;//获取内容容器的ID号
        /***1-初始化底部导航栏设置***/
        //初始化第一次显示的item为设置界面
        mLastMenuItem = mBtmNav.getMenu().findItem(R.id.bottom_0);
        mLastMenuItem.setChecked(true);
        //添加点击监听事件
        mBtmNav.setOnNavigationItemSelectedListener(this);
        /***2-初始化创建模块的fragment实例对象,并装载到主布局中****/
        //初始化toolbar
        setToolBar(mToolbar, (String) mLastMenuItem.getTitle(),false);
        //mToolbar.setLogo(R.drawable.logo);//设置logo
        //默认先创建第一界面
        mNewsMainFragment = new NewsMainFragment();
        //并将第一界面碎片添加到布局容器中
        replaceFragment(contentViewId, getTargetFragment(showFragment));
        SharedPreferenceUtil.setCurrentItem(showFragment);
    }



    //重写back按钮的点击事件
    @Override
    public void onBackPressed() {
        SystemUtil.showExitDialog(this);
    }


    /*-----为工具栏创建菜单选项按钮-----*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().//获得MenuInflater对象
                inflate(R.menu.menu_toolbar_main,//指定通过哪一个资源文件来创建菜单
                menu);
        return true;//返回true,表示允许创建的菜单显示出来
    }

    /*-----为菜单按钮添加点击监听事件-----*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                break;
            case R.id.add_person:
                Toast.makeText(this, "添加朋友", Toast.LENGTH_SHORT).show();
                break;
            default:break;
        }
        return true;//返回true,表示允许item点击响应
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_0:
                showFragment = Constants.TYPE_NEWS;
                //第一次加载显示时,才创建碎片对象,并添加到内容容器中
                if (mNewsMainFragment == null) {
                    mNewsMainFragment = new NewsMainFragment();
                    addFragment(contentViewId, mNewsMainFragment);
                }
                break;
            case R.id.bottom_1:
                showFragment = Constants.TYPE_CONTACTS;
                if (mContactsMainFragment == null) {
                    mContactsMainFragment = new ContactsMainFragment();
                    addFragment(contentViewId, mContactsMainFragment);
                }
                break;
            case R.id.bottom_2:
                showFragment = Constants.TYPE_EXPLORE;
                if (mExploreMainFragment == null) {
                    mExploreMainFragment = new ExploreMainFragment();
                    addFragment(contentViewId, mExploreMainFragment);
                }
                break;
            case R.id.bottom_3:
                showFragment = Constants.TYPE_MYSELF;
                if (mMyselfMainFragment == null) {
                    mMyselfMainFragment = new MyselfMainFragment();
                    addFragment(contentViewId, mMyselfMainFragment);
                }
                break;
            default:
                break;
        }
        /***点击item后进行显示切换处理,并记录在本地中***/
        if (mLastMenuItem != null && mLastMenuItem != item) {
            mToolbar.setTitle(item.getTitle());//改变标题栏的内容
            mLastMenuItem.setChecked(false);//取消历史选择
            item.setChecked(true);//设置当前item选择
            //记录当前显示的item
            SharedPreferenceUtil.setCurrentItem(showFragment);
            //实现fragment的切换显示
            showFragment(getTargetFragment(hideFragment), getTargetFragment(showFragment));
            //选择过的item变成了历史
            mLastMenuItem = item;
            //当前fragment显示完就成为历史了
            hideFragment = showFragment;
        }
        return true;
    }

    //根据item编号获取fragment对象的方法
    private Fragment getTargetFragment(int item) {
        switch (item) {
            case Constants.TYPE_NEWS:
                return mNewsMainFragment;
            case Constants.TYPE_CONTACTS:
                return mContactsMainFragment;
            case Constants.TYPE_EXPLORE:
                return mExploreMainFragment;
            case Constants.TYPE_MYSELF:
                return mMyselfMainFragment;
            default:
                break;
        }
        return mNewsMainFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
