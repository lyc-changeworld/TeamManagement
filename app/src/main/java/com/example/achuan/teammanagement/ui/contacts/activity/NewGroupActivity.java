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
import com.example.achuan.teammanagement.base.SimpleActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by achuan on 17-3-9.
 * 功能：新建群组界面,进行相关配置
 * /**
 * 创建群组
 *
 * @param groupName  群组名称
 * @param desc       群组简介
 * @param allMembers 群组初始成员，如果只有自己传空数组即可
 * @param reason     邀请成员加入的reason
 * @param option     群组类型选项，可以设置群组最大用户数(默认200)及群组类型@see {@link EMGroupStyle}
 *                   option.inviteNeedConfirm表示邀请对方进群是否需要对方同意，默认是需要用户同意才能加群的。
 *                   option.extField创建群时可以为群组设定扩展字段，方便个性化订制。
 * @return 创建好的group
 * @throws HyphenateException
 */
/*EMGroupOptions option = new EMGroupOptions();
        option.maxUsers = 200;
        option.style = EMGroupStyle.EMGroupStylePrivateMemberCanInvite;

        EMClient.getInstance().groupManager().createGroup(groupName, desc, allMembers, reason, option);
        option里的GroupStyle分别为：

        EMGroupStylePrivateOnlyOwnerInvite——私有群，只有群主可以邀请人；
        EMGroupStylePrivateMemberCanInvite——私有群，群成员也能邀请人进群；
        EMGroupStylePublicJoinNeedApproval——公开群，加入此群除了群主邀请，只能通过申请加入此群；
        EMGroupStylePublicOpenJoin ——公开群，任何人都能加入此群。
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
            startActivityForResult(intent, 0);*/
        }
    }


    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


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
