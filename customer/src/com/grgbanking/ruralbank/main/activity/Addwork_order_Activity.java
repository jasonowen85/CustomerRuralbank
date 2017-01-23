package com.grgbanking.ruralbank.main.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ruralbank.DemoCache;
import com.grgbanking.ruralbank.NimApplication;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ApiHttpClient;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.common.photo.AlbumActivity;
import com.grgbanking.ruralbank.common.photo.GalleryActivity;
import com.grgbanking.ruralbank.common.photo.adapter.MyAdapter;
import com.grgbanking.ruralbank.common.photo.adapter.PictureAdapter;
import com.grgbanking.ruralbank.common.photo.model.ImageBean;
import com.grgbanking.ruralbank.common.photo.utils.Bimp;
import com.grgbanking.ruralbank.common.photo.utils.BitmapUtils;
import com.grgbanking.ruralbank.common.photo.utils.FileUtils;
import com.grgbanking.ruralbank.common.photo.view.NoScrollGridView;
import com.grgbanking.ruralbank.common.util.PermissionUtils;
import com.grgbanking.ruralbank.common.util.ToastUtils;
import com.grgbanking.ruralbank.common.util.sys.ImageUtils;
import com.grgbanking.ruralbank.config.preference.Preferences;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.main.helper.RecordButton;
import com.grgbanking.ruralbank.main.helper.RecordButtonUtil;
import com.grgbanking.ruralbank.main.model.AddworderBean;
import com.grgbanking.ruralbank.main.model.Extras;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Addwork_order_Activity extends UI implements View.OnClickListener {
    private TextView repairs_form;
    private EditText repairs_contacts, et_mailing_address, contact_number, et_express, courier_number, nummber, note, et_write;
    private Spinner deviceTypes, models, supplier;
    private ImageView iv_present, iv_mike, iv_yes, iv_add;
    private String jobOrderType, comAddress, network, userId, deviceNum, express, courierNum,
            phone, images, modelid, modelname, deviceId, remark, voice, voiceStr, SupplierId, account, deviceName;
    private AddworderBean bean;
    private ArrayAdapter adapter_supplier;
    private ArrayAdapter adapter_deviceType;
    private ArrayAdapter adapter_model;
    List<AddworderBean> addwordBean_supplier = null;
    List<AddworderBean> addwordBean_deviceType = null;
    List<AddworderBean> addworderBeen_model = null;
    private String supplierId;
    private String equipmentId;
    private RecordButton mBtnRecort;
    private RelativeLayout mLayout;
    private ImageView mImgVolume, mImgLayout, mImgDeleteRecord;
    private TextView mTvTime;
    private AnimationDrawable drawable; // 录音播放时的动画背景
    private NoScrollGridView noScrollGridView;
    private PictureAdapter adapter;
    private com.grgbanking.ruralbank.common.photo.popwindow.SelectPicPopupWindow menuWindow;
    private Addwork_order_Activity instence;
    private List<String> urllist;
    private static final int TAKE_PICTURE = 999;
    private String filepath;
    private String urls;
    private String rowUrl;
    private String mSupplierid;
    private String mSuppliername;
    private String mUserid;

    public static void start(Context context, String account) {
        Intent intent = new Intent();
        intent.setClass(context, MeProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwork_order);
        instence = this;
        urllist = new ArrayList<String>();
        init();
        getParams();

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.returns;
        setToolBar(R.id.toolbar, options);

    }

    private void getParams() {
        mSupplierid = this.getIntent().getStringExtra("supplierid");
        mSuppliername = this.getIntent().getStringExtra("suppliername");
        mUserid = this.getIntent().getStringExtra("userid");


        if (mSupplierid != null) {
            addwordBean_supplier = new ArrayList<>();
            bean = new AddworderBean();
            bean.setSupplier(mSuppliername);
            bean.setSupplierId(mSupplierid);
            addwordBean_supplier.add(bean);
            updateSupplierUI();
        } else {
            getdeviceTypes("");
        }
    }

    private void init() {

        account = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        if (account == null || account.equals("")) {
            account = DemoCache.getAccount();
        }
        repairs_form = (TextView) findViewById(R.id.et_repairs_form);
        (repairs_contacts = (EditText) findViewById(R.id.et_repairs_contacts)).setOnClickListener(this);
        if (DemoCache.getAccount().equals(account)) {
            repairs_contacts.setText(NimUserInfoCache.getInstance().getUserName(account));
        }

        et_mailing_address = (EditText) findViewById(R.id.et_mailing_address);
        if (Preferences.getBranch() != null) {
            et_mailing_address.setText(Preferences.getBranch());
        }

        (contact_number = (EditText) findViewById(R.id.et_contact_number)).setOnClickListener(this);

        if(account.contains(NimApplication.sendApkName)){
            contact_number.setText(account.substring(NimApplication.sendApkName.length()));
        } else {
            contact_number.setText(account);
        }

        adapter = new PictureAdapter(this);
        noScrollGridView = (NoScrollGridView) findViewById(R.id.noScrollgridview);
        noScrollGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        noScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideInput(Addwork_order_Activity.this.getApplicationContext(), repairs_contacts);
                if (i == Bimp.getTempSelectBitmap().size()) {
                    selectImgs();
                } else {
                    Intent intent = new Intent(instence,
                            GalleryActivity.class);
                    intent.putExtra("ID", i);
                    startActivity(intent);
                }
            }
        });
        noScrollGridView.setAdapter(adapter);
        et_express = (EditText) findViewById(R.id.et_express);
        courier_number = (EditText) findViewById(R.id.et_courier_number);

        (et_write = (EditText) findViewById(R.id.iv_write)).addTextChangedListener(mtextWatcher);

        deviceTypes = (Spinner) findViewById(R.id.et_equipments);
        supplier = (Spinner) findViewById(R.id.supplier);
        models = (Spinner) findViewById(R.id.et_model);
        (nummber = (EditText) findViewById(R.id.et_nummber)).setOnClickListener(this);
        (note = (EditText) findViewById(R.id.et_note)).setOnClickListener(this);
        (iv_present = (ImageView) findViewById(R.id.iv_yes)).setOnClickListener(this);
        (iv_mike = (ImageView) findViewById(R.id.mike)).setOnClickListener(this);
        (iv_yes = (ImageView) findViewById(R.id.iv_yes)).setOnClickListener(this);
        //(iv_add = (ImageView) findViewById(R.id.iv_add)).setOnClickListener(this);
        note = (EditText) findViewById(R.id.et_note);
        mBtnRecort = (RecordButton) findViewById(R.id.record_btn);
        mLayout = (RelativeLayout) findViewById(R.id.tweet_layout_record);
        mImgLayout = (ImageView) findViewById(R.id.tweet_img_layout);
        mImgDeleteRecord = (ImageView) findViewById(R.id.delete_record);
        mImgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnRecort.playRecord();
            }
        });
        mImgDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnRecort.cancelRecord();
                mLayout.setVisibility(View.GONE);
            }
        });
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBtnRecort
                .getLayoutParams();
        params.width = getScreenW(this);
        params.height = (int) (getScreenH(this) * 0.4);
        mBtnRecort.setLayoutParams(params);
        mTvTime = (TextView) findViewById(R.id.tweet_time_record);
        mImgVolume = (ImageView) findViewById(R.id.tweet_img_volume);
        mBtnRecort.setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int recordTime) {
                mLayout.setVisibility(View.VISIBLE);
                if (recordTime < 10) {
                    mTvTime.setText("0" + recordTime + "\"");
                } else {
                    mTvTime.setText(recordTime + "\"");
                }
            }

            @Override
            public void onCancleRecord() {
                mLayout.setVisibility(View.GONE);
            }
        });

        drawable = (AnimationDrawable) mImgVolume.getBackground();
        mBtnRecort.getAudioUtil().setOnPlayListener(new RecordButtonUtil.OnPlayListener() {
            @Override
            public void stopPlay() {
                drawable.stop();
                mImgVolume.setBackgroundDrawable(drawable.getFrame(0));
            }

            @Override
            public void starPlay() {
                mImgVolume.setBackgroundDrawable(drawable);
                drawable.start();
            }
        });
    }

    TextWatcher mtextWatcher = new TextWatcher() {

        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = et_write.getSelectionStart();
            editEnd = et_write.getSelectionEnd();
            if (temp.length() > 200) {
                ToastUtils.showToast(instence, "故障描述限200字");
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                et_write.setText(s);
                et_write.setSelection(tempSelection);
            }
        }
    };


    /**
     * 强制隐藏输入法键盘
     */
    private void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void selectImgs() {
        //  ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(instence.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        menuWindow = new com.grgbanking.ruralbank.common.photo.popwindow.SelectPicPopupWindow(Addwork_order_Activity.this, itemsOnClick);
        //设置弹窗位置
        menuWindow.showAtLocation(Addwork_order_Activity.this.findViewById(R.id.ll_first_my_fragment), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_camera:        //点击拍照按钮

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(Addwork_order_Activity.this, PermissionUtils.PERMISSION_CAMERA) ==
                                PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(Addwork_order_Activity.this,
                                    new String[]{PermissionUtils.PERMISSION_CAMERA}, PermissionUtils.CODE_CAMERA);
                        } else {
                            goCamera();
                        }
                    } else {
                        goCamera();
                    }


                    break;
                case R.id.item_popupwindows_Photo:       //点击从相册中选择按钮
                    if (Build.VERSION.SDK_INT >= 23) {
                        //读取sd卡权限；
                        if (ContextCompat.checkSelfPermission(Addwork_order_Activity.this, PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(Addwork_order_Activity.this,
                                    new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, PermissionUtils.CODE_READ_EXTERNAL_STORAGE);
                        } else {
                            Intent intent = new Intent(instence,
                                    AlbumActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(instence,
                                AlbumActivity.class);
                        startActivity(intent);
                    }

                    break;
                default:
                    break;
            }
        }
    };

    private void goCamera() {

        filepath = FileUtils.iniFilePath(Addwork_order_Activity.this);
        urllist.add(filepath);
        File file = new File(filepath);
        // 启动Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, TAKE_PICTURE);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        LogUtil.d(TAG, "权限申请 回调 申请的permissions= " + permissions[0].toString());
        switch(permsRequestCode) {
            case PermissionUtils.CODE_RECORD_AUDIO:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(this, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        PermissionUtils.confirmActivityPermission(this, new String[]{PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE},
                                PermissionUtils.CODE_RECORD_AUDIO, getString(R.string.readSDcard), false);
                    } else {
                        if(mBtnRecort != null)
                            mBtnRecort.requestPermissionAudio();
                    }

                } else {
                    if (ContextCompat.checkSelfPermission(this, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        //重新申请 sd 录音权限
                        PermissionUtils.confirmActivityPermission(this, permissions, PermissionUtils.CODE_RECORD_AUDIO, getString(R.string.recordAudio), false);
                    } else {
                        //只申请  录音权限
                        PermissionUtils.confirmActivityPermission(this, new String[]{PermissionUtils.PERMISSION_RECORD_AUDIO},
                                PermissionUtils.CODE_RECORD_AUDIO, getString(R.string.recordAudio), false);
                    }
                }

                break;
            case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(instence,
                            AlbumActivity.class);
                    startActivity(intent);
                } else {
                    PermissionUtils.confirmActivityPermission(this, new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE},
                            PermissionUtils.CODE_READ_EXTERNAL_STORAGE, getString(R.string.readSDcard), false);
                }
                break;

            case PermissionUtils.CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goCamera();
                } else {
                    PermissionUtils.confirmActivityPermission(this, new String[]{PermissionUtils.PERMISSION_CAMERA},
                            PermissionUtils.CODE_CAMERA, getString(R.string.camera), false);
                }
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = BitmapUtils.getCompressedBitmap(Addwork_order_Activity.this, filepath);
                    FileUtils.saveBitmap(bm, fileName);

                    ImageBean takePhoto = new ImageBean();
                    takePhoto.setBitmap(bm);
                    takePhoto.setPath(filepath);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public static int getScreenW(Context aty) {
        new DisplayMetrics();
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        return w;
    }

    public static int getScreenH(Context aty) {
        new DisplayMetrics();
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        int h = dm.heightPixels;
        return h;
    }

    @Override
    protected void onDestroy() {
        Bimp.tempSelectBitmap.clear();
        MyAdapter.mSelectedImage.clear();
        super.onDestroy();
    }

    private void getSuppliers(String type) {
        ServerApi.getSuppliers(type, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = null;
                String ret_msg = null;
                String json = response.toString();
                addwordBean_supplier = new ArrayList<>();
                try {
                    if (response.getString("ret_code").equals("0")) {
                        JSONArray jsonArray = response.getJSONArray("lists");
                        int iSize = jsonArray.length();
                        for (int i = 0; i < iSize; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bean = new AddworderBean();
                            bean.setSupplier(jsonObject.optString("name"));
                            bean.setSupplierId(jsonObject.optString("id"));
                            addwordBean_supplier.add(bean);

                        }
                        updateSupplierUI();
                    } else {
                        Toast.makeText(Addwork_order_Activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(Addwork_order_Activity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Log.e("TAG", "reset password fail:" + throwable.getMessage());
            }
        });
    }

    private void updateSupplierUI() {
        String[] addlist = new String[addwordBean_supplier.size()];
        for (int i = 0; i < addwordBean_supplier.size(); i++) {
            addlist[i] = addwordBean_supplier.get(i).getSupplier();
        }
        adapter_supplier = new ArrayAdapter(this, R.layout.spinner_supplier, R.id.text, addlist);
        supplier.setAdapter(adapter_supplier);
        supplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                supplierId = addwordBean_supplier.get(position).getSupplierId();

                getdeviceModels();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                supplierId = null;
            }
        });
        adapter_supplier.notifyDataSetChanged();
        supplier.setSelection(0, true);
        if (mSupplierid != null) {
            getdeviceTypes(mSupplierid);
        }
    }

    private void getdeviceTypes(String supplier) {
        ServerApi.getdeviceTypes(supplier, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                addwordBean_deviceType = new ArrayList<>();
                try {
                    if (response.getString("ret_code").equals("0")) {
                        JSONArray jsonArray = response.getJSONArray("lists");
                        int iSize = jsonArray.length();
                        for (int i = 0; i < iSize; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bean = new AddworderBean();
                            bean.setEquipment(jsonObject.optString("name"));
                            bean.setEquipmentId(jsonObject.optString("id"));
                            addwordBean_deviceType.add(bean);
                        }
                        updateDeviceTypeUI();

                    } else {
                        Toast.makeText(Addwork_order_Activity.this, response.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                        if (response.getString("ret_code").equals("0011")) {
                            Intent intent = new Intent(Addwork_order_Activity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Log.e("TAG", "reset password fail:" + throwable.getMessage());
                //  Toast.makeText(Addwork_order_Activity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                LogUtil.e("TAG", "login fail:" + throwable.getMessage());
            }
        });
    }

    private void updateDeviceTypeUI() {

        String[] addlist = new String[addwordBean_deviceType.size()];
        for (int i = 0; i < addwordBean_deviceType.size(); i++) {
            addlist[i] = addwordBean_deviceType.get(i).getEquipment();
        }
        adapter_deviceType = new ArrayAdapter(this, R.layout.spinner_supplier, R.id.text, addlist);
        deviceTypes.setAdapter(adapter_deviceType);
        deviceTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                equipmentId = addwordBean_deviceType.get(position).getEquipmentId();
                if (mSupplierid == null) {
                    getSuppliers(equipmentId);
                }
                getdeviceModels();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                equipmentId = null;
            }
        });
        adapter_deviceType.notifyDataSetChanged();
        deviceTypes.setSelection(0, true);


    }


    private void getdeviceModels() {
        if (supplierId == null || supplierId.equals("") ||
                equipmentId == null || equipmentId.equals("")) {
            return;
        }
        ServerApi.getdeviceModels(supplierId, equipmentId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = null;
                String ret_msg = null;
                String json = response.toString();
                addworderBeen_model = new ArrayList<>();
                try {
                    if (response.getString("ret_code").equals("0")) {
                        JSONArray jsonArray = response.getJSONArray("lists");
                        int iSize = jsonArray.length();
                        for (int i = 0; i < iSize; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bean = new AddworderBean();
                            bean.setModel(jsonObject.optString("name"));
                            bean.setModelId(jsonObject.optString("id"));
                            addworderBeen_model.add(bean);

                        }
                        updateModelUI();
                    } else {
                        Toast.makeText(Addwork_order_Activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(Addwork_order_Activity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Log.e("TAG", "reset password fail:" + throwable.getMessage());
                //  Toast.makeText(Addwork_order_Activity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Log.e("TAG", "reset password fail:" + throwable.getMessage());
            }
        });
    }

    private void updateModelUI() {
        String[] addlist = new String[addworderBeen_model.size()];
        for (int i = 0; i < addworderBeen_model.size(); i++) {
            addlist[i] = addworderBeen_model.get(i).getModel();
        }
        adapter_model = new ArrayAdapter(this, R.layout.spinner_supplier, R.id.text, addlist);
        models.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modelid = addworderBeen_model.get(position).getModelId();
                modelname = addworderBeen_model.get(position).getModel();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                equipmentId = null;
            }
        });
        models.setAdapter(adapter_model);
        adapter_model.notifyDataSetChanged();
        models.setSelection(0, true);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mike: {
                if (mBtnRecort.getVisibility() == View.VISIBLE) {
                    mBtnRecort.setVisibility(View.GONE);
                } else if (mBtnRecort.getVisibility() == View.GONE) {
                    mBtnRecort.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.iv_yes:
                //TODO 验证输入有效性
                iv_yes.setClickable(false);
                showWaitDialog("操作进行中,请稍后...");
                uploadPicFiles();

                break;
            case R.id.toolbar:
                Intent intent = new Intent(Addwork_order_Activity.this, first_supplier_activity.class);
                startActivities(new Intent[]{intent});
                this.finish();
                break;
            // case R.id.iv_add:
            // break;
            default:
                break;

        }
    }


    private String uploadRecordFile() {
        // LogUtil.e("fileName==", mBtnRecort.getCurrentAudioPath());
        if (mLayout.getVisibility() == View.VISIBLE && !RecordButtonUtil.isEmpty(mBtnRecort.getCurrentAudioPath())) {
            final RequestParams params = new RequestParams();
            File file = new File(mBtnRecort.getCurrentAudioPath());
            try {
                params.put("fileName0", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            params.put("number", 1);
            ServerApi.uploadSingleFile(params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                    String content = null;
                    try {
                        content = new String(responseBody, "UTF-8");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(content);
                        if (jsonObject.getString("ret_code").equals("0")) {
                            rowUrl = jsonObject.getString("dataset");
                            submitOrder();
                        } else {
                            //Toast.makeText(Addwork_order_Activity.this, jsonObject.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("ret_code").equals("0011")) {
                                Intent intent = new Intent(Addwork_order_Activity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.e("路径为:", rowUrl);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(Addwork_order_Activity.this, "语音上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    // showWaitDialog();
                }

            });

        } else
            submitOrder();
        return null;
    }

    private void uploadPicFiles() {
        File file = null;
        if (Bimp.tempSelectBitmap.size() > 0) {
            final RequestParams params = new RequestParams();
            int number = 0;
            int j = 0;
            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                try {
                    number++;
                    File f = new File(ImageUtils.getRealFilePath(this, Uri.parse(Bimp.tempSelectBitmap.get(i).getPath())));
                    // ImageUtils.compressBmpToFile(ImageUtils.compressImageFromFile(Bimp.tempSelectBitmap.get(i).getPath(), f), f.getName());
                    // params.put("fileName" + j, f);
                    file = ImageUtils.compressBmpToFile(Bimp.tempSelectBitmap.get(i).getBitmap(), f.getName() + ".jpg");
                    params.put("fileName" + j, file);
                    j++;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            params.put("number", number);
            ApiHttpClient.setTimeOut(60000);
            ServerApi.uploadSingleFile(params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getString("ret_code").equals("0")) {
                            Bimp.tempSelectBitmap.clear();
                            MyAdapter.mSelectedImage.clear();
                            ApiHttpClient.setTimeOut(10000);
                            try {
                                urls = response.getString("dataset");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            uploadRecordFile();
                        } else {
                            hideWaitDialog();
                            iv_yes.setClickable(true);
                            String msg = response.getString("ret_msg");
                            Toast.makeText(Addwork_order_Activity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                    hideWaitDialog();
                    iv_yes.setClickable(true);
                    ApiHttpClient.setTimeOut(10000);
                    Toast.makeText(Addwork_order_Activity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                    hideWaitDialog();
                    iv_yes.setClickable(true);
                    ApiHttpClient.setTimeOut(10000);
                    Toast.makeText(Addwork_order_Activity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    //showWaitDialog();
                }
            });

        } else {
            uploadRecordFile();
            return;
        }
    }

    private void submitOrder() {
        jobOrderType = String.valueOf(repairs_form.getText().toString().trim());
        comAddress = String.valueOf(et_mailing_address.getText().toString().trim());
        express = String.valueOf(et_express.getText().toString().trim());
        courierNum = String.valueOf(courier_number.getText().toString().trim());
        phone = String.valueOf(contact_number.getText().toString().trim());

        deviceName = String.valueOf(deviceTypes.getSelectedItem().toString().trim());
        modelname = String.valueOf(models.getSelectedItem().toString().trim());
        remark = String.valueOf(note.getText().toString().trim());
        deviceNum = String.valueOf(nummber.getText().toString().trim());

        if (urls == null) {
            urls = "";
        }
        if (rowUrl == null) {
            rowUrl = "";
        }

        voiceStr = et_write.getText().toString();


        if (urls.equals("") && rowUrl.equals("") && voiceStr.equals("")) {
            hideWaitDialog();
            ToastUtils.showToast(instence, "输入信息不全");
            iv_yes.setClickable(true);
        } else {
            iv_yes.setClickable(false);
            showWaitDialog("操作进行中,请稍后...");
            ServerApi.Addworder_send(Preferences.getUserid(), deviceNum, express, courierNum, Preferences.getBranch(), phone, Preferences.getBranchid(),
                    supplierId, urls, remark, rowUrl, modelid, modelname, deviceName, voiceStr, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            String ret_code = null;
                            String ret_msg = null;
                            try {
                                ret_code = response.getString("ret_code");
                                if (ret_code.equals("0")) {
                                    JSONObject obj = response.getJSONObject("lists");
                                    String orderid = obj.getString("jobOrderId");
                                    forwardOrder(orderid);
                                } else {
                                    hideWaitDialog();
                                    iv_yes.setClickable(true);
                                    Toast.makeText(Addwork_order_Activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                                    if (ret_code.equals("0011")) {
                                        Intent intent = new Intent(Addwork_order_Activity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                            hideWaitDialog();
                            iv_yes.setClickable(true);
                            Log.e("TAG", "reset password fail:" + throwable.getMessage());
                            // Toast.makeText(Addwork_order_Activity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                            hideWaitDialog();
                            iv_yes.setClickable(true);
                            Log.e("TAG", "reset password fail:" + throwable.getMessage());
                            //  Toast.makeText(Addwork_order_Activity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
        }
    }

    private void forwardOrder(String orderId) {
        String userId = "0";
        if (mUserid != null) {
            userId = mUserid;
        }
        ServerApi.forward(orderId, userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = null;
                String ret_msg = null;
                try {
                    ret_code = response.getString("ret_code");
                    hideWaitDialog();
                    iv_yes.setClickable(true);
                    if (ret_code.equals("0")) {
                        Toast.makeText(Addwork_order_Activity.this, "工单创建成功", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Addwork_order_Activity.this, first_workorder_activity.class);
                        i.putExtra("state", "001");
                        startActivity(i);
                        finish();
                    } else {
                        ret_msg = response.getString("ret_msg");
                        Toast.makeText(Addwork_order_Activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(Addwork_order_Activity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                hideWaitDialog();
                iv_yes.setClickable(true);
                Log.e("TAG", "reset password fail:" + throwable.getMessage());
                Toast.makeText(Addwork_order_Activity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                hideWaitDialog();
                iv_yes.setClickable(true);
                Log.e("TAG", "reset password fail:" + throwable.getMessage());
                Toast.makeText(Addwork_order_Activity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
