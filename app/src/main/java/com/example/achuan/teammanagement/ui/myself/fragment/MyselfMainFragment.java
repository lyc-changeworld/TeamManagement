package com.example.achuan.teammanagement.ui.myself.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.app.App;
import com.example.achuan.teammanagement.base.SimpleFragment;
import com.example.achuan.teammanagement.ui.main.activity.LoginActivity;
import com.example.achuan.teammanagement.util.DialogUtil;
import com.hyphenate.EMCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by achuan on 17-2-1.
 * 功能：
 */

public class MyselfMainFragment extends SimpleFragment {

    private Context mContext;

    @BindView(R.id.btn_logout)
    Button mBtnLogout;



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_myself_main;
    }

    @Override
    protected void initEventAndData() {
        //初始化获取上下文操作对象
        mContext=getActivity();

    }

    @OnClick({R.id.btn_logout})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_logout:
                logoutDeal();
                break;
            default:break;
        }
    }

    private void logoutDeal() {
        //弹出对话框确认是否退出
        DialogUtil.createOrdinaryDialog(mContext,
                getString(R.string.Whether_or_not_to_logout),//标题
                getString(R.string.Logout_alert),//内容
                getString(R.string.logout), //右边按钮内容
                getString(R.string.cancel), //左边按钮内容
                true, new DialogUtil.OnAlertDialogButtonClickListener() {
                    //点击退出按钮
                    @Override
                    public void onRightButtonClick() {
                        //创建进度加载对话框
                        DialogUtil.createProgressDialog(mContext,"",
                                getString(R.string.Are_logged_out),
                                false,false);//对话框无法被取消
                        /*---执行登出操作---*/
                        App.getInstance().logout(false,new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(!getActivity().isFinishing()&&DialogUtil.isProgressDialogShowing()){
                                            DialogUtil.closeProgressDialog();
                                        }

                                        //后期可以设置是否退出后清空当前用户的缓存数据,即删除数据库文件

                                        //结束主界面并跳转到登录页面
                                        startActivity(new Intent(mContext, LoginActivity.class));
                                        getActivity().finish();
                                    }
                                });
                            }
                            @Override
                            public void onProgress(int progress, String status) {
                            }
                            @Override
                            public void onError(int code, String message) {
                                //需要在主线程中进行UI更新
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        if(DialogUtil.isProgressDialogShowing()){
                                            DialogUtil.closeProgressDialog();
                                        }
                                        Toast.makeText(mContext,
                                                "unbind devicetokens failed",
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                            }
                        });
                    }
                    @Override
                    public void onLeftButtonClick() {
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

}
