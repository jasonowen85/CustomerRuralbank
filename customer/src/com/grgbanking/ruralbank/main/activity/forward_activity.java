package com.grgbanking.ruralbank.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.common.util.widget.IconCenterEditText;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.main.fragment.ToolUtil;
import com.grgbanking.ruralbank.main.model.ForwardListBean.ForwardListEntity;
import com.grgbanking.ruralbank.main.model.ForwardListBean.ForwardListEntity.ListEntity;
import com.grgbanking.ruralbank.session.SessionHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by liufei on 2016/7/31.
 */
public class forward_activity extends UI implements View.OnClickListener {
    class ItemStatus {
        public boolean mIsCheckBoxVisible = false;
        public boolean mIsCheckBoxSelected = false;
    }

    private ListView lv_news;
    private LinkedList<ItemStatus> mItemsExtendData;
    private ArrayList<ForwardListEntity.ListEntity> mList;
    private ArrayList<ListEntity> mOldList;
    private ListAdapt mListAdapt;
    private boolean mbStatueShow = false;
    private IconCenterEditText icet_search;
    private Button confirmforward;
    //供应商的id,用于向服务器取客服列表
    private String supplierId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forward_activity);
        ToolBarOptions options = new ToolBarOptions();
        //options.titleId = R.string.shenzheng;
        Intent intent = getIntent();
        supplierId = intent.getStringExtra("supplierId");
        String supplierName = intent.getStringExtra("supplierName");
        LogUtil.e("forward_activity", "supplierName==" + supplierName+"--supplierId="+supplierId);
        options.titleString = supplierName;
        setToolBar(R.id.toolbar, options);
        initDatas();
        initID();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 汉字转换位汉语拼音，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }

    private void initID() {
        confirmforward = (Button) findViewById(R.id.confirmforward);
        confirmforward.setOnClickListener(this);
        lv_news = (ListView) findViewById(R.id.lv_news);
        mListAdapt = new ListAdapt(this);
        lv_news.setAdapter(mListAdapt);
        //ToolUtil.ReCalListViewHeightBasedOnChildren(lv_news);
        lv_news.setOnItemClickListener(new OnItemClickListenerImpl());
        icet_search = (IconCenterEditText) findViewById(R.id.icet_search);
        icet_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = icet_search.getText().toString();
                if (content.length() > 0) {
                    ArrayList<ListEntity> fileterList = new ArrayList<>();
                        for (ListEntity contact : mOldList) {
                            if (contact.getName() != null) {
                                if (contact.getName().contains(content)
                                        || converterToSpell(contact.getName()).toLowerCase(Locale.CHINESE).contains(content.toLowerCase(Locale.CHINESE))) {
                                    if (!fileterList.contains(contact)) {
                                        fileterList.add(contact);
                                    }
                                }
                            }
                        mList = fileterList;
                    }
                } else {
                    mList = mOldList;
                }
                mListAdapt.notifyDataSetChanged();
               // Toast.makeText(forward_activity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
        icet_search.setOnSearchClickListener(new IconCenterEditText.OnSearchClickListener() {

            @Override
            public void onSearchClick(View view) {
                String content = icet_search.getText().toString();
//                if (content.length() > 0) {
//                    ArrayList<ListEntity> fileterList = new ArrayList<>();
//                    for (ListEntity contact : mOldList) {
//                        if (contact.getName() != null) {
//                            if (contact.getName().contains(content)) {
//                                if (!fileterList.contains(contact)) {
//                                    fileterList.add(contact);
//                                }
//                            }
//                        }
//                    }
//                    mList = fileterList;
//                } else {
//                    mList = mOldList;
//                }
//                mListAdapt.notifyDataSetChanged();
                Toast.makeText(forward_activity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDatas() {
        mList = new ArrayList<>();
        mOldList = new ArrayList<>();
        mList.clear();
        //06B0AB4CD0DB4EBDB07C0499A0835A32
        ServerApi.getBankForwardList(supplierId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getString("ret_code").equals("0")) {
                        JSONObject json = response.getJSONObject("lists");
                        LogUtil.e("forward_activity", "ret_code==" + json.toString());
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<ForwardListEntity>() {
                        }.getType();
                        ForwardListEntity b = gson.fromJson(json.toString(), type);
                        mList.addAll(b.getLists());
                        mOldList = mList;
                        mListAdapt.notifyDataSetChanged();
                    }else{
                        Toast.makeText(forward_activity.this, response.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                        if (response.getString("ret_code").equals("0011")){
                            Intent intent=new Intent(forward_activity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogUtil.e("SupplierListActivity", "sign in fail:" + throwable.getMessage() + "--" + responseString);
            }

        });

        InitListViewExtendData();

        SetShowStatus(false);

    }

    private void SetShowStatus(Boolean bStatueShow) {
        mbStatueShow = bStatueShow;
        if (mbStatueShow) {
            for (ItemStatus ItemData : GetItemsExtendData()) {
                ItemData.mIsCheckBoxSelected = false;
                ItemData.mIsCheckBoxVisible = true;
            }
        } else {
            for (ItemStatus ItemData : GetItemsExtendData()) {
                ItemData.mIsCheckBoxSelected = false;
                ItemData.mIsCheckBoxVisible = false;
            }
        }
        if (mListAdapt != null) {
            ToolUtil.ReCalListViewHeightBasedOnChildren(lv_news);
            mListAdapt.notifyDataSetChanged();
        }
    }

    private void InitListViewExtendData() {
        GetItemsExtendData().clear();
        int nItemCount = mList.size();
        for (int i = 0; i < nItemCount; i++) {
            ItemStatus itemData = new ItemStatus();
            GetItemsExtendData().add(itemData);
        }
    }

    class ViewHolder {
        TextView titleTextView, content;
        HeadImageView headImageView;
    }

    class ListAdapt extends BaseAdapter implements OnCheckedChangeListener {
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public ListAdapt(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder = null;
            if (convertView == null) {
                vHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(
                        R.layout.forward_list, null);
                vHolder.titleTextView = (TextView) convertView
                        .findViewById(R.id.tv_name);
                vHolder.headImageView = (HeadImageView) convertView.findViewById(R.id.iv_head_portrait);
                vHolder.content = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.headImageView.loadBuddyAvatar(mList.get(position).getPhone());
            vHolder.titleTextView.setText(mList.get(position).getName());
            vHolder.content.setText(mList.get(position).getJobOrderNum());
            return convertView;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Integer nPosition = (Integer) (buttonView.getTag());
            GetItemsExtendData().get(nPosition.intValue()).mIsCheckBoxSelected = isChecked;
            int nSelectCount = 0;
            for (ItemStatus ItemData : GetItemsExtendData()) {
                if (ItemData.mIsCheckBoxSelected == true)
                    nSelectCount += 1;
            }
        }
    }

    private LinkedList<ItemStatus> GetItemsExtendData() {
        if (mItemsExtendData == null)
            mItemsExtendData = new LinkedList<ItemStatus>();
        return mItemsExtendData;
    }

    private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            SessionHelper.startP2PSession(forward_activity.this, mList.get(Integer.parseInt(parent.getAdapter().getItem(position).toString())).getPhone());
            //NimUIKit.getContactEventListener().onItemClick(forward_activity.this, mList.get(Integer.parseInt(parent.getAdapter().getItem(position).toString())).getPhone());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == confirmforward) {
            Intent it = new Intent();
            it.setClass(forward_activity.this, MainActivity.class);
            startActivity(it);
        }
    }
}
