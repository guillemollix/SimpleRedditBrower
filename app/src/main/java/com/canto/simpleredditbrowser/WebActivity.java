package com.canto.simpleredditbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent incomingIntent = getIntent();
        String url = incomingIntent.getStringExtra("@string/web_url");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Simple Reddit Browser");


        WebView webView = (WebView) findViewById(R.id.postWebview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}
