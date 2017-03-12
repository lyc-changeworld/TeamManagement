package com.example.achuan.teammanagement.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by achuan on 16-11-8.
 * 功能：自定义右侧索引栏控件View
 */
public class SideBar extends View {

    private Paint paint = new Paint();//画笔
    private int choose = -1;//记录手指的状态
    private boolean showBackground;//根据手指点击状态进行侧边栏背景的切换的标志位
    private TextView mTv_hint;//点击索引栏显示提示文本(屏幕中间)
    private OnChooseLetterChangedListener onChooseLetterChangedListener;
    private Context context;

    //预留一个方法,后续将调用该方法将TextView控件引入布局中
    public void setTextView(TextView mTv_hint) {
        this.mTv_hint= mTv_hint;
    }
    /*右侧需要显示的字符以及顺序*/
    public static String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z","#"};

    /*public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }*/
    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }
    /*public SideBar(Context context) {
        super(context);
    }*/

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBackground) {
            canvas.drawColor(Color.parseColor("#D9D9D9"));//手指触碰侧边栏时背景变成灰色
        }
        int height = getHeight();
        int width = getWidth();
        //平均每个字母占的高度
        int singleHeight = height / letters.length;
        for (int i = 0; i < letters.length; i++) {
            //paint.setColor(Color.BLACK);
            paint.setColor(Color.parseColor("#8C8C8C"));
            paint.setAntiAlias(true);
            //paint.setTextSize(35f);
            paint.setTextSize(sp2px(context, 10));//该方法的参数单位是px

            // 选中的状态
            if (i == choose) {
                paint.setColor(Color.parseColor("#FF2828"));
                paint.setFakeBoldText(true);
            }
            // x坐标等于中间-字符串宽度的一半.
            float x = width / 2 - paint.measureText(letters[i]) / 2;
            float y = singleHeight * i + singleHeight;
            canvas.drawText(letters[i], x, y, paint);
            paint.reset();//重置画笔
        }
    }

    /*将sp变换成px的方法,从而实现屏幕适配*/
    public int sp2px(Context context, float spValue){
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }
    //对手指触碰事件进行监听处理
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        int oldChoose = choose;
        int c = (int) (y / getHeight() * letters.length);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBackground = true;
                if (oldChoose != c && onChooseLetterChangedListener != null) {
                    if (c > -1 && c < letters.length) {
                        onChooseLetterChangedListener.onChooseLetter(letters[c]);
                        choose = c;
                        invalidate();
                        //将点击的字母显示在屏幕中间
                        if(mTv_hint!=null){
                            mTv_hint.setText(letters[c]);
                            mTv_hint.setVisibility(VISIBLE);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && onChooseLetterChangedListener != null) {
                    if (c > -1 && c < letters.length) {
                        onChooseLetterChangedListener.onChooseLetter(letters[c]);
                        choose = c;
                        invalidate();
                        //将点击的字母显示在屏幕中间
                        if(mTv_hint!=null){
                            mTv_hint.setText(letters[c]);
                            mTv_hint.setVisibility(VISIBLE);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBackground = false;
                choose = -1;
                if (onChooseLetterChangedListener != null) {
                    onChooseLetterChangedListener.onNoChooseLetter();
                    if(mTv_hint!=null){
                        //隐藏中间的文字控件
                        mTv_hint.setVisibility(INVISIBLE);
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                showBackground = false;
                choose = -1;
                if (onChooseLetterChangedListener != null) {
                    onChooseLetterChangedListener.onNoChooseLetter();
                    if(mTv_hint!=null){
                        //隐藏中间的文字控件
                        mTv_hint.setVisibility(INVISIBLE);
                    }
                }
                invalidate();
                break;
            default:break;
        }
        return true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    //设置监听的方法
    public void setOnTouchingLetterChangedListener(OnChooseLetterChangedListener onChooseLetterChangedListener) {
        this.onChooseLetterChangedListener = onChooseLetterChangedListener;
    }
    //自定义接口方法,让引入者自己实现具体的方法
    public interface OnChooseLetterChangedListener {
        void onChooseLetter(String s);
        void onNoChooseLetter();
    }

}
