package com.grgbanking.ruralbank.main.activity;

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

public class Visit_worderActivity extends UI implements View.OnClickListener {
    private EditText visit_contacts;
    private EditText visit_phone;
    private EditText visit_address;
    private EditText visit_write;
    private Spinner visit_equipments, visit_models, visit_suppliers;
    private String account;
    private ArrayList<AddworderBean> addwordBean_supplier;
    private AddworderBean bean;
    private ArrayAdapter adapter_supplier;
    private com.grgbanking.ruralbank.common.photo.popwindow.SelectPicPopupWindow menuWindow;
    private String supplierId;
    private ArrayList<AddworderBean> addwordBean_deviceType;
    private ArrayAdapter adapter_deviceType;
    private String equipmentId;
    private String write;
    private ArrayList<AddworderBean> addworderBeen_model;
    private ArrayAdapter adapter_model;
    private NoScrollGridView noScrollGridView;
    private String modelid;
    private String modelname;
    private String filepath;
    private String urls;
    private List<String> urllist;
    private Visit_worderActivity instence;
    private static final int TAKE_PICTURE = 0;
    private PictureAdapter adapter;
    private ImageView visit_mike, mvisit_yes;
    private RecordButton mBtnRecort;
    private String rowUrl;
    private RelativeLayout mLayout;
    private String branch;
    private String visit_branch;
    private String visit_deviceName;
    private String visit_remark;
    private String phone;
    private EditText remark;
    private TextView mTvTime;
    private ImageView mImgVolume;
    private AnimationDrawable drawable;
    private ImageView mImgDeleteRecord;
    private ImageView mImgLayout;
    private String mSupplierid;
    private String mUserid;
    private String mSuppliername;
    private EditText visit_mStr;
    private String visit_Str;

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
        setContentView(R.layout.activity_visit_worder);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.returns;

        setToolBar(R.id.toolbar, options);
        instence = this;
        urllist = new ArrayList<String>();
        init();
        getParams();
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

        (mvisit_yes = (ImageView) findViewById(R.id.iv_yes)).setOnClickListener(this);
        ;
        remark = (EditText) findViewById(R.id.visit_note);

        (visit_mStr = (EditText) findViewById(R.id.visit_write)).addTextChangedListener(mtextWatcher);


        (visit_mike = (ImageView) findViewById(R.id.visit_mike)).setOnClickListener(this);
        mLayout = (RelativeLayout) findViewById(R.id.visit_layout_record);
        account = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        if (account == null || account.equals("")) {
            account = DemoCache.getAccount();
        }
        visit_contacts = (EditText) findViewById(R.id.visit_repairs_contacts);
        if (DemoCache.getAccount().equals(account)) {
            visit_contacts.setText(NimUserInfoCache.getInstance().getUserName(account));
        }
        visit_phone = (EditText) findViewById(R.id.visit_contact_number);

