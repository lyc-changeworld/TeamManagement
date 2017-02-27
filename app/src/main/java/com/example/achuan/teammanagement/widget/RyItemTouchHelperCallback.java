package com.example.achuan.teammanagement.widget;

import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by achuan on 17-2-7.
 * 参考资料：普通版：http://blog.csdn.net/yanzhenjie1003/article/details/51935982
 *         高级版：http://blog.csdn.net/yanzhenjie1003/article/details/52115566
 * 功能：实现item拖拽和侧滑效果,使用了接口回调机制
 * 注明：具体的使用见： ui/module0/fragment/Itme0Fragment文件
 */

public class RyItemTouchHelperCallback extends ItemTouchHelper.Callback{

    private float ALPHA_FULL = 1.0f;//透明度的满格值
    private boolean isCanDrag = false;//是否可以拖拽
    private boolean isCanSwipe = false;//是否可以被滑动

    private OnItemTouchCallbackListener onItemTouchCallbackListener;//Item操作的回调
    /**
     * 设置Item操作的回调，去更新UI和数据源
     *
     * @param onItemTouchCallbackListener
     */
    public void setOnItemTouchCallbackListener(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }
    //定义回调接口方法
    public interface OnItemTouchCallbackListener {
        /**
         * 当某个Item被滑动删除的时候
         *
         * @param adapterPosition item的position
         */
        void onSwiped(int adapterPosition);
        /**
         * 当两个Item位置互换的时候被回调
         *
         * @param srcPosition    拖拽的item的position
         * @param targetPosition 目的地的Item的position
         * @return 开发者处理了操作应该返回true，开发者没有处理就返回false
         */
        boolean onMove(int srcPosition, int targetPosition);
    }


    /*设置是否可以拖拽*/
    public void setDragEnable(boolean canDrag) {
        isCanDrag = canDrag;
    }
    /*设置是否可以被滑动*/
    public void setSwipeEnable(boolean canSwipe) {
        isCanSwipe = canSwipe;
    }


    /**
     * RecyclerView item支持长按进入拖动操作
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return isCanDrag;
    }
    /**
     * RecyclerView item任意位置触发启用滑动操作
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return isCanSwipe;
    }
    /**
     * 指定可以支持的拖放和滑动的方向，上下为拖动（drag），左右为滑动（swipe）
     * 使用makeMovementFlags(dragFlags, swipeFlags)来设置标志位
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0,swipeFlags=0;//拖拽和侧滑的允许标志
        //拿到布局管理者
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //网格布局判断
        if (layoutManager instanceof GridLayoutManager ||
                layoutManager instanceof StaggeredGridLayoutManager) {
            // flag如果值是0,相当于这个功能被关闭
            dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT |
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN;//上下左右
            swipeFlags = 0;//无法进行侧滑
        } else if (layoutManager instanceof LinearLayoutManager) {
            // linearLayoutManager
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            //获取线性布局的方向
            int orientation = linearLayoutManager.getOrientation();
            // 为了方便理解,相当于分为横着的ListView和竖着的ListView
            if (orientation == LinearLayoutManager.HORIZONTAL) {//横向的布局
                swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//左右
            } else if (orientation == LinearLayoutManager.VERTICAL) {//竖向的布局
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//上下
                swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }
    /**
     * 拖拽滑动操作
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //如果当前拖拽的item和目标位置的item的样式不同,将无法进行拖拽移动
        /*if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;//false则代表该动作无法进行
        }*/
        if(onItemTouchCallbackListener!=null){
            //添加回调接口方法
            return onItemTouchCallbackListener.onMove(
                    viewHolder.getAdapterPosition(),target.getAdapterPosition());
        }
        return false;
    }
    /**
     * 侧滑操作
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //注册接口回调方法(侧滑消失)
        if(onItemTouchCallbackListener!=null){
            //添加回调接口方法
            onItemTouchCallbackListener.onSwiped(viewHolder.getAdapterPosition());
        }
    }
    /**
     *复写item布局的描绘方法,添加自定义的动画(分拖拽和滑动两种)
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //侧边刷动时添加动画
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //添加透明度变化的动画,随着横向移动距离透明度逐渐减小
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
    /***--***/
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }
    /***--***/
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }
}
