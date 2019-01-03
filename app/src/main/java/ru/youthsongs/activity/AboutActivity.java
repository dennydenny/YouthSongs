package ru.youthsongs.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import ru.youthsongs.R;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setTitle("");
        toolbar.setTitle("О программе");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WebView webView = (WebView) findViewById(R.id.about_webview);
        webView.setVerticalScrollBarEnabled(true);

        // Get the Android assets folder path
        String folderPath = "file:android_asset/about/";

        // Get the HTML file name
        String fileName = "About.html";

        // Get the exact file location
        String file = folderPath + fileName;
        //webView.loadDataWithBaseURL(null, file, "text/html", "utf-8", null);
        webView.loadUrl(file);
    }
}
