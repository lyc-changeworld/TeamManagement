package com.example.achuan.teammanagement.ui.main.activity;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.app.Constants;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.model.db.ContactUser;
import com.example.achuan.teammanagement.model.db.DBManager;
import com.example.achuan.teammanagement.model.db.InviteMessage;
import com.example.achuan.teammanagement.ui.contacts.fragment.ContactsMainFragment;
import com.example.achuan.teammanagement.ui.conversation.fragment.ConversationMainFragment;
import com.example.achuan.teammanagement.ui.explore.fragment.ExploreMainFragment;
import com.example.achuan.teammanagement.ui.myself.fragment.MyselfMainFragment;
import com.example.achuan.teammanagement.util.SharedPreferenceUtil;
import com.example.achuan.teammanagement.util.StringUtil;
import com.example.achuan.teammanagement.util.SystemUtil;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.achuan.teammanagement.model.db.DBManager.deleteMessage;

public class MainActivity extends SimpleActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //需要装载到主活动中的Fragment的引用变量
    ConversationMainFragment mNewsMainFragment;
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

    EMContactListener mEMContactListener;


    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData() {
        /********************检测并打开网络****************/
        //SystemUtil.checkAndShowNetSettingDialog(this);
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
        mNewsMainFragment = new ConversationMainFragment();
        //并将第一界面碎片添加到布局容器中
        replaceFragment(contentViewId, getTargetFragment(showFragment));
        SharedPreferenceUtil.setCurrentItem(showFragment);

        /***3-注册联系人变动监听***/
        mEMContactListener=new MyContactListener();
        EMClient.getInstance().contactManager().setContactListener(mEMContactListener);

    }

    /***
     * 自定义好友变化listener类
     */
    public class MyContactListener implements EMContactListener{
        @Override
        public void onContactAdded(final String username) {
            //增加了联系人时回调此方法
            /*---保存增加的联系人---*/
            //获取本地联系人数据
            Map<String, ContactUser> localUsers = DBManager.getContactList();
            // 添加好友时可能会回调added方法两次
            if (!localUsers.containsKey(username)) {
                //创建实例对象
                ContactUser user = new ContactUser();
                user.setUserName(username);
                user.setInitialLetter(StringUtil.getHeadChar(username));
                //进行查询操作,避免重复添加
                DBManager.saveContact(user);
            }
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "增加联系人：+"+username,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void onContactDeleted(final String username) {
            //被删除时回调此方法
            //获取本地联系人数据
            Map<String, ContactUser> localUsers = DBManager.getContactList();
            // 本地包含此用户才执行删除操作
            if (localUsers.containsKey(username)) {
                //从本地数据库中删除
                DBManager.deleteContact(username);
                DBManager.deleteMessage(username);
            }
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "删除联系人：+"+username, Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void onContactInvited(final String username, String reason) {
            //收到邀请

            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = DBManager.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                //如果之前发的消息不是群邀请(好友邀请)且发起人名和当前发起人名相同,就把历史消息删除掉
                /*当前是无群聊的情况,后面引入群聊后还需进行大量优化*/
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    deleteMessage(username);
                }
            }

            //自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());

            /**-在消息源头进行特殊处理,提升用户体验*/
            if(reason ==null|| TextUtils.isEmpty(reason)){
                //请求加你为好友(默认显示的加好友理由)
                reason=getString(R.string.Request_to_add_you_as_a_friend);
            }

            msg.setReason(reason);
            // 设置相应status对应的序数
            msg.setStatusOrdinal(InviteMessage.InviteMesageStatus.BEINVITEED.ordinal());
            notifyNewIviteMessage(msg);

            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "收到好友申请：+"+username,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void onFriendRequestAccepted(final String username) {
            //好友请求被同意
            List<InviteMessage> msgs = DBManager.getMessagesList();

            /***---注意：下面的逻辑有待改善！！！-***/
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }

            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setStatusOrdinal(InviteMessage.InviteMesageStatus.BEAGREED.ordinal());
            //存储同意的消息
            notifyNewIviteMessage(msg);
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "好友申请同意：+"+username, Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void onFriendRequestDeclined(String username) {
            //好友请求被拒绝
            // 参考同意，被邀请实现此功能,demo未实现
            //Log.d(username, username + "拒绝了你的好友请求");
        }
    }

    /***
     * 在活动销毁时移除联系人监听器
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().contactManager().removeContactListener(mEMContactListener);
    }

    /**
     * 保存并提示消息的邀请消息
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg){
        /*if(inviteMessgeDao == null){
            inviteMessgeDao = new InviteMessgeDao(MainActivity.this);
        }
        inviteMessgeDao.saveMessage(msg);
        //保存未读数，这里没有精确计算
        inviteMessgeDao.saveUnreadMessageCount(1);
        // 提示有新消息
        //响铃或其他操作*/
        DBManager.saveMessage(msg);

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
                Intent intent=new Intent(this,AddContactActivity.class);
                startActivity(intent);
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
                    mNewsMainFragment = new ConversationMainFragment();
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
