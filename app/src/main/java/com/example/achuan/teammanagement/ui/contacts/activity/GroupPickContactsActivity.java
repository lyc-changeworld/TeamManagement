package com.example.achuan.teammanagement.ui.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.app.Constants;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.model.db.ContactUser;
import com.example.achuan.teammanagement.model.db.DBManager;
import com.example.achuan.teammanagement.ui.contacts.adapter.PickContactAdapter;
import com.example.achuan.teammanagement.widget.RyItemDivider;
import com.example.achuan.teammanagement.widget.SideBar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by achuan on 17-3-9.
 * 功能：联系人选择界面
 */

public class GroupPickContactsActivity extends SimpleActivity {

    public static final String TAG="GroupPickContactsActivity";


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_floating_header)
    TextView mTvFloatingHeader;
    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.sidebar)
    SideBar mSidebar;
    @BindView(R.id.btn_save_contacts)
    Button mBtnSaveContacts;

    /**
     * if this is a new group
     */
    protected boolean isCreatingNewGroup;
    /**
     * members already in the group
     */
    private List<String> existMembers;
    //private boolean[] isCheckedArray;//记录item是否被选中

    List<ContactUser> mContactUserList;//本地联系人集合
    LinearLayoutManager linearlayoutManager;
    private PickContactAdapter mPickContactAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_group_pick_contacts;
    }

    @Override
    protected void initEventAndData() {
        //设置标题工具栏
        setToolBar(mToolbar, getString(R.string.invite_friend), true);
        mBtnSaveContacts.setText(R.string.save);//设置标题中右侧按钮的文本
        /**1-初始化判断是否为创建新群组*/
        //获取传递过来的群ID号,没有就说明是刚创建群
        String groupId = getIntent().getStringExtra(Constants.GROUP_ID);
        if (groupId == null) {// create new group
            isCreatingNewGroup = true;
        } else {
            // get members of the group
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            existMembers = group.getMembers();
        }
        if (existMembers == null) {
            //为空说明该群是新群(空的),创建集合来添加将要邀请的用户成员
            existMembers = new ArrayList<String>();
        }

        /***2-对列表的布局显示进行设置***/
        //先获取本地联系人集合数据
        mContactUserList= new ArrayList<ContactUser>(
                DBManager.getContactList().values());
        /*让数组中的数据按照compareTo方法中的规则返回的结果进行排序*/
        Collections.sort(mContactUserList);
        //适配器和布局对象
        mPickContactAdapter = new PickContactAdapter(this, mContactUserList,existMembers);
        linearlayoutManager = new LinearLayoutManager(this);
        //设置方向(默认是垂直,下面的是水平设置)
        //linearlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv.setLayoutManager(linearlayoutManager);//为列表添加布局
        mRv.setAdapter(mPickContactAdapter);//为列表添加适配器
        //添加自定义的分割线
        mRv.addItemDecoration(new RyItemDivider(this, R.drawable.di_item));

        /**3-为列表控件添加监听事件*/
        //添加item点击监听
        mPickContactAdapter.setOnClickListener(new PickContactAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int postion) {
                //点击item触发对应item中的checkButton
                AppCompatCheckBox checkBox = (AppCompatCheckBox) view.findViewById(R.id.cb_contact);
                checkBox.toggle();//切换复选框的状态
            }
        });

        /**4-设置索引栏点击监听事件,实现点击字母实现列表栏定位移动*/
        mSidebar.setTextView(mTvFloatingHeader);//添加中间部分显示控件
        mSidebar.setOnTouchingLetterChangedListener(new SideBar.OnChooseLetterChangedListener() {
            @Override
            public void onChooseLetter(String s) {
                //这部分后续再实现

            }
            @Override
            public void onNoChooseLetter() {

            }
        });
    }


    /*获取选中联系人名称集合的方法*/
    private List<String> getToBeAddMembers() {
        List<String> members = new ArrayList<String>();
        int length = mPickContactAdapter.getIsCheckedArray().length;
        for (int i = 0; i < length; i++) {
            String username = mContactUserList.get(i).getUserName();
            if (mPickContactAdapter.getIsCheckedArray()[i] && !existMembers.contains(username)) {
                members.add(username);
            }
        }
        return members;
    }

    //保存选中的联系人的方法
    private void save() {
        List<String> var = getToBeAddMembers();
        /*for (int i = 0; i <var.size() ; i++) {
            LogUtil.d(TAG,"包含："+var.get(i));
        }*/
        Intent intent=new Intent();
        //传递数据给上一个活动
        intent.putExtra(Constants.NEW_MEMBERS,var.toArray(new String[var.size()]));
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick({R.id.btn_save_contacts})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_save_contacts:
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
