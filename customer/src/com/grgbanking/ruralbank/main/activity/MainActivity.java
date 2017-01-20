package com.grgbanking.ruralbank.main.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.login.LogoutHelper;
import com.grgbanking.ruralbank.main.fragment.SessionListFragment;
import com.grgbanking.ruralbank.session.SessionHelper;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * 主界面
 * <p/>
 * Created by huangjun on 2015/3/25.
 */
public class MainActivity extends UI implements View.OnClickListener {

    private static final String EXTRA_APP_QUIT = "APP_QUIT";
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int BASIC_PERMISSION_REQUEST_CODE = 100;

    private SessionListFragment mainFragment;
    protected ImageView iv_message, iv_workorder, iv_me,iv_supplier;


    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    // 注销
    public static void logout(Context context, boolean quit) {
        Intent extra = new Intent();
        extra.putExtra(EXTRA_APP_QUIT, quit);
        start(context, extra);
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.message;
        setToolBarCenter(R.id.toolbar, options);
        toolbar.setNavigationIcon(null);

        onParseIntent();

        DialogMaker.showProgressDialog(this, null, getString(R.string.logining), true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        }).setCanceledOnTouchOutside(false);

        // 等待同步数据完成
        boolean syncCompleted = LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(new Observer<Void>() {
            @Override
            public void onEvent(Void v) {
                DialogMaker.dismissProgressDialog();
            }
        });

        Log.i(TAG, "sync completed = " + syncCompleted);
        if (!syncCompleted) {
            DialogMaker.showProgressDialog(MainActivity.this, getString(R.string.prepare_data)).setCanceledOnTouchOutside(false);
        } else{
            DialogMaker.dismissProgressDialog();
        }
        onInit();
    }

    private void onInit() {
        iv_me = (ImageView) findViewById(R.id.iv_me);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_message.setImageDrawable(getResources().getDrawable(R.drawable.main_tab_item_home_focus));
        iv_workorder = (ImageView) findViewById(R.id.iv_workorder);
        iv_supplier = (ImageView) findViewById(R.id.iv_supplier);
        iv_me.setOnClickListener(this);
        iv_workorder.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        iv_supplier.setOnClickListener(this);
        // 加载主页面
        showMainFragment();

        LogUtil.ui("NIM SDK cache path=" + NIMClient.getSdkStorageDirPath());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onParseIntent();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_btn:
                GlobalSearchActivity.start(MainActivity.this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onParseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            IMMessage message = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    SessionHelper.startP2PSession(this, message.getSessionId());
                    break;
                case Team:
                    SessionHelper.startTeamSession(this, message.getSessionId());
                    break;
                default:
                    break;
            }
        } else if (intent.hasExtra(EXTRA_APP_QUIT)) {
            onLogout();
            return;
        } else if (intent.hasExtra(com.grgbanking.ruralbank.main.model.Extras.EXTRA_JUMP_P2P)) {
            Intent data = intent.getParcelableExtra(com.grgbanking.ruralbank.main.model.Extras.EXTRA_DATA);
            String account = data.getStringExtra(com.grgbanking.ruralbank.main.model.Extras.EXTRA_ACCOUNT);
            if (!TextUtils.isEmpty(account)) {
                SessionHelper.startP2PSession(this, account);
            }
        }
    }

    private void showMainFragment() {
        if (mainFragment == null && !isDestroyedCompatible()) {
            mainFragment = new SessionListFragment();
            switchFragmentContent(mainFragment);
        }
    }

    // 注销
    private void onLogout() {
        // 清理缓存&注销监听
        LogoutHelper.logout();

        // 启动登录
        LoginActivity.start(this);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_workorder:
                Intent i = new Intent(this, first_workorder_activity.class);
                i.putExtra("state","001");
                startActivity(i);
                break;
            case R.id.iv_me:
                Intent i2 = new Intent(this,MeProfileActivity.class);
                startActivity(i2);
                break;
            case R.id.iv_message:
//                Intent i3=new Intent(this,ContactListActivity.class);
//                startActivity(i3);
                break;
            case R.id.iv_supplier:
//                Intent i4 = new Intent(this, first_supplier_activity.class);
//                startActivity(i4);
                Intent intent = new Intent(this,SupplierListActivity.class);
                startActivity(intent);
                break;
        }
    }
}
