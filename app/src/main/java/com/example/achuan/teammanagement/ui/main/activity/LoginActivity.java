package com.example.achuan.teammanagement.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.model.db.ContactUser;
import com.example.achuan.teammanagement.model.db.DBManager;
import com.example.achuan.teammanagement.util.DialogUtil;
import com.example.achuan.teammanagement.util.SharedPreferenceUtil;
import com.example.achuan.teammanagement.util.SnackbarUtil;
import com.example.achuan.teammanagement.util.StringUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by achuan on 17-2-27.
 * 功能：登录界面的逻辑功能
 */

public class LoginActivity extends SimpleActivity {

    public static final String TAG="LoginActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_forgetPassword)
    TextView mTvForgetPassword;
    @BindView(R.id.tv_newUser)
    TextView mTvNewUser;
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.txtInput_name)
    TextInputLayout mTxtInputName;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.txtInput_password)
    TextInputLayout mTxtInputPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    /*对应EditText控件中输入字符的引用变量*/
    String userName, password;

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initEventAndData() {
        //设置Toolbar
        setToolBar(mToolbar, getString(R.string.login), true);

        /***为Edit输入框添加输入监听类,实现合理的效果***/
        //对只有在用户名和密码的输入都不为空的情况下，button按钮才显示有效，
        // 可以自己构造一个TextChange的类，实现一个TextWatcher接口，
        // 里面有三个函数可以实现对所有text的监听。
        TextChange textChange = new TextChange();
        mEtUsername.addTextChangedListener(textChange);
        mEtPassword.addTextChangedListener(textChange);
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
                    mEtPassword.length() > 0) {
                mBtnLogin.setBackgroundResource(R.drawable.btn_login_enable_shape);
                mBtnLogin.setEnabled(true);//设置按钮可以点击使用
            } else {
                mBtnLogin.setBackgroundResource(R.drawable.btn_login_disable_shape);
                mBtnLogin.setEnabled(false);
            }
        }
    }


    /*点击监听事件入口*/
    @OnClick({R.id.tv_forgetPassword, R.id.tv_newUser, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login://登录
                loginDeal();//登录处理
                break;
            case R.id.tv_forgetPassword://忘记密码
                SnackbarUtil.showShort(view,"该功能还未实现...");
                break;
            case R.id.tv_newUser://新用户注册
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            default:break;
        }
    }

    /**1-登录的处理方法*/
    private void loginDeal() {
        userName = mEtUsername.getText().toString().trim();//用户名
        password = mEtPassword.getText().toString().trim();//密码

        //创建加载进度框
        DialogUtil.createProgressDialog(this,null,
                getString(R.string.Is_landing),
                false,false);//对话框无法被取消
        // 调用sdk登陆方法登陆聊天服务器
        /*执行登录操作,成功后还需执行两个load方法,保证进入主页面后本地会话和群组都 load 完毕*/
        //开启子线程进行登录操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().login(userName, password, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        //LogUtil.d(TAG,"登录成功");
                        //注意:这里要回到主线程中进行UI更新
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //关闭加载窗口
                                if (!LoginActivity.this.isFinishing() && DialogUtil.isProgressDialogShowing()) {
                                    DialogUtil.closeProgressDialog();
                                }

                                /*---登录成功后更新当前用户信息---*/
                                SharedPreferenceUtil.setCurrentUserName(userName);
                                /***---切换数据库文件到当前对应的用户---***/
                                /*创建一个名为xxx的数据库,而它的所有配置都会直接使用litepal.xml文件中配置的内容*/
                                LitePalDB litePalDB=LitePalDB.fromDefault(userName+"_EM");
                                LitePal.use(litePalDB);
                                //切换回litepal.xml中指定的默认数据库
                                //LitePal.useDefault();

                                //提示登录成功
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.Login_successfully),
                                        Toast.LENGTH_SHORT).show();

                                // ** 第一次登录或者之前logout后再登录,加载所有本地群和会话
                                // ** manually load all local groups and
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                                getFriends();//加载好友信息
                                //跳转到主页面
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    @Override
                    public void onProgress(int progress, String status) {
                    }
                    @Override
                    public void onError(final int code, final String message) {
                        //发生错误时,在主线程中进行警告提示
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (DialogUtil.isProgressDialogShowing()) {
                                    DialogUtil.closeProgressDialog();
                                }
                                //根据不同的错误码进行不同的提醒
                                if(code== EMError.USER_NOT_FOUND){
                                    //不存在此用户 错误码:204
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.User_not_found),
                                            Toast.LENGTH_SHORT).show();
                                }else if(code==EMError.USER_AUTHENTICATION_FAILED){
                                    //用户id或密码错误 错误码:202
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.UserName_or_password_is_wrong),
                                            Toast.LENGTH_SHORT).show();
                                }else if(code==EMError.SERVER_TIMEOUT){
                                    //等待服务器响应超时 错误码:301
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.Wait_for_server_response_timeout),
                                            Toast.LENGTH_SHORT).show();
                                }else if(code==EMError.USER_LOGIN_ANOTHER_DEVICE) {
                                    //账户在另外一台设备登录 错误码:206
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.User_login_on_another_device),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.Login_failed) + message,
                                            Toast.LENGTH_SHORT).show();
                                }
                                //用来测试获取错误码,错误码对应错误信息见以下地址:
                                //http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                        /*Toast.makeText(getApplicationContext(), ""+code,
                                Toast.LENGTH_SHORT).show();*/
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**2-获取好友列表,并存储到本地数据库中*/
    private  void  getFriends(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取网络端的所有好友列表
                    List<String> usernames = EMClient.getInstance().contactManager().
                            getAllContactsFromServer();
                    Map<String ,ContactUser> users=new HashMap<String ,ContactUser>();
                    for(String username:usernames){
                        //测试打印当前用户的联系人的名称
                        //LogUtil.d(TAG,username);
                        ContactUser user=new ContactUser();
                        user.setUserName(username);
                        //设置首字母
                        user.setInitialLetter(StringUtil.getHeadChar(username));
                        users.put(username, user);
                    }
                    //保存联系人到本地数据库
                    DBManager.saveContactList(new ArrayList<ContactUser>(users.values()));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
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
