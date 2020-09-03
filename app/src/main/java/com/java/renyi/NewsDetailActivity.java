package com.java.renyi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 新闻详情页（新闻列表页直接过滤掉content == null 的新闻）
 * 由相关的fragment跳转至此时，需要携带url信息/若url == null，则需要携带time, source, content信息
 */
// 参考：https://www.jianshu.com/p/4564be81a108

public class NewsDetailActivity extends AppCompatActivity {

    private NewsEntity news;
    private TextView tvTitle, tvContent, tvTime, tvSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Intent intent = getIntent();
        news = (NewsEntity) intent.getSerializableExtra("news");

        System.out.println(news);
        initView();
    }

    private void initView() {
        tvTitle = findViewById(R.id.news_detail_title);
        tvTime = findViewById(R.id.news_detail_time);
        tvSource = findViewById(R.id.news_list_source);
        tvContent = findViewById(R.id.news_detail_content);

        tvTitle.setText(news.getTitle());
        tvTime.setText(news.getTime());
        if (news.getSource() != null) {
            tvSource.setText(news.getSource());
        }
//        else {
//            tvSource.setText("");
//        }

        tvContent.setText(news.getContent());
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
        Toast.makeText(this, "分享至微信", Toast.LENGTH_SHORT).show();
    }

    public void onShareWeiboClick(View v) {
        Toast.makeText(this, "分享至微博", Toast.LENGTH_SHORT).show();
    }
}