        if(account.contains(NimApplication.sendApkName)){
            visit_phone.setText(account.substring(NimApplication.sendApkName.length()));
        } else {
            visit_phone.setText(account);
        }
        visit_address = (EditText) findViewById(R.id.visit_mailing_address);
        if (Preferences.getBranch() != null) {
            visit_address.setText(Preferences.getBranch());
        }
        mBtnRecort = (RecordButton) findViewById(R.id.visit_btn);
        mLayout = (RelativeLayout) findViewById(R.id.visit_layout_record);
        mImgLayout = (ImageView) findViewById(R.id.visit_img_layout);
        mImgDeleteRecord = (ImageView) findViewById(R.id.delete_visit);
        mImgDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnRecort.cancelRecord();
                mLayout.setVisibility(View.GONE);
            }
        });

        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnRecort.playRecord();
            }
        });
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBtnRecort.getLayoutParams();
        params.width = getScreenW(this);
        params.height = (int) (getScreenH(this) * 0.3);
        mBtnRecort.setLayoutParams(params);
        mTvTime = (TextView) findViewById(R.id.visit_time_record);
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

        adapter = new PictureAdapter(this);
        noScrollGridView = (NoScrollGridView) findViewById(R.id.visit_noScrollgridview);
        noScrollGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        noScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideInput(Visit_worderActivity.this.getApplicationContext(), visit_contacts);
                if (i == Bimp.getTempSelectBitmap().size()) {
                    selectImgs();
                } else {
                    Intent intent = new Intent(Visit_worderActivity.this,
                            GalleryActivity.class);
                    intent.putExtra("ID", i);
                    startActivity(intent);
                }
            }
        });
        noScrollGridView.setAdapter(adapter);


        visit_equipments = (Spinner) findViewById(R.id.visit_equipment);
        visit_models = (Spinner) findViewById(R.id.visit_model);
        visit_suppliers = (Spinner) findViewById(R.id.visit_supplier);


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
            editStart = visit_mStr.getSelectionStart();
            editEnd = visit_mStr.getSelectionEnd();
            if (temp.length() > 200) {
                ToastUtils.showToast(instence, "故障描述限200字");
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                visit_mStr.setText(s);
                visit_mStr.setSelection(tempSelection);
            }
        }
    };

    private void selectImgs() {
        //  ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(instence.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        menuWindow = new com.grgbanking.ruralbank.common.photo.popwindow.SelectPicPopupWindow(Visit_worderActivity.this, itemsOnClick);
        //设置弹窗位置
        menuWindow.showAtLocation(Visit_worderActivity.this.findViewById(R.id.ll_first_my_fragment), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_camera:        //点击拍照按钮
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(Visit_worderActivity.this, PermissionUtils.PERMISSION_CAMERA) ==
                                PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(Visit_worderActivity.this,
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
                        if (ContextCompat.checkSelfPermission(Visit_worderActivity.this, PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(Visit_worderActivity.this,
                                    new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, PermissionUtils.CODE_READ_EXTERNAL_STORAGE);
                        } else {
                            Intent intent = new Intent(Visit_worderActivity.this,
                                    AlbumActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(Visit_worderActivity.this,
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

        filepath = FileUtils.iniFilePath(Visit_worderActivity.this);
        urllist.add(filepath);
        File file = new File(filepath);
        // 启动Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = BitmapUtils.getCompressedBitmap(Visit_worderActivity.this, filepath);
                    FileUtils.saveBitmap(bm, fileName);

                    ImageBean takePhoto = new ImageBean();
                    takePhoto.setBitmap(bm);
                    takePhoto.setPath(filepath);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
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
                    if(ContextCompat.checkSelfPermission(this, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
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


    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        Bimp.tempSelectBitmap.clear();
        MyAdapter.mSelectedImage.clear();
        super.onDestroy();
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

    private void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                        Toast.makeText(Visit_worderActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(Visit_worderActivity.this, LoginActivity.class);
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
        visit_suppliers.setAdapter(adapter_supplier);
        visit_suppliers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        visit_suppliers.setSelection(0, true);
        if (mSupplierid != null) {
            getdeviceTypes(mSupplierid);
        }
    }

    private void getdeviceTypes(String supplier) {
        ServerApi.getdeviceTypes(supplier, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = null;
                String ret_msg = null;
                String json = response.toString();
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
                        Toast.makeText(Visit_worderActivity.this, response.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                        if (response.getString("ret_code").equals("0011")) {
                            Intent intent = new Intent(Visit_worderActivity.this, LoginActivity.class);
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

        });
    }

    private void updateDeviceTypeUI() {
        String[] addlist = new String[addwordBean_deviceType.size()];
        for (int i = 0; i < addwordBean_deviceType.size(); i++) {
            addlist[i] = addwordBean_deviceType.get(i).getEquipment();
        }
        adapter_deviceType = new ArrayAdapter(this, R.layout.spinner_supplier, R.id.text, addlist);
        visit_equipments.setAdapter(adapter_deviceType);
        visit_equipments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        visit_equipments.setSelection(0, true);
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
                        Toast.makeText(Visit_worderActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(Visit_worderActivity.this, LoginActivity.class);
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
        visit_models.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        visit_models.setAdapter(adapter_model);
        adapter_model.notifyDataSetChanged();
        visit_models.setSelection(0, true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.visit_mike:
                if (mBtnRecort.getVisibility() == View.VISIBLE) {
                    mBtnRecort.setVisibility(View.GONE);
                } else if (mBtnRecort.getVisibility() == View.GONE) {
                    mBtnRecort.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_yes:
                mvisit_yes.setClickable(false);
                showWaitDialog("操作进行中,请稍后...");
                uploadPicFiles();
                break;
            default:
                break;
        }

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
                            mvisit_yes.setClickable(true);
                            String msg = response.getString("ret_msg");
                            Toast.makeText(Visit_worderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                    hideWaitDialog();
                    mvisit_yes.setClickable(true);
                    ApiHttpClient.setTimeOut(10000);
                    Toast.makeText(Visit_worderActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                    hideWaitDialog();
                    mvisit_yes.setClickable(true);
                    ApiHttpClient.setTimeOut(10000);
                    Toast.makeText(Visit_worderActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    showWaitDialog();
                }
            });

        } else {

            uploadRecordFile();
            return;
        }
    }

    private void uploadRecordFile() {
        LogUtil.e("fileName==", mBtnRecort.getCurrentAudioPath());
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
                            Toast.makeText(Visit_worderActivity.this, jsonObject.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("ret_code").equals("0011")) {
                                Intent intent = new Intent(Visit_worderActivity.this, LoginActivity.class);
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
                    Toast.makeText(Visit_worderActivity.this, "语音上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    //showWaitDialog();
                }

            });

        } else
            submitOrder();
    }

    private void submitOrder() {
        visit_branch = String.valueOf(visit_address.getText().toString().trim());
        phone = String.valueOf(visit_phone.getText().toString().trim());
        visit_deviceName = String.valueOf(visit_equipments.getSelectedItem().toString().trim());
        visit_remark = String.valueOf(remark.getText().toString().trim());
        visit_Str = String.valueOf(visit_mStr.getText().toString().trim());

        if (urls == null) {
            urls = "";
        }
        if (rowUrl == null) {
            rowUrl = "";
        }
        if (visit_Str == null) {
            visit_Str = "";
        }

        if (urls.equals("") && rowUrl.equals("") && visit_Str.equals("")) {
            hideWaitDialog();
            mvisit_yes.setClickable(true);
            ToastUtils.showToast(instence, "输入信息不全");
            mvisit_yes.setClickable(true);
        } else {
            ServerApi.Addworder_door(
                    Preferences.getBranch(), Preferences.getUserid(), phone, Preferences.getBranchid(), supplierId, urls, visit_remark, rowUrl
                    , visit_Str, modelname, visit_deviceName, modelid, new JsonHttpResponseHandler() {
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
                                    mvisit_yes.setClickable(true);
                                    Toast.makeText(Visit_worderActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                                    if (ret_code.equals("0011")) {
                                        Intent intent = new Intent(Visit_worderActivity.this, LoginActivity.class);
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
                            mvisit_yes.setClickable(true);
                            Log.e("TAG", "reset password fail:" + throwable.getMessage());
                            Toast.makeText(Visit_worderActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                            hideWaitDialog();
                            mvisit_yes.setClickable(true);
                            Log.e("TAG", "reset password fail:" + throwable.getMessage());
                            Toast.makeText(Visit_worderActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });

        }
    }

    private void forwardOrder(String orderid) {
        String userId = "0";
        if (mUserid != null) {
            userId = mUserid;
        }
        ServerApi.forward(orderid, userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = null;
                String ret_msg = null;
                try {
                    ret_code = response.getString("ret_code");
                    hideWaitDialog();
                    mvisit_yes.setClickable(true);
                    if (ret_code.equals("0")) {
                        Toast.makeText(Visit_worderActivity.this, "工单创建成功", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Visit_worderActivity.this, first_workorder_activity.class);
                        i.putExtra("state", "001");
                        startActivity(i);
                        finish();
                    } else {
                        ret_msg = response.getString("ret_msg");
                        Toast.makeText(Visit_worderActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(Visit_worderActivity.this, LoginActivity.class);
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
                mvisit_yes.setClickable(true);
                Log.e("TAG", "reset password fail:" + throwable.getMessage());
                Toast.makeText(Visit_worderActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                hideWaitDialog();
                mvisit_yes.setClickable(true);
                Log.e("TAG", "reset password fail:" + throwable.getMessage());
                Toast.makeText(Visit_worderActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }


}
