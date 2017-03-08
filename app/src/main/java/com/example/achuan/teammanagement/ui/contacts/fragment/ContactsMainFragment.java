package com.example.achuan.teammanagement.ui.contacts.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.app.Constants;
import com.example.achuan.teammanagement.base.SimpleFragment;
import com.example.achuan.teammanagement.model.db.ContactUser;
import com.example.achuan.teammanagement.model.db.DBManager;
import com.example.achuan.teammanagement.ui.contacts.activity.NewFriendsMsgActivity;
import com.example.achuan.teammanagement.ui.contacts.adapter.ContactAdapter;
import com.example.achuan.teammanagement.ui.news.activity.ChatActivity;
import com.example.achuan.teammanagement.util.DialogUtil;
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


    @BindView(R.id.rv)
    RecyclerView mRv;

    Context mContext;//上下文操作对象
    ContactAdapter mContactAdapter;//适配器
    Map<String, ContactUser> contactsMap;//指向本地联系人集合
    List<ContactUser> mContactUserList;//真正显示在列表中的联系人集合


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

        //初始化获取本地联系人数据
        Map<String, ContactUser> localUsers = DBManager.getContactList();
        Collection values = localUsers.values();//获取Map集合的value集合
        mContactUserList.addAll(values);
        //getContactList();//过滤黑名单及排序

        /*2-*/
        //创建联系人列表适配器对象实例
        mContactAdapter=new ContactAdapter(mContext, mContactUserList);
        /***3-对列表的布局显示进行设置***/
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(mContext);
        //设置方向(默认是垂直,下面的是水平设置)
        //linearlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv.setLayoutManager(linearlayoutManager);//为列表添加布局
        mRv.setAdapter(mContactAdapter);//为列表添加适配器
        //添加自定义的分割线
        mRv.addItemDecoration(new RyItemDivider(mContext, R.drawable.di_item));
        /***4-为列表item添加点击监听事件***/
        //点击事件
        mContactAdapter.setOnClickListener(new ContactAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int postion) {
                if(postion==0){
                    //如果点击最顶上item,跳转到申请消息界面
                    Intent intent=new Intent(mContext, NewFriendsMsgActivity.class);
                    startActivity(intent);
                }else {
                    //点击联系人跳转到对应人的聊天界面
                    Intent intent=new Intent(mContext,ChatActivity.class);
                    //发送联系人的名称到聊天界面
                    intent.putExtra(Constants.EXTRA_USER_ID,
                            mContactUserList.get(postion).getUserName());
                    startActivity(intent);
                }
            }
        });
        //长按事件
        mContactAdapter.setOnLongClickListener(new ContactAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View view, final int postion) {
                //屏蔽掉最顶端的item
                if(postion!=0){
                    //获取对象名
                    final String username=mContactUserList.get(postion).getUserName();
                    //创建对话框
                    final Dialog dialog= DialogUtil.createMyselfDialog(mContext,
                            R.layout.dlg_two,//资源id号
                            Gravity.CENTER);//布局位置
                    //获取布局中的控件对象
                    TextView mTvDeleteContact= (TextView) dialog.findViewById(R.id.tv_one);
                    TextView mTvAddToBlackList= (TextView) dialog.findViewById(R.id.tv_two);
                    //为控件设置文本
                    mTvDeleteContact.setText(getString(R.string.Delete_contactUser));
                    mTvAddToBlackList.setText(getString(R.string.Add_to_blackList));
                    //有两种选择：
                    //1.删除联系人
                    mTvDeleteContact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteContact(username,postion);//前者用来删除联系人,后者更新列表
                            dialog.dismiss();//记得关闭对话框
                        }
                    });
                    //2.移入到黑名单
                    mTvAddToBlackList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //addToBlackList(username,postion);//前者用来移入黑名单,后者更新列表
                            dialog.dismiss();//记得关闭对话框
                        }
                    });
                }
            }
        });
    }

    //1-删除联系人的方法
    private void deleteContact(final String username,final int postion){
        //创建加载进度框
        DialogUtil.createProgressDialog(mContext,null,
                getString(R.string.Are_delete_with),//正在删除
                false,false);//对话框无法被取消
        //开启子线程进行删除操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //执行删除好友的操作
                    EMClient.getInstance().contactManager().deleteContact(username);
                    //本地数据库删除联系人
                    DBManager.deleteContact(username);
                    /*回到主线程进行UI更新*/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(DialogUtil.isProgressDialogShowing()){
                                DialogUtil.closeProgressDialog();
                            }
                            //刷新列表显示
                            mContactUserList.remove(postion);
                            //调用下面的方法进行数据刷新比较保险,
                            //之前用notifyItemRemoved(postion)时出现了数据更新不及时的情况
                            mContactAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //2-添加到黑名单
    private void addToBlackList(final String username,final int postion){
        //创建加载进度框
        DialogUtil.createProgressDialog(mContext,null,
                getString(R.string.Are_delete_with),//正在删除
                false,false);//对话框无法被取消
        //从服务器获取黑名单列表
        //EMClient.getInstance().contactManager().getBlackListFromServer();
        //从本地db获取黑名单列表
        //EMClient.getInstance().contactManager().getBlackListUsernames();
        //开启子线程进行操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //第二个参数如果为true，则把用户加入到黑名单后双方发消息时对方都收不到；
                    // false，则我能给黑名单的中用户发消息，但是对方发给我时我是收不到的
                    EMClient.getInstance().contactManager().addUserToBlackList(username,true);
                    //本地数据库删除联系人
                    DBManager.deleteContact(username);
                    /*回到主线程进行UI更新*/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(DialogUtil.isProgressDialogShowing()){
                                DialogUtil.closeProgressDialog();
                            }
                            //刷新列表显示
                            mContactUserList.remove(postion);
                            //调用下面的方法进行数据刷新比较保险,
                            //之前用notifyItemRemoved(postion)时出现了数据更新不及时的情况
                            mContactAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
        }).start();
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
