package com.example.achuan.teammanagement.ui.contacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.model.bean.MyBean;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by achuan on 16-10-5.
 * 功能：RY列表适配器类(这是个模板,大家可以在此基础上进行扩展)
 *      MyBean是一个(模板)数据模型类,路径为：model/bean/
 *      注意：该文件具体使用时需移动到对应ui/modulexxx/adapter文件目录下,方便管理
 */
public  class PickContactAdapter extends RecyclerView.Adapter<PickContactAdapter.ViewHolder> {

    private LayoutInflater mInflater;//创建布局装载对象来获取相关控件（类似于findViewById()）
    private Context mContext;//显示框面
    protected List<MyBean> mList;


    //定义两个接口引用变量
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;

    //define interface
    public interface OnClickListener {
        void onClick(View view, int postion);
    }
    public interface OnLongClickListener{
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
    public PickContactAdapter(Context mContext, List<MyBean> myBeanList) {
        this.mContext = mContext;
        this.mList=myBeanList;
        //通过获取context来初始化mInflater对象
        mInflater = LayoutInflater.from(mContext);
    }
    //适配器中数据集中的个数
    public int getItemCount() {
        return mList.size();
    }
    /****
     * item第一次显示时,才创建其对应的viewholder进行控件存储,之后直接使用即可
     ****/
    //先创建ViewHolder
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        //下面需要把单个item对应的布局文件加载进来,这里对应R.layout.item_my布局文件
        View view = mInflater.inflate(R.layout.item_no_card, parent, false);
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
        //final MyBean myBean = mList.get(postion);//从数据源集合中获得对象
        /*
        * 这里结合下面自定义的viewHolder类,拿到控件的加载对象然后进行数据绑定
        * 这一步比较重要,需要小心设置
        * */
        //holder.mTvAchuan.setText(myBean.getAchuan());
        /***为item设置点击监听事件***/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    //设置回调监听
                    mOnClickListener.onClick(view,postion);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnLongClickListener!=null){
                    //设置长时间点击回调监听
                    mOnLongClickListener.onLongClick(v,postion);
                }
                return false;//设置成false,这样就不会触发单击的监听事件
            }
        });
    }


    /*创建自定义的ViewHolder类*/
    public  static class ViewHolder extends RecyclerView.ViewHolder {
        //使用butterknife来进行item中的控件加载,此处需要自己添加
        /*@BindView(R.id.tv_achuan)
        TextView mTvAchuan;*/
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
