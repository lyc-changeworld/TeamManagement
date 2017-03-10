package com.example.achuan.teammanagement.ui.contacts.activity;

import com.example.achuan.teammanagement.R;
import com.example.achuan.teammanagement.base.SimpleActivity;
import com.example.achuan.teammanagement.ui.contacts.adapter.PickContactAdapter;

import java.util.List;

/**
 * Created by achuan on 17-3-9.
 */

public class GroupPickContactsActivity extends SimpleActivity {

    /** if this is a new group */
    protected boolean isCreatingNewGroup;
    private PickContactAdapter mPickContactAdapter;
    /** members already in the group */
    private List<String> existMembers;

    @Override
    protected int getLayout() {
        return R.layout.activity_group_pick_contacts;
    }

    @Override
    protected void initEventAndData() {

    }
}
