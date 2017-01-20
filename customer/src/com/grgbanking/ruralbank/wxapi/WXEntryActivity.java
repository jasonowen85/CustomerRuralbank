package com.grgbanking.ruralbank.wxapi;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    public static final String WX_APP_ID="wxae7243835fa82045";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IWXAPI api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }
    @Override
    public void onReq(BaseReq arg0) {
    }


    @Override
    public void onResp(BaseResp resp) {
//        LogUtil.e("qzc", "resp.errCode:" + resp.errCode + ",resp.errStr:"
//                + resp.errStr);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(getApplicationContext(), "分享成功", Toast.LENGTH_SHORT).show();
                System.out.println("success");
                this.finish();
                //分享成功
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
                Toast.makeText(getApplicationContext(), "分享取消", Toast.LENGTH_SHORT).show();
                System.out.println("ERR_USER_CANCEL");
                this.finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(getApplicationContext(), "分享成功", Toast.LENGTH_SHORT).show();
                System.out.println("ERR_AUTH_DENIED");
                this.finish();
                //分享拒绝
                break;
        }
    }
}