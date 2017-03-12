package com.example.achuan.teammanagement.ui.contacts.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.model.db.ContactUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by achuan on 16-10-5.
 * 功能：选择联系的列表适配器
 */
public class PickContactAdapter extends RecyclerView.Adapter<PickContactAdapter.ViewHolder> {


    private LayoutInflater mInflater;//创建布局装载对象来获取相关控件（类似于findViewById()）
    private Context mContext;//显示框面
    protected List<ContactUser> mContactUserList;
    private List<String> existMembers;
    private boolean[] isCheckedArray;//记录item是否被选中

    public boolean[] getIsCheckedArray() {
        return isCheckedArray;
    }

    //定义两个接口引用变量
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;

    //复选框监听接口及设置入口
    /*private OnCheckedChangeListener mOnCheckedChangeListener;
    public interface OnCheckedChangeListener {
        void onCheckedChange(CompoundButton buttonView, int postion, boolean isChecked);
    }
    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }*/

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
    public PickContactAdapter(Context mContext, List<ContactUser> mContactUserList,List<String> existMembers) {
        this.mContext = mContext;
        this.mContactUserList = mContactUserList;
        this.existMembers=existMembers;
        //通过获取context来初始化mInflater对象
        mInflater = LayoutInflater.from(mContext);
        //注意,这里需要根据集合的数量来定义状态数值的大小
        isCheckedArray = new boolean[mContactUserList.size()];
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
        View view = mInflater.inflate(R.layout.item_contact_with_checkbox, parent, false);
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
        ContactUser contactUser=mContactUserList.get(postion);//拿到联系人对象
        final String userName=contactUser.getUserName();//获取用户名

        holder.mTvName.setText(userName);//设置用户名文本

        /**设置首字母导航栏布局*/
        char header=contactUser.getInitialLetter();//获取到用户名的首字母
        //不为空：!='\0',不为空格：Character.isSpace(ch[i]))
        /*首字母相同的item,只在第一个item上显示字母header*/
        if (postion == 0 || header !='\0' &&
                !(header== mContactUserList.get(postion - 1).getInitialLetter())) {
            if (Character.isSpaceChar(header)) {
                holder.mTvHeader.setVisibility(View.GONE);
            } else {
                holder.mTvHeader.setVisibility(View.VISIBLE);
                holder.mTvHeader.setText(String.valueOf(header));
            }
        } else {
            holder.mTvHeader.setVisibility(View.GONE);
        }


        AppCompatCheckBox appCompatCheckBox=holder.mCbContact;//获取到控件缓存对象
        //为复选框添加状态变化监听事件
        appCompatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // check the exist members
                if (existMembers.contains(userName)) {
                    isChecked = true;
                    buttonView.setChecked(true);
                }
                isCheckedArray[postion] = isChecked;
            }
        });
        // keep exist members checked
        if (existMembers.contains(userName)) {
            appCompatCheckBox.setChecked(true);
            isCheckedArray[postion] = true;
        } else {
            appCompatCheckBox.setChecked(isCheckedArray[postion]);
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
        @BindView(R.id.cb_contact)
        AppCompatCheckBox mCbContact;
        @BindView(R.id.iv_avatar)
        ImageView mIvAvatar;
        @BindView(R.id.tv_name)
        TextView mTvName;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
