package ru.youthsongs.service;

import android.util.Log;

import ru.youthsongs.BuildConfig;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

public class FabricTrackingService implements TrackingService {
    private final String version = BuildConfig.VERSION_NAME;
    @Override
    public void trackSongOpened(final String songNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("tracking", "Sending Song_Opened event to Crashlytics... Song is " + songNumber);

                Answers.getInstance().logCustom(new CustomEvent("Song_Opened")
                .putCustomAttribute("Number", songNumber)
                .putCustomAttribute("Version", version));
            }
        }).start();

    }

    @Override
    public void trackEasterEggFound(final int stage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("tracking", "Sending Easter_Egg_Found event to Crashlytics... Stage is " + stage);

                Answers.getInstance().logCustom(new CustomEvent("Easter_Egg_Found")
                        .putCustomAttribute("Stage", stage)
                        .putCustomAttribute("Version", version));

            }
        }).start();
    }
}
