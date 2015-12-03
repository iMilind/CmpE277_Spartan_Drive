package com.example.milindmahajan.spartandrive.activities;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.model.DropboxItem;

public class FilePreviewActivity extends AppCompatActivity {

    DropboxItem dropboxItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_preview);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5AC5A7")));
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1565C0")));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            dropboxItem = extras.getParcelable("dropboxItem");
        }

        setTitle(dropboxItem.getName());
    }

    @Override
    protected void onStart() {

        super.onStart();

        WebView webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {

            }
        });

        webView.loadUrl(dropboxItem.getShareLink());
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }
}
