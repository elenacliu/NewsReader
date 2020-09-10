//package com.java.renyi.wxapi;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.java.renyi.R;
//import com.tencent.mm.opensdk.modelbase.BaseReq;
//import com.tencent.mm.opensdk.modelbase.BaseResp;
//import com.tencent.mm.opensdk.openapi.IWXAPI;
//import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
//import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//import com.xyzlf.share.library.interfaces.ShareConstant;
//import com.xyzlf.share.library.util.ManifestUtil;
//
//public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler  {
//
//    private IWXAPI api;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_w_x_entry);
//
//        api = WXAPIFactory.createWXAPI(this, ManifestUtil.getWeixinKey(this), false);
//        api.handleIntent(getIntent(), this);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        api.handleIntent(intent, this);
//    }
//
//    @Override
//    public void onReq(BaseReq baseReq) {
//
//    }
//
//    @Override
//    public void onResp(BaseResp baseResp) {
//        Intent intent = new Intent();
//        intent.setAction(ShareConstant.ACTION_WEIXIN_CALLBACK);
//        intent.putExtra(ShareConstant.EXTRA_WEIXIN_RESULT, baseResp.errCode);
//        sendBroadcast(intent);
//        finish();
//    }
//}