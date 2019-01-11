package ru.youthsongs.service;

import java.util.List;

import ru.youthsongs.entity.Song;
import ru.youthsongs.util.DatabaseHelper;

public class SongServiceImpl implements SongService {
    private DatabaseHelper db;

    @Override
    public List<Song> getSongsByQuery(String query) {
        return null;
    }


}
