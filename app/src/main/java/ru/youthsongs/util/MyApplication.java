package ru.youthsongs.util;

import android.app.Application;
import android.util.Log;

import com.flurry.android.FlurryAgent;

public class MyApplication extends Application {

    private final String flurryApiKey = "G9R7JY9FTHZWSFTCYFZ8";
    private Thread.UncaughtExceptionHandler priorExceptionHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .withLogLevel(Log.INFO)
                .withCaptureUncaughtExceptions(true)
                .build(this, flurryApiKey);
        // get the prior default exception handler
        priorExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
                priorExceptionHandler.uncaughtException(thread, e);
            }
        });
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        Log.e("handleUncaughtException", "Sending UncaughtException to Flurry...");
        FlurryAgent.onError("UncaughtException", e.getMessage(), e);
    }
}
