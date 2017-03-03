package com.example.achuan.teammanagement.ui.contacts.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.base.SimpleFragment;
import com.example.achuan.teammanagement.model.db.ContactUser;
import com.example.achuan.teammanagement.model.db.DBManager;
import com.example.achuan.teammanagement.ui.contacts.adapter.ContactAdapter;
import com.example.achuan.teammanagement.ui.main.activity.NewFriendsMsgActivity;
import com.example.achuan.teammanagement.util.SystemUtil;
import com.example.achuan.teammanagement.widget.RyItemDivider;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 17-2-1.
 * 功能：
 */

public class ContactsMainFragment extends SimpleFragment {


    Context mContext;//上下文操作对象
    ContactAdapter mContactAdapter;//适配器
    Map<String, ContactUser> contactsMap;//指向本地联系人集合
    List<ContactUser> mContactUserList;//真正显示在列表中的联系人集合

    @BindView(R.id.rv)
    RecyclerView mRv;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contacts_main;
    }

    @Override
    protected void initEventAndData() {
        mContext = getActivity();
        mContactUserList = new ArrayList<ContactUser>();//创建集合对象

        /***1-先添加最顶部的"申请与通知"入口,该item点击后将跳转到申请消息界面
         * 后续还将添加群聊入口***/
        ContactUser top =new ContactUser();
        top.setUserName(getString(R.string.new_friends_msg));
        mContactUserList.add(top);//添加顶上的那个数据栏



        //获取本地联系人数据
        Map<String, ContactUser> localUsers = DBManager.getContactList();
        Collection values = localUsers.values();//获取Map集合的value集合
        mContactUserList.addAll(values);
        //如果有网,则与网络端的联系人同步,否则只加载本地联系人
        if(SystemUtil.isNetworkConnected()){
            try {
                //获取好友的 username list,开发者需要根据 username 去自己服务器获取好友的详情
                List<String> usernames = EMClient.getInstance().contactManager().
                        getAllContactsFromServer();
                for(String userName:usernames){
                    // 本地不包含此用户才执行添加操作
                    if (!localUsers.containsKey(userName)) {
                        ContactUser user=new ContactUser();
                        user.setUserName(userName);
                        //保存用户人到数据库
                        DBManager.saveContact(user);
                        mContactUserList.add(user);
                    }
                }
                /*//本地包含但网络端不包含的,就从本地删除掉,这个逻辑比较复杂,后续再实现
                if(){
                    DBManager.deleteContact(userName);
                }*/
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
        //getContactList();

        /*2-*/
        //创建联系人列表适配器对象实例
        mContactAdapter=new ContactAdapter(mContext, mContactUserList);
        /*---3-对列表的布局显示进行设置---*/
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(mContext);
        //设置方向(默认是垂直,下面的是水平设置)
        //linearlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv.setLayoutManager(linearlayoutManager);//为列表添加布局
        mRv.setAdapter(mContactAdapter);//为列表添加适配器
        //添加自定义的分割线
        mRv.addItemDecoration(new RyItemDivider(mContext, R.drawable.di_item));
        /*---4-为列表item添加点击监听事件---*/
        mContactAdapter.setOnClickListener(new ContactAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int postion) {
                if(postion==0){
                    //如果点击最顶上item,跳转到申请消息界面
                    Intent intent=new Intent(mContext, NewFriendsMsgActivity.class);
                    startActivity(intent);
                }else {
                    //点击联系人跳转到对应人的聊天界面

                }
            }
        });
        /*mContactAdapter.setOnLongClickListener(new ContactAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View view, final int postion) {
                if(postion==0){

                }else {
                    //长按可以删除联系人
                    DialogUtil.createOrdinaryDialog(mContext, "",
                            "是否删除联系人", "确定", "取消",
                            true, new DialogUtil.OnAlertDialogButtonClickListener() {
                                @Override
                                public void onRightButtonClick() {
                                    try {
                                        String useName=mContactUserList.get(postion).getUserName();
                                        //执行删除好友操作
                                        EMClient.getInstance().contactManager().
                                                deleteContact(useName);
                                    } catch (HyphenateException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onLeftButtonClick() {

                                }
                            });
                }
            }
        });*/
    }

    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    protected void getContactList() {
        //mContactUserList.clear();
        //先获取本地联系人数据集合
        contactsMap= DBManager.getContactList();
        if (contactsMap.isEmpty()) {
            return;
        }
        synchronized (this.contactsMap) {
            //使用迭代器,主要用来遍历序列中的对象
            Iterator<Map.Entry<String, ContactUser>> iterator =
                    contactsMap.entrySet().iterator();
            //获取黑名单用户名集合
            List<String> blackList =
                    EMClient.getInstance().contactManager().getBlackListUsernames();
            //迭代器用于while循环
            while (iterator.hasNext()) {
                //拿到下一个对象
                Map.Entry<String, ContactUser> entry = iterator.next();
                /*// 兼容以前的通讯录里的已有的数据显示，加上此判断，如果是新集成的可以去掉此判断
                if (!entry.getKey().equals("item_new_friends")
                        && !entry.getKey().equals("item_groups")
                        && !entry.getKey().equals("item_chatroom")
                        && !entry.getKey().equals("item_robots")) {

                }*/
                if (!blackList.contains(entry.getKey())) {
                    //不显示黑名单中的用户
                    ContactUser user = entry.getValue();
                    //设置昵称,后续再实现
                    //EaseCommonUtils.setUserInitialLetter(user);
                    mContactUserList.add(user);
                }
            }
        }
        /*// 排序
        Collections.sort(mContactUserList, new Comparator<ContactUser>() {
            @Override
            public int compare(ContactUser o1, ContactUser o2) {
                if (o1.getInitialLetter().equals(o2.getInitialLetter())) {
                    return o1.getNick().compareTo(o2.getNick());
                } else {
                    if ("#".equals(o1.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(o2.getInitialLetter())) {
                        return -1;
                    }
                    return o1.getInitialLetter().compareTo(o2.getInitialLetter());
                }
            }
        });*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
