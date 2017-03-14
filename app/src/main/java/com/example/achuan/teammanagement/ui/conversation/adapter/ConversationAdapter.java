package com.example.achuan.teammanagement.ui.conversation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.achuan.teammanagement.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 16-10-5.
 * 功能：RY列表适配器类(这是个模板,大家可以在此基础上进行扩展)
 * 消息列表的适配器类
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private LayoutInflater mInflater;//创建布局装载对象来获取相关控件（类似于findViewById()）
    private Context mContext;//显示框面
    private List<EMConversation> mEMConversationList;


    //定义两个接口引用变量
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;

    //define interface
    public interface OnClickListener {
        void onClick(View view, int postion);
    }

    public interface OnLongClickListener {
        void onLongClick(View view, int postion);
    }

    //定义 set方法
    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    /*构造方法*/
    public ConversationAdapter(Context mContext, List<EMConversation> mEMConversationList) {
        this.mContext = mContext;
        this.mEMConversationList = mEMConversationList;
        //通过获取context来初始化mInflater对象
        mInflater = LayoutInflater.from(mContext);
    }

    //适配器中数据集中的个数
    public int getItemCount() {
        return mEMConversationList.size();
    }

    /****
     * item第一次显示时,才创建其对应的viewholder进行控件存储,之后直接使用即可
     ****/
    //先创建ViewHolder
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        //下面需要把单个item对应的布局文件加载进来,这里对应R.layout.item_my布局文件
        View view = mInflater.inflate(R.layout.item_conversation, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);//创建一个item的viewHoler实例
        return viewHolder;
    }

    /****
     * 当前界面中出现了item的显示更新时执行该方法（即有item加入或者移除界面）
     * <p/>
     * 该方法的执行顺序　　早于　　onScrolled（）方法
     ****/
    //绑定ViewHolder
    public void onBindViewHolder(final ViewHolder holder, final int postion) {
        //再通过viewHolder中缓冲的控件添加相关数据
        // 获取与此用户/群组的会话
        EMConversation conversation = mEMConversationList.get(postion);
        // 获取用户username或者群组groupid
        String username = conversation.conversationId();


        /*1-设置头像图标*/
        if(conversation.isGroup()){
            //群聊
            if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                holder.mIvAvatar.setImageResource(R.drawable.em_group_icon);
                username = EMClient.getInstance().groupManager().
                        getGroup(username).getGroupName();
            }
        }else{
            //单聊
            holder.mIvAvatar.setImageResource(R.drawable.default_avatar);
        }

        /*2-设置聊天对象名称*/
        holder.mTvName.setText(username);

        /*3-设置未读消息数*/
        if (conversation.getUnreadMsgCount() > 0) {
            // 显示与此用户的消息未读数
            holder.mTvUnreadMsgNumber.setText(
                    String.valueOf(conversation.getUnreadMsgCount()));
            holder.mTvUnreadMsgNumber.setVisibility(View.VISIBLE);
        } else {
            holder.mTvUnreadMsgNumber.setVisibility(View.INVISIBLE);
        }
        /*4-设置最后一条消息内容及时间*/
        if (conversation.getAllMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            holder.mTvMessage.setText(lastMessage.getBody().toString());
            holder.mTvTime.setText(DateUtils.getTimestampString(
                    new Date(lastMessage.getMsgTime())));
            //如果最后一条消息为发送但失败的状态,显示一个警示图标
            if (lastMessage.direct() == EMMessage.Direct.SEND &&
                    lastMessage.status() == EMMessage.Status.FAIL) {
                holder.mTvMsgState.setVisibility(View.VISIBLE);
            } else {
                holder.mTvMsgState.setVisibility(View.GONE);
            }
        }
        /***为item设置点击监听事件***/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    //设置回调监听
                    mOnClickListener.onClick(view, postion);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongClickListener != null) {
                    //设置长时间点击回调监听
                    mOnLongClickListener.onLongClick(v, postion);
                }
                return false;//设置成false,这样就不会触发单击的监听事件
            }
        });
    }


    /*创建自定义的ViewHolder类*/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //使用butterknife来进行item中的控件加载,此处需要自己添加
        @BindView(R.id.iv_avatar)
        ImageView mIvAvatar;
        @BindView(R.id.tv_unread_msg_number)
        TextView mTvUnreadMsgNumber;
        @BindView(R.id.tv_msg_state)
        ImageView mTvMsgState;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.tv_message)
        TextView mTvMessage;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
