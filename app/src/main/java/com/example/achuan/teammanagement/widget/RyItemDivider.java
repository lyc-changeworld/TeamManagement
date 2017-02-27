package com.example.achuan.teammanagement.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by achuan on 17-2-6.
 * 功能：为RecycleView绘制自定义的分割线
 * 方法说明：onDraw方法：其绘制将会在每个Item被绘制之前进行；
           onDrawOver：在绘制完Item后进行绘制；
          getItemOffsets：可以通过outRect.set()为每个Item设置一定的偏移量；
   注明：该类的具体使用见：ui/module0/fragment/Itme0Fragment文件
 */

public class RyItemDivider extends RecyclerView.ItemDecoration {

    private Drawable mDrawable;

    public RyItemDivider(Context context, int resId) {
        //在这里我们传入作为Divider的Drawable对象
        mDrawable = context.getResources().getDrawable(resId);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //设置item底部的间距
        outRect.set(0, 0, 0, mDrawable.getIntrinsicWidth());
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //计算分割线最左边和最右边的位置
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        //计算item的个数
        final int childCount = parent.getChildCount();
        //遍历将每个item的分割线画出来
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            //以下计算主要用来确定绘制的位置
            final int top = child.getBottom() + params.bottomMargin;//顶部的位置
            final int bottom = top + mDrawable.getIntrinsicHeight();//底部的位置
            mDrawable.setBounds(left, top, right, bottom);//设置分割线的参数(画矩形)
            mDrawable.draw(c);//进行绘画
        }
    }
}
