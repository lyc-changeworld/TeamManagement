package com.example.achuan.teammanagement.ui.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.app.Constants;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.util.DialogUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hyphenate.chat.EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
import static com.hyphenate.chat.EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
import static com.hyphenate.chat.EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
import static com.hyphenate.chat.EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;

/**
 * Created by achuan on 17-3-9.
 * 功能：新建群组界面,进行相关配置
 */

public class NewGroupActivity extends SimpleActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.btn_save_group)
    Button mBtnSaveGroup;
    @BindView(R.id.et_groupName)
    EditText mEtGroupName;
    @BindView(R.id.et_desc)
    EditText mEtDesc;
    @BindView(R.id.cb_public)
    AppCompatCheckBox mCbPublic;
    @BindView(R.id.cb_member_inviter)
    AppCompatCheckBox mCbMemberInviter;
    @BindView(R.id.tv_second_desc)
    TextView mTvSecondDesc;

    //boolean groupStyle=;



    @Override
    protected int getLayout() {
        return R.layout.activity_add_group;
    }

    @Override
    protected void initEventAndData() {
        /*1-初始化控件*/
        //设置toolbar
        setToolBar(mToolbar, getString(R.string.add_group), true);
        //设置保存按钮的文本
        mBtnSaveGroup.setText(getString(R.string.save));
        //为是否公开的复选框设置状态改变监听事件
        mCbPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTvSecondDesc.setText(R.string.isJoinNeedApproval);
                }else {
                    mTvSecondDesc.setText(R.string.isMemberCanInvite);
                }
            }
        });


    }

    private void save(){
        String name=mEtGroupName.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,
                    R.string.Group_name_cannot_be_empty,
                    Toast.LENGTH_SHORT).show();
        }else {
            //跳转到联系人选择界面
            /*Intent intent=new Intent(this, GroupPickContactsActivity.class);
            intent.putExtra("groupName", name);
            //该启动方法会在后者活动销毁后返回信息过来,0代表消息回执码
            startActivityForResult(intent, Constants.GROUP_PICK_CONTACTS_REQUEST_CODE);*/
        }
    }


    //从联系人选择界面回来后执行的结果回执方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //核对请求码,判断数据来源
        switch (requestCode){
            case Constants.GROUP_PICK_CONTACTS_REQUEST_CODE:
                //判断数据处理结果是否成功,然后会拿到另外一个活动传递过来的数据
                if(resultCode==RESULT_OK){
                    //创建加载对话框
                    DialogUtil.createProgressDialog(this,"",
                            getString(R.string.Is_to_create_a_group_chat),
                            false,false);
                    //开启子线程进行操作
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                            /*-----进行新建群组的相关配置-----*/
                                //1.获取输入的群组名和简介
                                String groupName=mEtGroupName.getText().toString().trim();
                                String desc=mEtDesc.getText().toString().trim();
                                //2.获取到传递过来的群组初始成员数组
                                String[] members = data.getStringArrayExtra("newmembers");
                                //3.设置邀请成员加入的reason(当前用户+邀请加入+群名称)
                                String reason=EMClient.getInstance().getCurrentUser()+
                                        getString(R.string.invite_join_group)+groupName;
                                //4.创建配置参数的实例对象,然后设置相关属性
                                EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                                option.maxUsers=200;//设置群组最大用户数(默认200)
                                //判断复选框的选择状态对权限进行设定
                        /*option里的GroupStyle分别为：
                        EMGroupStylePrivateOnlyOwnerInvite——私有群，只有群主可以邀请人；
                        EMGroupStylePrivateMemberCanInvite——私有群，群成员也能邀请人进群；
                        EMGroupStylePublicJoinNeedApproval——公开群，加入此群除了群主邀请，只能通过申请加入此群；
                        EMGroupStylePublicOpenJoin ——公开群，任何人都能加入此群。*/
                                if(mCbPublic.isChecked()){
                                    option.style = mCbMemberInviter.isChecked() ?
                                            EMGroupStylePublicJoinNeedApproval :
                                            EMGroupStylePublicOpenJoin;
                                }else{
                                    option.style = mCbMemberInviter.isChecked()?
                                            EMGroupStylePrivateMemberCanInvite:
                                            EMGroupStylePrivateOnlyOwnerInvite;
                                }
                                //---执行创建群组的方法
                                EMClient.getInstance().groupManager().createGroup(
                                        groupName,desc,members,reason,option);
                                //回到主线程进行UI更新
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(DialogUtil.isProgressDialogShowing()){
                                            DialogUtil.closeProgressDialog();
                                        }
                                        setResult(RESULT_OK);//告诉上一个活动操作执行成功了
                                        finish();//结束当前活动
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                //回到主线程进行UI更新
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(DialogUtil.isProgressDialogShowing()){
                                            DialogUtil.closeProgressDialog();
                                        }
                                        //对异常进行提示
                                        Toast.makeText(NewGroupActivity.this,
                                                getString(R.string.Failed_to_create_groups)+
                                                        e.getLocalizedMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
                break;
            default:break;
        }
    }


    @OnClick({R.id.btn_save_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save_group:
                save();
                break;
            default:break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


}
