package com.java.renyi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.java.renyi.db.Entry;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 新闻详情页（新闻列表页直接过滤掉content == null 的新闻）
 * 由相关的fragment跳转至此时，需要携带url信息/若url == null，则需要携带time, source, content信息
 */
// 参考：https://www.jianshu.com/p/4564be81a108
public class NewsDetailActivity extends AppCompatActivity implements IWXAPIEventHandler{

    public static final String APP_ID = "wxf8948c9080cebb06";       // APP_ID PASS, but signature doesn't match
    private IWXAPI api;

    private Entry news;
    private TextView tvTitle, tvContent, tvTime, tvSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Intent intent = getIntent();
        news = (Entry) intent.getSerializableExtra("news");

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(APP_ID);
        initView();
    }

    private void initView() {
        tvTitle = findViewById(R.id.news_detail_title);
        tvTime = findViewById(R.id.news_detail_time);
        tvSource = findViewById(R.id.news_detail_source);
        tvContent = findViewById(R.id.news_detail_content);

        if (news.title != null) {
            tvTitle.setText(news.title);
        }
        if (news.time != null) {
            tvTime.setText(news.time);
        }
        if (news.source != null) {
            System.out.println(news.source);
            tvSource.setText(news.source);
        }
        if (news.content != null) {
            tvContent.setText(news.content);
        }
        Intent intent = getIntent();
        intent.putExtra("id", news.get_id());
        intent.putExtra("title", news.title);
        intent.putExtra("type", news.type);

        setResult(Activity.RESULT_OK, intent);
    }

    public void onShareWechatClick(View v) {
//        String description;
//        if (news.content.length() > 30)
//            description = news.content.substring(0,30);
//        else
//            description = news.content;
//        ShareEntity shareEntity = new ShareEntity(news.title, description);
//        String url = news.urls;
//        if (!url.equals(""))
//            shareEntity.setUrl(url);
//
//        ShareUtil.showShareDialog(this, ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND | ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE
//        , shareEntity, ShareConstant.REQUEST_CODE);
        WXWebpageObject webpageObject = new WXWebpageObject();
        String url = "http://www.xinhuanet.com/english/2020-09/10/c_139356571.htm";
        if (news.urls != null && news.urls.size() > 0)
            url = news.urls.get(0);

        System.out.println("----------");
        Log.e("news url", url);
        System.out.println("----------");

        if (!url.equals(""))
            webpageObject.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = news.title;
        if (news.content.length() > 30)
            msg.description = news.content.substring(0, 30);      // only 1024 characters
        else
            msg.description = news.content;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        // transaction作为一个请求的唯一标识，必须进行赋值
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);

//        WXTextObject wxTextObject = new WXTextObject();
//        wxTextObject.text = news.content;
//        msg.mediaObject = wxTextObject;
//        msg.title = news.title;
//        if (news.content.length() > 30)
//            msg.description = news.content.substring(0, 30);      // only 1024 characters
//        else
//            msg.description = news.content;
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.message = msg;
//        //transaction作为一个请求的唯一标识，必须进行赋值
//        req.transaction = "text";
//        req.scene = SendMessageToWX.Req.WXSceneSession;
//        api.sendReq(req);
    }

    public void onShareWeiboClick(View v) {
        Toast.makeText(this, "分享至微博", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        String result;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "取消分享";
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        /**
//         * 分享回调处理
//         */
//        if (requestCode == ShareConstant.REQUEST_CODE) {
//            if (data != null) {
//                int channel = data.getIntExtra(ShareConstant.EXTRA_SHARE_CHANNEL, -1);
//                int status = data.getIntExtra(ShareConstant.EXTRA_SHARE_STATUS, -1);
//                onShareCallback(channel, status);
//            }
//        }
//    }
}