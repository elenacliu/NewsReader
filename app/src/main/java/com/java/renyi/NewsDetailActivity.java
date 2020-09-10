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
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 新闻详情页（新闻列表页直接过滤掉content == null 的新闻）
 * 由相关的fragment跳转至此时，需要携带url信息/若url == null，则需要携带time, source, content信息
 */
// 参考：https://www.jianshu.com/p/4564be81a108
public class NewsDetailActivity extends AppCompatActivity {

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

        initView();
        api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.registerApp(APP_ID);
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
        setResult(Activity.RESULT_OK, intent);
//        // 构造WebView
//        webView = new WebView(this);
//        setContentView(webView);
//
//        WebSettings webSettings = webView.getSettings();
//        // 设置自适应屏幕
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setJavaScriptEnabled(true);
//
//        webSettings.setUseWideViewPort(true);       // 将图片调整到合适webview的大小
//        webSettings.setLoadWithOverviewMode(true);  // 缩放至屏幕的大小
//
//        webSettings.setSupportZoom(true);           // 支持缩放
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);  // 隐藏原生缩放控件
//
//        webSettings.setLoadsImagesAutomatically(true);  // 支持自动加载图片
//        webSettings.setDefaultTextEncodingName("utf-8");    // 设置编码格式
//
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setAppCacheEnabled(true);
////        webSettings.setUserAgentString("User-Agent:Android");
//
//        // 加载网页链接
//        webView.loadUrl(url);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                Log.d(url, "开始加载");
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                Log.d(url, "加载结束");
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                Log.d("跳转", url);
//                view.loadUrl(url);      // 强制在当前webview中加载url
//                return true;
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
//                handler.proceed();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    webView.getSettings()
//                            .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//                }
//            }
//        });
//        WebView.setWebContentsDebuggingEnabled(true);
    }

    public void onShareWechatClick(View v) {
//        Toast.makeText(this, "分享至微信", Toast.LENGTH_SHORT).show();
        WXMediaMessage msg = new WXMediaMessage();
        WXTextObject wxTextObject = new WXTextObject();
        wxTextObject.text = news.content;
        msg.mediaObject = wxTextObject;
        msg.title = news.title;
        msg.description = news.content.substring(0, 500);      // only 1024 characters

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        //transaction作为一个请求的唯一标识，必须进行赋值
        req.transaction = "text";
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    public void onShareWeiboClick(View v) {
        Toast.makeText(this, "分享至微博", Toast.LENGTH_SHORT).show();
    }
}