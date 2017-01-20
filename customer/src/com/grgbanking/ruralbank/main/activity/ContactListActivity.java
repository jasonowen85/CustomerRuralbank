package com.grgbanking.ruralbank.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.grgbanking.ruralbank.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.contact.ContactsFragment;


/**
 * 集成通讯录列表
 * <p/>
 * Created by huangjun on 2015/9/7.
 */
public class ContactListActivity extends UI implements View.OnClickListener {

    private ContactsFragment fragment;
    protected ImageView iv_message, iv_workorder, iv_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list);
        setToolBar(R.id.toolbar, R.string.app_name, R.drawable.actionbar_dark_logo);

        setTitle("联系人");
    }

    @Override
    protected void onResume() {
        super.onResume();
        onInit();
    }

    protected void onInit() {
        iv_me = (ImageView) findViewById(R.id.iv_me);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_workorder = (ImageView) findViewById(R.id.iv_workorder);
        iv_workorder.setImageDrawable(getResources().getDrawable(R.drawable.main_tab_item_category_focus));
        iv_me.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        addContactFragment();  // 集成通讯录页面
    }

    // 将通讯录列表fragment动态集成进来。 开发者也可以使用在xml中配置的方式静态集成。
    private void addContactFragment() {
        fragment = new ContactsFragment();
        fragment.setContainerId(R.id.contact_fragment);

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        fragment = (ContactsFragment) this.addFragment(fragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_me:
                Intent i2 = new Intent(this, MeProfileActivity.class);
                startActivity(i2);
                finish();
                break;
            case R.id.iv_message:
                Intent i3 = new Intent(this, MainActivity.class);
                startActivity(i3);
                finish();
                break;
        }
    }
}
