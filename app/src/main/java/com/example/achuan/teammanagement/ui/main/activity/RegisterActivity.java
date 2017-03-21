package com.example.achuan.teammanagement.ui.main.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.util.DialogUtil;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by achuan on 17-2-27.
 * 功能：注册环信账号界面
 */

public class RegisterActivity extends SimpleActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_confirmPassword)
    EditText mEtConfirmPassword;
    @BindView(R.id.btn_register)
    Button mBtnRegister;

    /*对应EditText控件中输入字符的引用变量*/
    String userName, firstPassword, confirmPassword;



    @Override
    protected int getLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initEventAndData() {
        //设置Toolbar
        setToolBar(mToolbar, getString(R.string.register), true);

        /***为Edit输入框添加输入监听类,实现合理的效果***/
        //对只有在用户名和密码的输入都不为空的情况下，button按钮才显示有效，
        // 可以自己构造一个TextChange的类，实现一个TextWatcher接口，
        // 里面有三个函数可以实现对所有text的监听。
        TextChange textChange = new TextChange();
        mEtUsername.addTextChangedListener(textChange);
        mEtPassword.addTextChangedListener(textChange);
        mEtConfirmPassword.addTextChangedListener(textChange);
    }

    //创建一个多editext的输入监听类
    class TextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mEtUsername.length() > 0 &&
                    mEtPassword.length() > 0 &&
                    mEtConfirmPassword.length() > 0) {
                mBtnRegister.setBackgroundResource(R.drawable.btn_login_enable_shape);
                mBtnRegister.setEnabled(true);//设置按钮可以点击使用
            } else {
                mBtnRegister.setBackgroundResource(R.drawable.btn_login_disable_shape);
                mBtnRegister.setEnabled(false);
            }
        }
    }

    @OnClick({R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                registerDeal();
                break;
            default:
                break;
        }
    }

    //注册处理的方法实现
    private void registerDeal() {
        userName = mEtUsername.getText().toString().trim();//用户名
        firstPassword = mEtPassword.getText().toString().trim();//密码
        confirmPassword = mEtConfirmPassword.getText().toString().trim();//确认密码
        //判断两次输入的密码是否相同
        if (firstPassword.equals(confirmPassword)) {
            //创建加载进度框
            DialogUtil.createProgressDialog(this, null,
                    getString(R.string.Is_the_registered),
                    false,false);//对话框无法被取消
            //开启子线程进行注册操作
            new Thread(new Runnable() {
                public void run() {
                    try {
                        // 调用sdk注册方法
                        EMClient.getInstance().createAccount(userName, firstPassword);
                        //跳转到主线程进行UI更新
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (!RegisterActivity.this.isFinishing()&&DialogUtil.isProgressDialogShowing())
                                    //关闭进度窗口
                                    DialogUtil.closeProgressDialog();
                                // 保存用户名
                                //DemoApplication.getInstance().setCurrentUserName(username);
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.Registered_successfully),
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    } catch (final HyphenateException e) {
                        //出现异常,返回主线程进行UI提示
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (DialogUtil.isProgressDialogShowing()) {
                                    DialogUtil.closeProgressDialog();
                                }
                                int errorCode = e.getErrorCode();
                                if (errorCode == EMError.NETWORK_ERROR) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }).start();
        } else {
            Toast.makeText(
                    RegisterActivity.this,//在该activity显示
                    getResources().getString(R.string.Two_input_password),//显示的内容
                    Toast.LENGTH_SHORT).show();//显示的格式
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
