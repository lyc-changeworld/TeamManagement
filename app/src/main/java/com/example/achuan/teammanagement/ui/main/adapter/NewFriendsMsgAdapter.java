package com.example.achuan.teammanagement.ui.main.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.model.db.DBManager;
import com.example.achuan.teammanagement.model.db.InviteMessage;
import com.example.achuan.teammanagement.util.DialogUtil;
import com.hyphenate.chat.EMClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 16-10-5.
 * 功能：RY列表适配器类(这是个模板,大家可以在此基础上进行扩展)
 * MyBean是一个(模板)数据模型类,路径为：model/bean/
 * 注意：该文件具体使用时需移动到对应ui/modulexxx/adapter文件目录下,方便管理
 */
public  class NewFriendsMsgAdapter extends RecyclerView.Adapter<NewFriendsMsgAdapter.ViewHolder> {


    private LayoutInflater mInflater;//创建布局装载对象来获取相关控件（类似于findViewById()）
    private Context mContext;//显示框面
    protected List<InviteMessage> mInviteMessageList;


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
    public NewFriendsMsgAdapter(Context mContext, List<InviteMessage> mInviteMessageList) {
        this.mContext = mContext;
        this.mInviteMessageList = mInviteMessageList;
        //通过获取context来初始化mInflater对象
        mInflater = LayoutInflater.from(mContext);
    }

    //适配器中数据集中的个数
    public int getItemCount() {
        return mInviteMessageList.size();
    }

    /****
     * item第一次显示时,才创建其对应的viewholder进行控件存储,之后直接使用即可
     ****/
    //先创建ViewHolder
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        //下面需要把单个item对应的布局文件加载进来,这里对应R.layout.xxx布局文件
        View view = mInflater.inflate(R.layout.item_invite_msg, parent, false);
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
        /*
        * 这里结合下面自定义的viewHolder类,拿到控件的加载对象然后进行数据绑定
        * 这一步比较重要,需要小心设置
        * */
        /***----------------------------------------***/

        String str1 = mContext.getResources().getString(
                R.string.Has_agreed_to_your_friend_request);//已同意你的好友要请
        String str2 = mContext.getResources().getString(
                R.string.agree);//同意
        String str3 = mContext.getResources().getString(
                R.string.Request_to_add_you_as_a_friend);//请求加你为好友(默认显示的加好友理由)

        final Button mBtnAgree=holder.mBtnAgree;
        TextView mTvReason=holder.mTvReason;
        TextView mTvName=holder.mTvName;

        final InviteMessage msg = mInviteMessageList.get(postion);
        if (msg != null) {
            //通过数据库中存储的序数来获取对应的状态
            InviteMessage.InviteMesageStatus msgStatus= InviteMessage.
                    InviteMesageStatus.valueOf(msg.getStatusOrdinal());
            //初始化设置
            mTvReason.setText(msg.getReason());
            mTvName.setText(msg.getFrom());


            if (msgStatus== InviteMessage.InviteMesageStatus.BEINVITEED ||
                    msgStatus == InviteMessage.InviteMesageStatus.BEAPPLYED ||
                    msgStatus == InviteMessage.InviteMesageStatus.GROUPINVITATION) {
                //对方向你发起要请
                mBtnAgree.setVisibility(View.VISIBLE);
                mBtnAgree.setEnabled(true);
                //mBtnAgree.setBackgroundResource(R.drawable.btn_login_enable_shape);
                mBtnAgree.setBackgroundResource(android.R.drawable.btn_default);
                mBtnAgree.setText(str2);
                if (msg.getReason() == null) {
                    // 如果没写理由
                    holder.mTvReason.setText(str3);
                }
                // 设置点击事件
                mBtnAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 同意别人发的好友请求
                        acceptInvitation(mBtnAgree,  msg);
                    }
                });
            }else if(msgStatus == InviteMessage.InviteMesageStatus.BEAGREED) {
                //对方同意了你的要请
                mTvReason.setText(str1);
            } else if (msgStatus == InviteMessage.InviteMesageStatus.AGREED) {
                //我同意了对方的要请
            } else if(msgStatus == InviteMessage.InviteMesageStatus.REFUSED){
                //我拒绝了对方的要请
            } else if(msgStatus == InviteMessage.InviteMesageStatus.GROUPINVITATION_ACCEPTED){
                //收到对方同意群邀请的通知
            } else if(msgStatus == InviteMessage.InviteMesageStatus.GROUPINVITATION_DECLINED){
                //收到对方拒绝群邀请的通知
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


    /**
     * 同意好友请求或者群申请
     *
     * @param buttonAgree
     * @param msg
     */
    private void acceptInvitation(final Button buttonAgree , final InviteMessage msg) {
        String str1 = mContext.getResources().getString(
                R.string.Are_agree_with);//正在同意
        final String str2 = mContext.getResources().getString(
                R.string.Has_agreed_to);//已同意
        final String str3 = mContext.getResources().getString(
                R.string.Agree_with_failure);//同意失败

        //通过数据库中存储的序数来获取对应的状态
        final InviteMessage.InviteMesageStatus msgStatus= InviteMessage.
                InviteMesageStatus.valueOf(msg.getStatusOrdinal());

        /*创建加载对话框*/
        DialogUtil.createProgressDialog(mContext,null,
                str1,
                false,false);
        /*---开启子线程进行接受加好友操作---*/
        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                    if (msgStatus == InviteMessage.InviteMesageStatus.BEINVITEED) {
                        //同意好友请求
                        EMClient.getInstance().contactManager().acceptInvitation(msg.getFrom());
                    } else if (msgStatus == InviteMessage.InviteMesageStatus.BEAPPLYED) {
                        //同意对方的加群申请
                        EMClient.getInstance().groupManager().acceptApplication(msg.getFrom(), msg.getGroupId());
                    } else if (msgStatus == InviteMessage.InviteMesageStatus.GROUPINVITATION) {
                        //收到对方的群邀请
                        EMClient.getInstance().groupManager().acceptInvitation(msg.getGroupId(), msg.getGroupInviter());
                    }
                    //更新状态为:我同意了对方的请求
                    /***对数据库进行更新状态操作***/
                    DBManager.updateMessage(
                            msg.getId(),//消息的id号
                                    InviteMessage.InviteMesageStatus.AGREED.ordinal());//对应状态在枚举类中的序数

                    //更新界面显示
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void run() {
                            if(DialogUtil.isProgressDialogShowing()){
                                DialogUtil.closeProgressDialog();
                            }
                            buttonAgree.setText(str2);
                            buttonAgree.setBackgroundDrawable(null);//同意按钮消失
                            buttonAgree.setEnabled(false);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {

                        @SuppressLint("ShowToast")
                        @Override
                        public void run() {
                            if(DialogUtil.isProgressDialogShowing()){
                                DialogUtil.closeProgressDialog();
                            }
                            Toast.makeText(mContext, str3 + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }


    /*创建自定义的ViewHolder类*/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //使用butterknife来进行item中的控件加载,此处需要自己添加
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_reason)
        TextView mTvReason;
        @BindView(R.id.btn_agree)
        Button mBtnAgree;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
