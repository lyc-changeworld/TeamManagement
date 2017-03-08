package com.example.achuan.teammanagement.ui.news.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achuan.teammanagement.EaseCommonUtils;
import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.app.Constants;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.ui.news.adapter.ChatMessageAdapter;
import com.example.achuan.teammanagement.util.DialogUtil;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.achuan.teammanagement.R.id.et_content;

/**
 * Created by achuan on 17-3-4.
 * 功能：聊天界面
 * 问题：弹出键盘时toolbar被拉伸(已解决)  输入栏被遮挡(已解决)、对话列表被遮挡(未解决)
 * 参考链接：
 * http://stackoverflow.com/questions/31824476/toolbar-expanding-on-clicking-edittext-view
 * http://www.itdadao.com/articles/c15a896067p0.html
 */

public class ChatActivity extends SimpleActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(et_content)
    EditText mEtContent;
    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.btn_send)
    Button mBtnSend;
    @BindView(R.id.sw_rf)
    SwipeRefreshLayout mSwRf;

    private int chatType = 1;
    private String toChatUsername;//聊天对象名称
    private List<EMMessage> mEMMessageList;//聊天记录集合
    private EMConversation mEMConversation;
    protected int pagesize = 20;//初始化加载的消息条数
    protected int pageMore=10;//下拉刷新,多加载10条消息
    //下拉刷新后,可以加载更多消息

    Context mContext;
    LinearLayoutManager linearlayoutManager;
    ChatMessageAdapter mChatMessageAdapter;


    @Override
    protected int getLayout() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initEventAndData() {
        mContext=this;
        /*1-获取到当前聊天的联系人的名称*/
        toChatUsername = this.getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        //设置标题工具栏
        setToolBar(mToolbar, toChatUsername, true);
        /***2-为Edit输入框添加输入监听类,实现合理的效果***/
        //对只有在用户名和密码的输入都不为空的情况下，button按钮才显示有效，
        // 可以自己构造一个TextChange的类，实现一个TextWatcher接口，
        // 里面有三个函数可以实现对所有text的监听。
        TextChange textChange = new TextChange();
        mEtContent.addTextChangedListener(textChange);

        /***3-将消息适配显示到列表中***/
        initMessage();//初始化加载消息
        mEMMessageList = mEMConversation.getAllMessages();//获取到消息集合体
        //创建适配器对象
        mChatMessageAdapter = new ChatMessageAdapter(mContext, mEMMessageList);
        //对列表的布局显示进行设置
        linearlayoutManager = new LinearLayoutManager(mContext);
        //列表的焦点切换到消息列表的最后一条
        linearlayoutManager.scrollToPosition(mChatMessageAdapter.getItemCount() - 1);
        //设置方向(默认是垂直,下面的是水平设置)
        //linearlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv.setLayoutManager(linearlayoutManager);//为列表添加布局
        mRv.setAdapter(mChatMessageAdapter);//为列表添加适配器
        /*添加刷新控件的下拉刷新事件监听接口*/
        mSwRf.setColorSchemeResources(R.color.colorAccent);//刷新条的颜色
        mSwRf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMoreMessage();
            }
        });

        /***4-为文本内容控件添加点击监听事件***/
        //注意:该监听方法是在适配器类中自定义的
        mChatMessageAdapter.setContentOnLongClickListener(new ChatMessageAdapter.ContentOnLongClickListener() {
            @Override
            public void onLongClick(View view, final int postion) {

                //final EMMessage emMessage=mEMMessageList.get(postion);
                //创建对话框
                final Dialog dialog= DialogUtil.createMyselfDialog(ChatActivity.this,
                        R.layout.dlg_two,//资源id号
                        Gravity.CENTER);//布局位置
                //获取布局中的控件对象
                TextView mTvDeleteContact= (TextView) dialog.findViewById(R.id.tv_one);
                TextView mTvAddToBlackList= (TextView) dialog.findViewById(R.id.tv_two);
                //为控件设置文本
                mTvDeleteContact.setText(getString(R.string.Delete_message));//删除消息
                mTvAddToBlackList.setText(getString(R.string.Repeat));//转发
                //有两种选择：
                //1.删除消息
                mTvDeleteContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMessage(postion);//删除消息操作
                        dialog.dismiss();//记得关闭对话框
                    }
                });
                //2.转发
                mTvAddToBlackList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到联系人界面,选择想要发送的对象
                        dialog.dismiss();//记得关闭对话框
                    }
                });
            }
        });

        //添加聊天消息监听
        EMClient.getInstance().chatManager().addMessageListener(mEMMessageListener);
        /*
        * 记得在不需要的时候移除listener，如在activity的onDestroy()时
          EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        * */
    }

    /*5-删除某条消息记录*/
    private void deleteMessage(final int postion){

        final EMMessage deleteMsg=mEMMessageList.get(postion);
        //创建加载进度框
        DialogUtil.createProgressDialog(mContext,null,
                getString(R.string.Are_delete_with),//正在删除
                false,false);//对话框无法被取消
        //开启子线程进行删除操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                mEMConversation.removeMessage(deleteMsg.getMsgId());
                /*回到主线程进行UI更新*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(DialogUtil.isProgressDialogShowing()){
                            DialogUtil.closeProgressDialog();
                        }
                        //刷新列表显示
                        mEMMessageList.remove(postion);
                        //调用下面的方法进行数据刷新比较保险,
                        //之前用notifyItemRemoved(postion)时出现了数据更新不及时的情况
                        mChatMessageAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();


    }

    /*4-下拉刷新加载更多的消息*/
    private void getMoreMessage() {

        List<EMMessage> msgs = mEMConversation.getAllMessages();//当前会话的消息集合

        int msgAllCount=mEMConversation.getAllMsgCount();//全部消息的个数
        int msgCurrentCount = msgs != null ? msgs.size() : 0;//当前消息的个数
        List<EMMessage> moreMessages=null;//存储加载出来的更多消息
        String msgId = null;//获取最顶上的msg的id号

        if(msgCurrentCount>0){
            msgId=msgs.get(0).getMsgId();
            //去服务器加载消息出来
            if((msgAllCount-msgCurrentCount)>=pageMore){
                //如果剩下消息数大于pageMore
                //从最上面的消息位置开始,加载剩下数量的消息到本地对象来
                moreMessages=mEMConversation.loadMoreMsgFromDB(msgId, pageMore);
            }else if((msgAllCount-msgCurrentCount)>0){
                //剩余消息不到pageMore条,加载获取剩下的全部消息
                moreMessages=mEMConversation.loadMoreMsgFromDB(msgId,msgAllCount-msgCurrentCount );
                Toast.makeText(this, "最后几条消息了...",
                        Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "没有更多消息了...",
                        Toast.LENGTH_SHORT).show();
            }
            /*将加载出来的消息添加显示到列表中*/
            if(moreMessages!=null){
                //添加消息到集合的首部
                mEMMessageList.addAll(0,moreMessages);
                mChatMessageAdapter.notifyDataSetChanged();//刷新列表
                linearlayoutManager.scrollToPosition(0);
            }
        }else {
            //当前会话界面没有消息,说明消息记录被全部删除了,此时msgAllCount=msgCurrentCount=0;
            Toast.makeText(this, "没有任何消息记录...",
                    Toast.LENGTH_SHORT).show();
        }
        //关闭刷新动画条
        if(mSwRf.isRefreshing()){
            mSwRf.setRefreshing(false);
        }
    }

    /*3-注册消息监听来接收消息*/
    EMMessageListener mEMMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(final List<EMMessage> messages) {
            //LogUtil.d("aaaaaaaaaa","收到消息");
            /*回到主线程来进行UI更新,否则不会自动更新列表(需要碰一下列表)*/
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 收到消息
                    for (EMMessage message : messages) {
                        String username = null;
                        // 群组消息
                        if (message.getChatType() == EMMessage.ChatType.GroupChat ||
                                message.getChatType() == EMMessage.ChatType.ChatRoom) {
                            username = message.getTo();
                        } else {
                            // 单聊消息
                            username = message.getFrom();
                        }
                        // 如果是当前会话的消息，刷新聊天页面
                        if (username.equals(toChatUsername)) {
                            mEMMessageList.add(message);
                            mChatMessageAdapter.notifyDataSetChanged();
                            //mChatMessageAdapter.notifyItemInserted(mEMMessageList.size()-1);
                            /***
                             * 记得把别人发送过来并且显示了的消息置为已读
                             * ***/
                            mEMConversation.markMessageAsRead(message.getMsgId());
                        }
                    }
                    //调整焦点的位置
                    if (mEMMessageList.size() > 0) {
                        linearlayoutManager.scrollToPosition(mEMMessageList.size() - 1);
                    }
                }
            });
        }

        @Override//收到透传消息
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }
        @Override//收到已读回执
        public void onMessageRead(List<EMMessage> messages) {
        }
        @Override//收到已送达回执
        public void onMessageDelivered(List<EMMessage> messages) {

        }
        @Override//消息状态变动
        public void onMessageChanged(EMMessage message, Object change) {

        }
    };

    /*2-发送消息给对方的方法*/
    private void setMesaage() {
        //获取要发送的文本消息
        String content = mEtContent.getText().toString().trim();

        // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        final EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        // 如果是群聊，设置chattype，默认是单聊
        if (chatType == Constants.CHATTYPE_GROUP) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        /*子线程中进行网络请求,避免占用主线程造成UI显示延迟*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                //发送消息
                EMClient.getInstance().chatManager().sendMessage(message);
                /*回到主线程进行UI更新*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /***刷新消息列表的显示***/
                        mEMMessageList.add(message);//添加消息到集合对象
                        //mChatMessageAdapter.notifyItemInserted(mEMMessageList.size()-1);
                        mChatMessageAdapter.notifyDataSetChanged();//刷新列表
                        if (mEMMessageList.size() > 0) {
                            //item位置移动到消息列表的最底部
                            linearlayoutManager.scrollToPosition(mChatMessageAdapter.getItemCount() - 1);
                        }
                        mEtContent.setText("");//清空输入内容
                        //mEtContent.clearFocus();//取消输入焦点
                    }
                });
            }
        }).start();

    }

    /*1-初始化加载对话消息的方法*/
    private void initMessage() {
        // 获取当前聊天对象的会话对象
        mEMConversation = EMClient.getInstance().chatManager().getConversation(
                toChatUsername,//聊天对象
                EaseCommonUtils.getConversationType(chatType), true);

        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = mEMConversation.getAllMessages();//获取此会话的所有消息

        //SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
        //获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，
        // APP中无需再次把获取到的messages添加到会话中
        //List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);
        int msgCount = msgs != null ? msgs.size() : 0;
        //mEMConversation.getAllMsgCount();//获取此会话在本地的所有的消息数量
        //如果只是获取当前在内存的消息数量，调用conversation.getAllMessages().size();
        if (msgCount < mEMConversation.getAllMsgCount() && msgCount < pagesize) {
            //当前会话中的消息数量(默认每次20)小于用户的期望量时,就去服务器端加载更多
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();//获取当前会话中最老的消息的id标示
            }
            //从最上面的消息位置开始,加载剩下数量的消息到本地对象来
            mEMConversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }
        //指定会话消息未读数清零
        mEMConversation.markAllMessagesAsRead();

        //获取未读消息数量
        //int unReadCount=mEMConversation.getUnreadMsgCount();
        //把一条消息置为已读
        //conversation.markMessageAsRead(messageId);
        //所有未读消息数清零
        //EMClient.getInstance().chatManager().markAllConversationsAsRead();

    }

    @OnClick({R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                //注意：在xml文件中要记得把按钮设置为enabled="false",这样保证不会在输入空时触发发送请求
                setMesaage();
                break;
            default:
                break;
        }
    }

    //创建一个多editext的输入监听类
    class TextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mEtContent.length() > 0) {
                mBtnSend.setBackgroundResource(R.drawable.btn_login_enable_shape);
                mBtnSend.setEnabled(true);//设置按钮可以点击使用
            } else {
                mBtnSend.setBackgroundResource(R.drawable.btn_login_disable_shape);
                mBtnSend.setEnabled(false);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /*记得在activity销毁时移除消息监听*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(mEMMessageListener);
    }
}
