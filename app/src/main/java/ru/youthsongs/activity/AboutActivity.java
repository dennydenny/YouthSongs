package ru.youthsongs.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import ru.youthsongs.BuildConfig;

import ru.youthsongs.R;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.about_toolbar);
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

        // Setting a version
        TextView versionTv = (TextView) findViewById(R.id.aboutVersion);
        versionTv.setText("Версия - " + BuildConfig.VERSION_NAME);
    }
}
