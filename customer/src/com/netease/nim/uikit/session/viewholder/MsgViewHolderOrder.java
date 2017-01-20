package com.netease.nim.uikit.session.viewholder;

import android.content.Intent;
import android.widget.TextView;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.common.bean.userInfo;
import com.grgbanking.ruralbank.main.activity.SelectrepairsActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.ui.imageview.MsgThumbImageView;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.extension.WorkorderAttachment;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhoujianghua on 2015/8/7.
 */
public class MsgViewHolderOrder extends MsgViewHolderBase {
    private WorkorderAttachment attachment;
    public MsgThumbImageView orderView;
    public TextView orderText;

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_order;
    }

    @Override
    protected void inflateContentView() {
        orderView = (MsgThumbImageView) view.findViewById(R.id.message_item_order_image);
        orderText = (TextView) view.findViewById(R.id.message_item_order_text);
    }

    @Override
    protected void bindContentView() {
        final WorkorderAttachment order = (WorkorderAttachment) message.getAttachment();
        orderText.setText(order.getOrderid());

        int[] bound = ImageUtil.getBoundWithLength(getLocationDefEdge(), R.drawable.my_order1, true);
        int width = bound[0];
        int height = bound[1];

        setLayoutParams(width, height, orderView);
        setLayoutParams(width, (int) (0.38 * height), orderText);

        orderView.loadAsResource(R.drawable.my_order1, width, height, R.drawable.nim_message_item_round_bg);
    }

    @Override
    protected void onItemClick() {
        WorkorderAttachment workorder = (WorkorderAttachment) message.getAttachment();
        ServerApi.getUserInfo(message.getFromAccount(), new JsonHttpResponseHandler() {
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
                        Intent intent = new Intent(context, SelectrepairsActivity.class);
                        intent.putExtra("userid", userInfo.getId());
                        intent.putExtra("suppliername", userInfo.getCompany());
                        intent.putExtra("supplierid", userInfo.getCompanyId());
                        context.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
              //  LogUtil.e(TAG, "getUserInfo fail:" + throwable.getMessage());
            }
        });
    }

    public static int getLocationDefEdge() {
        return (int) (0.25 * ScreenUtil.screenWidth);
    }
}
