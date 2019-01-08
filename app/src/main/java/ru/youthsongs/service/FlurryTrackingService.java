package ru.youthsongs.service;

import android.util.Log;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

public class FlurryTrackingService {

    public static synchronized void trackSongOpened(final String songNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("tracking", "Sending Song_Opened event to flurry... Song is " + songNumber);
                Map<String, String> eventParams = new HashMap<>();
                eventParams.put("Number", songNumber);

                FlurryAgent.logEvent("Song_Opened", eventParams);
            }
        }).start();
    }

    public static synchronized void trackEasterEggFound(final int stage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("tracking", "Sending Easter_Egg_Found event to flurry... Stage is " + stage);
                Map<String, String> eventParams = new HashMap<>();
                eventParams.put("Stage", String.valueOf(stage));

                FlurryAgent.logEvent("Easter_Egg_Found", eventParams);
            }
        }).start();
    }
}
