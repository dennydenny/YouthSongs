package ru.youthsongs.service;

import java.util.List;

import ru.youthsongs.entity.Song;

public interface SongService {
    //TODO: Remove all calls to Database from all of activities and implement it through this service.

    public List<Song> getSongsByQuery(String query);
}
