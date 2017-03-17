package com.example.achuan.teammanagement.ui.contacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.model.db.ContactUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 16-10-5.
 * 功能：RY列表适配器类(这是个模板,大家可以在此基础上进行扩展)
 * MyBean是一个(模板)数据模型类,路径为：model/bean/
 * 注意：该文件具体使用时需移动到对应ui/modulexxx/adapter文件目录下,方便管理
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private LayoutInflater mInflater;//创建布局装载对象来获取相关控件（类似于findViewById()）
    private Context mContext;//显示框面
    protected List<ContactUser> mContactUserList;


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
    public ContactAdapter(Context mContext, List<ContactUser> mContactUserList) {
        this.mContext = mContext;
        this.mContactUserList = mContactUserList;
        //通过获取context来初始化mInflater对象
        mInflater = LayoutInflater.from(mContext);
    }

    //适配器中数据集中的个数
    public int getItemCount() {
        return mContactUserList.size();
    }

    /****
     * item第一次显示时,才创建其对应的viewholder进行控件存储,之后直接使用即可
     ****/
    //先创建ViewHolder
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        //下面需要把单个item对应的布局文件加载进来,这里对应R.layout.item_my布局文件
        View view = mInflater.inflate(R.layout.item_contact, parent, false);
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
        ContactUser contactUser = mContactUserList.get(postion);

        TextView mTvHeader=holder.mTvHeader;
        ImageView mIvAvatar=holder.mIvAvatar;

        //1-设置用户名
        holder.mTvName.setText(contactUser.getUserName());

        //设置头像
        if (postion == 0) {
            //最顶上的是申请和通知的栏(单栏)
            mIvAvatar.setBackgroundResource(R.drawable.em_new_friends_icon);
            mTvHeader.setVisibility(View.GONE);
        } else if (postion == 1) {
            //群聊栏(单栏)
            mIvAvatar.setBackgroundResource(R.drawable.em_groups_icon);
            mTvHeader.setVisibility(View.GONE);
        } else {
            //联系人栏(多栏)
            mIvAvatar.setBackgroundResource(R.drawable.em_default_avatar);
            /**设置首字母导航栏布局*/
            char header=contactUser.getInitialLetter();//获取到用户名的首字母
            //不为空：!='\0',不为空格：Character.isSpace(ch[i]))
            /*首字母相同的item,只在第一个item上显示字母header*/
            if (postion == 2 ||header !='\0' &&
                    !(header== mContactUserList.get(postion - 1).getInitialLetter())) {
                if (Character.isSpaceChar(header)) {
                    mTvHeader.setVisibility(View.GONE);
                } else {
                    mTvHeader.setVisibility(View.VISIBLE);
                    mTvHeader.setText(String.valueOf(header));
                }
            }else {
                mTvHeader.setVisibility(View.GONE);
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
        @BindView(R.id.tv_header)
        TextView mTvHeader;
        @BindView(R.id.iv_avatar)
        ImageView mIvAvatar;
        @BindView(R.id.tv_unread_msg_number)
        TextView mTvUnreadMsgNumber;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_signature)
        TextView mTvSignature;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
