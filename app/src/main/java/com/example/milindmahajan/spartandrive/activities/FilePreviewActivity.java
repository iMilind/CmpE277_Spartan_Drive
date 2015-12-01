package com.example.milindmahajan.spartandrive.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.milindmahajan.spartandrive.R;

public class FilePreviewActivity extends AppCompatActivity {

    String previewUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_preview);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            previewUrl = extras.getString("previewUrl");
        }
    }

    @Override
    protected void onStart() {

        super.onStart();

        WebView webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl(previewUrl);
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
