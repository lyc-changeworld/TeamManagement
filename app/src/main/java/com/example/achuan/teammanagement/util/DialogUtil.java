package com.example.achuan.teammanagement.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.achuan.teammanagement.R;

import java.util.Calendar;

/**
 * Created by achuan on 16-11-13.
 * 功能：对话框的封装功能类
 */
public class DialogUtil {

    public static ProgressDialog sProgressDialog;

    //普通窗口的接口
    public interface OnAlertDialogButtonClickListener {
        //右边按钮点击事件
        void onRightButtonClick();
        //左边按钮点击事件
        void onLeftButtonClick();
    }
    //输入文本窗口的接口
    public interface OnInputDialogButtonClickListener {
        //右边按钮点击事件
        void onRightButtonClick(String input);
        //左边按钮点击事件
        void onLeftButtonClick();
    }
    //时间选择器窗口的接口
    public interface OnDatePickerDialogButtonClickListener {
        //右边按钮点击事件
        void onRightButtonClick(Boolean isBorn, int age, String StarSeat);
        //左边按钮点击事件
        void onLeftButtonClick();
    }

    /***1-创建一个最普通的对话框***/
    public static void createOrdinaryDialog(Context context, String title, String message,
                                    String rightString, String leftString,boolean isCancelable,
                                    final OnAlertDialogButtonClickListener onAlertDialogButtonClickListener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context,
                R.style.Theme_AppCompat_Light_Dialog_Alert_Self);//先创建一个构造实例
        dialog.setTitle(title);//设置标题
        dialog.setMessage(message);//设置内容部分
        dialog.setCancelable(isCancelable);//设置是否可以通过Back键取消：false为不可以取消,true为可以取消
        //设置右边按钮的信息
        dialog.setPositiveButton(rightString, new DialogInterface.OnClickListener() {
            @Override//点击触发事件
            public void onClick(DialogInterface dialogInterface, int i) {
                if(onAlertDialogButtonClickListener!=null){
                    onAlertDialogButtonClickListener.onRightButtonClick();
                }
            }
        });
        //设置左边按钮的信息
        dialog.setNegativeButton(leftString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(onAlertDialogButtonClickListener!=null){
                    onAlertDialogButtonClickListener.onLeftButtonClick();
                }
            }
        });
        dialog.show();//将对话框显示出来
    }

    /***2-创建一个耗时等待的对话框***/
    public static void createProgressDialog(Context context,String title,
                                            String message){
        sProgressDialog=new ProgressDialog(context);
        sProgressDialog.setTitle(title);
        sProgressDialog.setMessage(message);
        sProgressDialog.setCancelable(true);//对话框可以通过Back键取消掉
        sProgressDialog.show();
        /*//加载完后执行关闭对话框的方法
                progressDialog.dismiss();*/
    }
    /***3-关闭耗时加载对话框***/
    public static void closeProgressDialog(){
        sProgressDialog.dismiss();
    }

    /***4-创建可输入编辑文本的对话框***/
    public static void createInputDialog(Context context, String message, String title,
                                         String rightString, String leftString,
                                         final OnInputDialogButtonClickListener onInputDialogButtonClickListener){
        /*eitText的相关处理*/
        final EditText editText = new EditText(context);//创建一个新的editext对象
        editText.setText(message);
        editText.setSelection(message.length());//让光标停留在文字的末尾

        AlertDialog.Builder inputDialog = new AlertDialog.Builder(context);
        inputDialog.setTitle(title).setView(editText);
        inputDialog.setPositiveButton(rightString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onInputDialogButtonClickListener.onRightButtonClick(
                        editText.getText().toString());
            }
        });
        inputDialog.setNegativeButton(leftString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onInputDialogButtonClickListener.onLeftButtonClick();
            }
        });
        inputDialog.show();
    }

    /***5-创建时间选择器框***/
    public static void createDatePickerDialog(Context context,String title,
                                              String rightString, String leftString,
                                              final OnDatePickerDialogButtonClickListener onDatePickerDialogButtonClickListener){
        //获取日历操作对象
        final Calendar calendar = Calendar.getInstance();
        //设置日历的时间停留在当前系统的时间,后面通过get方法可以获取(年\月\日)等信息
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int nowYear=calendar.get(Calendar.YEAR);//年
        final int nowMonth=calendar.get(Calendar.MONTH);//月
        final int nowDayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);//日
        //创建时间选择器窗口
        final DatePickerDialog dialog = new DatePickerDialog(context,null,//设置初始日期
                calendar.get(Calendar.YEAR), // 传入年份
                calendar.get(Calendar.MONTH), // 传入月份
                calendar.get(Calendar.DAY_OF_MONTH)); // 传入天数
        //右边按钮点击事件
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,rightString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //通过dialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
                DatePicker datePicker = dialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                Boolean isBorn = null;//记录你是否出生
                int age = 0;
                String StarSeat = null;//记录星座
                //判断当前选择的时间
                if(year>nowYear){
                    isBorn=false;
                }else if(year==nowYear){
                    if(month>nowMonth){
                        isBorn=false;
                    }else if(month==nowMonth){
                        if(dayOfMonth>nowDayOfMonth){
                            isBorn=false;
                        }else {isBorn=true;}
                    }else {isBorn=true;}
                }else {isBorn=true;}
                /*出生了才计算年龄和推算星座*/
                if(isBorn){
                    age=nowYear-year;
                    StarSeat=StringUtil.getStarSeat(month + 1, dayOfMonth);
                }
                onDatePickerDialogButtonClickListener.onRightButtonClick(
                        isBorn,age,StarSeat);
            }
        });
        //左边按钮点击事件
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE,leftString,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDatePickerDialogButtonClickListener.onLeftButtonClick();
            }
        });
        //设置点击控件外的位置,是否取消时间选择操作,默认为true(取消)
        //dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(title);
        dialog.show();
    }

    /***6-创建自定义的对话框,layoutId(自定义的布局文件)***/
    public static Dialog createMyselfDialog(Context context,int layoutId,int gravity){
        //先创建窗口构造者对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //设置视图布局
        builder.setView(layoutId);
        //通过构造者创建对话框布局实例
        Dialog dialog=builder.create();
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog的位置
        dialogWindow.setGravity(gravity);//Gravity.BOTTOM
        /*//获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 0;//设置Dialog距离底部的距离
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);*/
        //显示窗口布局
        dialog.show();
        return dialog;
        /*//初始化自定义布局的控件,通过dialog实例来获取
        TextView chooseMan= (TextView) dialog.findViewById(R.id.tv_choose_man);
        TextView chooseWoman= (TextView) dialog.findViewById(R.id.tv_choose_woman);
        TextView chooseCancel= (TextView) dialog.findViewById(R.id.tv_choose_cancel);*/
    }

}
