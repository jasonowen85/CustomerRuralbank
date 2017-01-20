package com.grgbanking.ruralbank.main.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.main.fragment.dropbox_bank_fragment;
import com.grgbanking.ruralbank.main.fragment.dropbox_branch_fragment;
import com.grgbanking.ruralbank.main.fragment.dropbox_time_fragment;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

public class first_workorder_activity extends UI implements View.OnClickListener {
    dropbox_bank_fragment fragment_dropbox_bank;
    dropbox_branch_fragment fragment_dropbox_branch;
    dropbox_time_fragment fragment_dropbox_time;
    private String state;
    private ViewStub viewstub_maintenance, viewstub_confirmed, viewstub_evaluation, viewstub_history_workorder;

    private ImageView iv_maintenance, iv_confirmed, iv_evaluation, iv_history_workorder;

    private View currentButton;
    private View view_maintenance, view_confirmed, view_evaluation, view_history_workorder;
    protected ImageView iv_message, iv_workorder, iv_me, iv_supplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_workorder);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.workorder;
        setToolBarCenter(R.id.toolbar, options);
        toolbar.setNavigationIcon(null);
        getParams();

        init();

        if (state != null) {
            if (state != null) {
                if (state.equals("001")) {
                    iv_maintenance.setImageResource(R.drawable.up1_2);
                    iv_confirmed.setImageResource(R.drawable.up6);
                    iv_evaluation.setImageResource(R.drawable.up7);
                    iv_history_workorder.setImageResource(R.drawable.up5);
                    if (view_maintenance == null) {
                        viewstub_maintenance = (ViewStub) findViewById(R.id.viewstub_maintenance);
                        view_maintenance = viewstub_maintenance.inflate();
                    } else {
                        view_maintenance.setVisibility(View.VISIBLE);
                    }

                } else if (state.equals("003")) {
                    iv_maintenance.setImageResource(R.drawable.up1);
                    iv_confirmed.setImageResource(R.drawable.up6_2);
                    iv_evaluation.setImageResource(R.drawable.up7);
                    iv_history_workorder.setImageResource(R.drawable.up5);
                    if (view_confirmed == null) {
                        viewstub_confirmed = (ViewStub) findViewById(R.id.viewstub_confirmed);
                        view_confirmed = viewstub_confirmed.inflate();
                    } else {
                        view_confirmed.setVisibility(View.VISIBLE);
                    }
                } else if (state.equals("004")) {
                    iv_maintenance.setImageResource(R.drawable.up1);
                    iv_confirmed.setImageResource(R.drawable.up6);
                    iv_evaluation.setImageResource(R.drawable.up7_2);
                    iv_history_workorder.setImageResource(R.drawable.up5);
                    if (view_evaluation == null) {
                        viewstub_evaluation = (ViewStub) findViewById(R.id.viewstub_evaluation);
                        view_evaluation = viewstub_evaluation.inflate();
                    } else {
                        view_evaluation.setVisibility(View.VISIBLE);
                    }
                } else if (state.equals("005")) {
                    iv_maintenance.setImageResource(R.drawable.up1);
                    iv_confirmed.setImageResource(R.drawable.up6);
                    iv_evaluation.setImageResource(R.drawable.up7);
                    iv_history_workorder.setImageResource(R.drawable.up5_2);
                    if (view_history_workorder == null) {
                        viewstub_history_workorder = (ViewStub) findViewById(R.id.viewstub_history_workorder);
                        view_history_workorder = viewstub_history_workorder.inflate();
                    } else {
                        view_history_workorder.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

    }

    private void getParams() {
        state = this.getIntent().getStringExtra("state");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
        if (outState != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            // remove掉保存的Fragment
            outState.remove(FRAGMENTS_TAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).pauseRequests();
    }

    protected void init() {
        fragment_dropbox_bank = (dropbox_bank_fragment) getSupportFragmentManager().findFragmentById(R.id.dropbox_bank);
        fragment_dropbox_branch = (dropbox_branch_fragment) getSupportFragmentManager().findFragmentById(R.id.dropbox_branch);
        fragment_dropbox_time = (dropbox_time_fragment) getSupportFragmentManager().findFragmentById(R.id.dropbox_time);

        iv_me = (ImageView) findViewById(R.id.iv_me);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_workorder = (ImageView) findViewById(R.id.iv_workorder);
        iv_workorder.setImageDrawable(getResources().getDrawable(R.drawable.main_tab_item_category_focus));
        iv_me.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        iv_supplier = (ImageView) findViewById(R.id.iv_supplier);
        iv_supplier.setOnClickListener(this);
        iv_maintenance = (ImageView) findViewById(R.id.iv_maintenance);
        iv_maintenance.setOnClickListener(this);
        iv_confirmed = (ImageView) findViewById(R.id.iv_confirmed);
        iv_confirmed.setOnClickListener(this);
        iv_evaluation = (ImageView) findViewById(R.id.iv_evaluation);
        iv_evaluation.setOnClickListener(this);
        iv_history_workorder = (ImageView) findViewById(R.id.iv_history_workorder);
        iv_history_workorder.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_order:
                WorkorderSearchActivity.start(first_workorder_activity.this);
                break;
            case R.id.addwork_order:
                Intent intent = new Intent();
                intent.setClass(first_workorder_activity.this, SelectrepairsActivity.class);
                startActivities(new Intent[]{intent});
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addwork_order, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_me:
                Intent i2 = new Intent(this, MeProfileActivity.class);
                startActivity(i2);
                break;
            case R.id.iv_message:
                Intent i3 = new Intent(this, MainActivity.class);
                startActivity(i3);
                break;
            case R.id.iv_supplier:
                Intent intent = new Intent(this, SupplierListActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_maintenance:
                iv_maintenance.setImageResource(R.drawable.up1_2);
                iv_confirmed.setImageResource(R.drawable.up6);
                iv_evaluation.setImageResource(R.drawable.up7);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_maintenance == null) {
                    viewstub_maintenance = (ViewStub) findViewById(R.id.viewstub_maintenance);
                    view_maintenance = viewstub_maintenance.inflate();
                } else {
                    view_maintenance.setVisibility(View.VISIBLE);
                }
                if (view_confirmed != null)
                    view_confirmed.setVisibility(View.INVISIBLE);
                if (view_evaluation != null)
                    view_evaluation.setVisibility(View.INVISIBLE);
                if (view_history_workorder != null)
                    view_history_workorder.setVisibility(View.INVISIBLE);

                //getSupportFragmentManager().beginTransaction().hide(fragment_confirmed).hide(fragment_have_inhand).hide(fragment_evaluation).hide(fragment_history_workorder).show(fragment_maintenance).commit();
                setButton(v);
                break;

            case R.id.iv_confirmed:
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_confirmed.setImageResource(R.drawable.up6_2);
                iv_evaluation.setImageResource(R.drawable.up7);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_confirmed == null) {
                    viewstub_confirmed = (ViewStub) findViewById(R.id.viewstub_confirmed);
                    view_confirmed = viewstub_confirmed.inflate();
                } else {
                    view_confirmed.setVisibility(View.VISIBLE);
                }
                if (view_maintenance != null)
                    view_maintenance.setVisibility(View.INVISIBLE);
                if (view_evaluation != null)
                    view_evaluation.setVisibility(View.INVISIBLE);
                if (view_history_workorder != null)
                    view_history_workorder.setVisibility(View.INVISIBLE);

                //getSupportFragmentManager().beginTransaction().hide(fragment_maintenance).hide(fragment_have_inhand).hide(fragment_evaluation).hide(fragment_history_workorder).show(fragment_confirmed).commit();
                setButton(v);
                break;
            case R.id.iv_evaluation:
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_confirmed.setImageResource(R.drawable.up6);
                iv_evaluation.setImageResource(R.drawable.up7_2);
                iv_history_workorder.setImageResource(R.drawable.up5);
                if (view_evaluation == null) {
                    viewstub_evaluation = (ViewStub) findViewById(R.id.viewstub_evaluation);
                    view_evaluation = viewstub_evaluation.inflate();
                } else {
                    view_evaluation.setVisibility(View.VISIBLE);
                }
                if (view_maintenance != null)
                    view_maintenance.setVisibility(View.INVISIBLE);
                if (view_confirmed != null)
                    view_confirmed.setVisibility(View.INVISIBLE);
                if (view_history_workorder != null)
                    view_history_workorder.setVisibility(View.INVISIBLE);
                //getSupportFragmentManager().beginTransaction().hide(fragment_confirmed).hide(fragment_have_inhand).hide(fragment_maintenance).hide(fragment_history_workorder).show(fragment_evaluation).commit();
                setButton(v);
                break;
            case R.id.iv_history_workorder:
                iv_maintenance.setImageResource(R.drawable.up1);
                iv_confirmed.setImageResource(R.drawable.up6);
                iv_evaluation.setImageResource(R.drawable.up7);
                iv_history_workorder.setImageResource(R.drawable.up5_2);
                if (view_history_workorder == null) {
                    viewstub_history_workorder = (ViewStub) findViewById(R.id.viewstub_history_workorder);
                    view_history_workorder = viewstub_history_workorder.inflate();
                } else {
                    view_history_workorder.setVisibility(View.VISIBLE);
                }
                if (view_maintenance != null)
                    view_maintenance.setVisibility(View.INVISIBLE);

                if (view_confirmed != null)
                    view_confirmed.setVisibility(View.INVISIBLE);
                if (view_evaluation != null)
                    view_evaluation.setVisibility(View.INVISIBLE);

                // getSupportFragmentManager().beginTransaction().hide(fragment_confirmed).hide(fragment_have_inhand).hide(fragment_evaluation).hide(fragment_maintenance).show(fragment_history_workorder).commit();
                setButton(v);
                break;
        }
    }

    private void setButton(View v) {
        if (currentButton != null && currentButton.getId() != v.getId()) {
            currentButton.setEnabled(true);
        }
        v.setEnabled(false);
        currentButton = v;
    }
}
