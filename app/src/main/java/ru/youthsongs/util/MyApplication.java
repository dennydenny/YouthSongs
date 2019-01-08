package ru.youthsongs.util;

import android.app.Application;
import android.util.Log;

import com.flurry.android.FlurryAgent;

public class MyApplication extends Application {

    private final String flurryApiKey = "RWNPXJM7B5N4J3RS2J6D";

        @Override
        public void onCreate() {
            super.onCreate();
            new FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .withLogLevel(Log.INFO)
                    .withCaptureUncaughtExceptions(true)
                    .build(this, flurryApiKey);

        }
}
