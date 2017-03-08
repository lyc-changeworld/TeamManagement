package com.example.achuan.teammanagement.ui.news.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.app.Constants;
import com.example.achuan.teammanagement.base.SimpleFragment;
import com.example.achuan.teammanagement.ui.news.activity.ChatActivity;
import com.example.achuan.teammanagement.ui.news.adapter.ConversationAdapter;
import com.example.achuan.teammanagement.util.DialogUtil;
import com.example.achuan.teammanagement.util.LogUtil;
import com.example.achuan.teammanagement.util.SharedPreferenceUtil;
import com.example.achuan.teammanagement.widget.RyItemDivider;
import com.hyphenate.EMConversationListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 17-2-1.
 * 功能：
 */

public class NewsMainFragment extends SimpleFragment {

    @BindView(R.id.rv)
    RecyclerView mRv;

    Context mContext;
    private List<EMConversation> mEMConversationList;
    LinearLayoutManager linearlayoutManager;
    ConversationAdapter mConversationAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_main;
    }

    @Override
    protected void initEventAndData() {
        mContext=getActivity();//获取到上下文对象

        /*1-初始化加载会话集合*/
        //创建会话集合体对象
        mEMConversationList=new ArrayList<EMConversation>();
        /*//获取到排好序的会话对象集合
        mEMConversationList.addAll(loadConversationList());*/

        /***2-列表适配及布局初始化的设置***/
        //创建适配器对象
        mConversationAdapter=new ConversationAdapter(mContext,mEMConversationList);
        //对列表的布局显示进行设置
        linearlayoutManager = new LinearLayoutManager(mContext);
        //设置方向(默认是垂直,下面的是水平设置)
        //linearlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv.setLayoutManager(linearlayoutManager);//为列表添加布局
        mRv.setAdapter(mConversationAdapter);//为列表添加适配器
        //添加自定义的分割线
        mRv.addItemDecoration(new RyItemDivider(mContext, R.drawable.di_item));

        /***3-为item的点击设置监听事件***/
        /*设置点击会话item的后跳转到聊天界面的功能*/
        mConversationAdapter.setOnClickListener(new ConversationAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int postion) {
                EMConversation conversation = mEMConversationList.get(postion);
                //获取到会话的对象名称
                String username = conversation.conversationId();
                //不能和自己聊天哟
                if (username.equals(SharedPreferenceUtil.getCurrentUserName()))
                    //提示:不能和自己聊
                    Toast.makeText(mContext, getString(R.string.Cant_chat_with_yourself),
                            Toast.LENGTH_SHORT).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    //如果是群聊天
                    if(conversation.isGroup()){
                        if(conversation.getType() == EMConversation.EMConversationType.ChatRoom){
                            // it's group chat
                            intent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_CHATROOM);
                        }else{
                            intent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_GROUP);
                        }
                    }
                    // it's single chat
                    intent.putExtra(Constants.EXTRA_USER_ID, username);
                    startActivity(intent);
                }
            }
        });
        /*设置长按删除会话的功能*/
        mConversationAdapter.setOnLongClickListener(new ConversationAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View view, final int postion) {
                final Dialog dialog=DialogUtil.createMyselfDialog(mContext,
                        R.layout.dlg_two,//资源id号
                        Gravity.CENTER);//布局位置
                //获取布局中的控件对象
                TextView mTvDeleteConver= (TextView) dialog.findViewById(R.id.tv_delete_conver);
                TextView mTvDeleteAll= (TextView) dialog.findViewById(R.id.tv_delete_all);
                //删除会话
                mTvDeleteConver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteConversation(postion,false);
                        dialog.dismiss();
                    }
                });
                //删除会话和消息(会将聊天消息全部删除)
                mTvDeleteAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteConversation(postion,true);
                        dialog.dismiss();
                    }
                });
            }
        });


        //添加会话监听器
        EMClient.getInstance().chatManager().addConversationListener(mEMConversationListener);
    }

    /*3-创建对话监听器实例对象*/
    EMConversationListener mEMConversationListener=new EMConversationListener() {
        @Override
        public void onCoversationUpdate() {
            /*触发该监听的情况：
            * 1、删除某个会话时
            * 2、删除某个会话后,第一次重新进入该会话聊天界面时
            * 3、删除某个会话和全部消息后,只要消息数是0,以后每次打开应用后第一次进入该聊天界面时*/
            LogUtil.d("what_happened","会话更新了...");

        }
    };

    /*2-删除会话的方法*/
    private void deleteConversation(final int postion, final boolean isDeleteNews){
        //创建加载进度框
        DialogUtil.createProgressDialog(mContext,null,
                getString(R.string.Are_delete_with),//正在删除
                false,false);//对话框无法被取消
        //开启子线程进行删除操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //删除和某个user会话，如果需要保留聊天记录，传false
                EMClient.getInstance().chatManager().deleteConversation(
                        mEMConversationList.get(postion).conversationId(),
                        isDeleteNews);//false代表不删除消息,true则相反
                /*回到主线程进行UI更新*/
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(DialogUtil.isProgressDialogShowing()){
                            DialogUtil.closeProgressDialog();
                        }
                        //刷新列表显示
                        mEMConversationList.remove(postion);
                        //调用下面的方法进行数据刷新比较保险,
                        //之前用notifyItemRemoved(postion)时出现了数据更新不及时的情况
                        mConversationAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    /**
     * 1-获取会话列表
     * @return List<EMConversation>
     */
    protected List<EMConversation> loadConversationList() {
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().
                getAllConversations();
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
         */
        //存储会话对象和对应最后一条消息的时间到集合体中,方便后面根据时间来进行会话列表排序
        List<Pair<Long, EMConversation>> sortList =
                new ArrayList<Pair<Long, EMConversation>>();
        //使用同步锁,避免并发问题
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                // 过滤掉messages size为0的conversation
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(
                            new Pair<Long, EMConversation>(
                                    conversation.getLastMessage().getMsgTime(),//最后一条消息的时间
                                    conversation));
                }
            }
        }
        try {
            //对集合中的对象根据最后一条消息的时间进行排序
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }
    
    /**
     * 根据最后一条消息的时间排序
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        //排序操作
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            //定义排序规则
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {
                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }


    //从其它的activity返回mainActivity时是回到焦点,此时更新列表较为合理
    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    //在碎片处于非遮挡状态时重新刷新消息列表显示
    //经实验,从mainActivity跳转到其它activity并不是碎片被遮挡,只有在单个活动中切换碎片时才叫遮挡
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //非遮挡状态时刷新显示
        if(!hidden){
            refresh();
        }
    }

    /*刷新列表显示*/
    private void refresh(){
        //重新加载显示列表
        mEMConversationList.clear();
        mEMConversationList.addAll(loadConversationList());
        mConversationAdapter.notifyDataSetChanged();
    }

    /*碎片销毁时记得移除监听*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //碎片销毁时移除监听
        EMClient.getInstance().chatManager().removeConversationListener(mEMConversationListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

}
