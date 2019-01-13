package ru.youthsongs.service;

public interface TrackingService {
    void trackSongOpened(final String songNumber);

    void trackEasterEggFound(final int stage);
}
