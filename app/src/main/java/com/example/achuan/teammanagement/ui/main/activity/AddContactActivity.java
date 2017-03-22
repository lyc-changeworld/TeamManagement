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
import com.example.achuan.teammanagement.model.http.EaseMobHelper;
import com.example.achuan.teammanagement.util.DialogUtil;
import com.hyphenate.EMCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by achuan on 17-3-1.
 * 功能：添加联系人界面
 */

public class AddContactActivity extends SimpleActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.btn_add_contact)
    Button mBtnAddContact;
    @BindView(R.id.et_reason)
    EditText mEtReason;

    @Override
    protected int getLayout() {
        return R.layout.activity_add_contact;
    }

    @Override
    protected void initEventAndData() {
        //设置标题栏
        setToolBar(mToolbar, getString(R.string.add_contact), true);

        mEtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEtUsername.getText().length() > 0) {
                    mBtnAddContact.setBackgroundResource(R.drawable.btn_login_enable_shape);
                    mBtnAddContact.setEnabled(true);//设置按钮可以点击使用
                } else {
                    mBtnAddContact.setBackgroundResource(R.drawable.btn_login_disable_shape);
                    mBtnAddContact.setEnabled(false);
                }
            }
        });
    }

    @OnClick({R.id.btn_add_contact})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_contact:
                addContact();
                break;
            default:break;
        }
    }

    /**
     * 添加contact
     */
    public void addContact() {
        final String userName = mEtUsername.getText().toString().trim();//用户名
        final String reason=mEtReason.getText().toString().trim();//邀请语

        DialogUtil.createProgressDialog(this, null,
                getResources().getString(R.string.Is_sending_a_request),
                false, false);

        new Thread(new Runnable() {
            public void run() {
                EaseMobHelper.getInstance().addContact(userName, reason, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                if (DialogUtil.isProgressDialogShowing()) {
                                    DialogUtil.closeProgressDialog();
                                }

                                String s1 = getResources().getString(R.string.send_successful);
                                Toast.makeText(getApplicationContext(), s1,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onError(int code, final String error) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (DialogUtil.isProgressDialogShowing()) {
                                    DialogUtil.closeProgressDialog();
                                }
                                String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                                Toast.makeText(getApplicationContext(), s2 + error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }).start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


}
