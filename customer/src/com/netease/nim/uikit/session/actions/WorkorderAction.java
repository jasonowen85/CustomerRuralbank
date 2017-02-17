package com.netease.nim.uikit.session.actions;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.grgbanking.ruralbank.NimApplication;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.common.bean.userInfo;
import com.grgbanking.ruralbank.main.activity.SelectrepairsActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class WorkorderAction extends BaseAction {
    private final static String TAG = "WorkorderAction";

    public WorkorderAction() {
        super(R.drawable.nim_message_plus_location_selector, R.string.input_panel_workorder);
    }

    public WorkorderAction(Context ctx) {
        super(ctx, R.drawable.nim_message_plus_location_selector, R.string.input_panel_workorder);
    }

    @Override
    public void onClick() {
        //TODO fengtangquan 参数传递
        String account = null;
        if(!TextUtils.isEmpty(getAccount())&& getAccount().contains(NimApplication.sendApkName)){
            account = getAccount().replaceAll(NimApplication.sendApkName, "");
        }
        LogUtil.e("jiang", "新报修 账号为： " + account) ;
        ServerApi.getUserInfo(account, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = null;
                String ret_msg = null;
                try {
                    ret_code = response.getString("ret_code");

                    if (ret_code.equals("0")) {
                        JSONObject data = response.getJSONObject("lists");
                        userInfo userInfo = new userInfo();
                        if (data.has("supplierName")) {
                            userInfo.setCompanyId(data.getString("supplierId"));
                            userInfo.setCompany(data.getString("supplierName"));
                        }
                        if (data.has("branch")) {
                            userInfo.setCompany(data.getString("branch"));
                        }
                        if (data.has("id")) {
                            userInfo.setId(data.getString("id"));
                        }

                        Intent intent = new Intent(WorkorderAction.this.context, SelectrepairsActivity.class);

                        intent.putExtra("userid", userInfo.getId());
                        intent.putExtra("suppliername", userInfo.getCompany());
                        intent.putExtra("supplierid", userInfo.getCompanyId());
                        WorkorderAction.this.context.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                LogUtil.e(TAG, "getUserInfo fail:" + throwable.getMessage());
            }
        });


//        showConfirmSource();
    }

    public void showConfirmSource() {
        EasyAlertDialogHelper.createOkCancelDiolag(getActivity(), null, "发送故障填写工单到当前聊天窗口？", true, new EasyAlertDialogHelper.OnDialogActionListener() {
            @Override
            public void doCancelAction() {

            }

            @Override
            public void doOkAction() {
//                WorkorderAttachment attachment = new WorkorderAttachment();
//                attachment.setOrderid("882829");
//                IMMessage message;
//                message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), attachment);
//                sendMessage(message);
                Intent intent = new Intent(WorkorderAction.this.context, SelectrepairsActivity.class);
                WorkorderAction.this.context.startActivity(intent);
            }
        }).show();
    }
}
