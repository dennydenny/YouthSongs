package ru.youthsongs.util;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.youthsongs.entity.Song;

public class DatabaseHelper extends SQLiteAssetHelper {
    static final String songs = "songs";

    private static final String songs_num = "num";
    private static final String songs_name = "name";
    private static final String songs_en_name = "en_name";
    private static final String songs_authors = "authors";
    public static final String songs_file = "file";
    public static final String songs_theme = "theme";
    private static final String songs_text = "text";
    private static final String songs_alt_name = "alt_name";

    private static final String DATABASE_NAME = "Sbornik_v8.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        initReadableDB();
    }

    private void initReadableDB() {
        this.db = getReadableDatabase();
    }

    public Song getSongByNumber(int num) {
        String sqlquery = "SELECT name, REPLACE(substr (text, 1, 40), '1. ', '') as text FROM songs WHERE num = " + num;

        try (Cursor c = db.rawQuery(sqlquery, null)) {
            c.moveToFirst();

            Song song = new Song();

            if (c.getCount() == 0) {
                song = null;
                return song;
            }
            song.setName(c.getString(c.getColumnIndex(songs_name)));
            song.setText(c.getString(c.getColumnIndex(songs_text)));
            return song;
        }
    }

    /*
    Возвращает объект песни, найденный по входному названию.
     */
    public Song getSongByName(String name) {
        long timeout = System.currentTimeMillis();
        String sqlquery = "SELECT name, text, en_name, authors, alt_name, num FROM songs WHERE name ='" + name + "'";
        try (Cursor c = db.rawQuery(sqlquery, null)) {
            c.moveToFirst();
            Song song = new Song();

            if (c.getCount() == 0) {
                return null;
            }

            // Название, текст песни и номер существуют всегда
            song.setName(c.getString(c.getColumnIndex(songs_name)));
            song.setText(c.getString(c.getColumnIndex(songs_text)));
            song.setNumber(c.getString(c.getColumnIndex(songs_num)));

            if (c.getString(c.getColumnIndex(songs_en_name)) != null) {
                song.setEnName(c.getString(c.getColumnIndex(songs_en_name)));
            } else {
                song.setEnName(null);
            }
            if (c.getString(c.getColumnIndex(songs_authors)) != null) {
                song.setAuthors(c.getString(c.getColumnIndex(songs_authors)));
            } else {
                song.setAuthors(null);
            }
            if (c.getString(c.getColumnIndex(songs_alt_name)) != null) {
                song.setAltName(c.getString(c.getColumnIndex(songs_alt_name)));
            } else {
                song.setAltName(null);
            }

            timeout = System.currentTimeMillis() - timeout;

            Log.i("DB", "Time of getsongbyname " + timeout + " ms");
            return song;
        }
    }

    // Возвращает список уникальных первых букв всех названий песен в БД.
    public ArrayList<String> getSongsFirstLetter() {
        String sqlquery = "SELECT DISTINCT substr (name, 1, 1) as letter FROM songs WHERE letter != '' ORDER BY letter";
        try (Cursor c = db.rawQuery(sqlquery, null)) {
            ArrayList<String> result = new ArrayList<>();
            while (c.moveToNext()) {
                String letter = c.getString(c.getColumnIndex("letter"));
                result.add(letter);
            }

            Log.i("getsongsfirstletter", "Result is " + result.size());
            return result;
        }
    }

    // Возвращает список песен, названия которых начинаются на входную букву.
    public ArrayList<String> getSongsByFirstLetter(String first_letter) {
        String sqlquery = "SELECT name FROM songs WHERE name like '" + first_letter + "%' ORDER BY name";
        try (Cursor c = db.rawQuery(sqlquery, null)) {
            ArrayList<String> songsbyfirstletter = new ArrayList<>();
            while (c.moveToNext()) {
                String letter = c.getString(c.getColumnIndex(songs_name));
                songsbyfirstletter.add(letter);
            }
            return songsbyfirstletter;
        }
    }

    // Возвращает список названий песен, содержащих данных из входной строки.
    public ArrayList<Song> getSongsByQuery(String query) {
        // Приводим регистр.
        String queryLow = query.toLowerCase();
        String queryUp = Character.toString(queryLow.charAt(0)).toUpperCase() + queryLow.substring(1);
        StringBuilder sqlquery = new StringBuilder();
        /*
        SELECT DISTINCT name, REPLACE(substr (text, 1, 40), '1. ', '') as short_text
        FROM songs
        WHERE text LIKE '%пред тобой%' OR name LIKE '%пред тобой%' OR text LIKE '%Пред тобой%' OR name LIKE '%Пред тобой%' ORDER by name
         */
        sqlquery.append("SELECT DISTINCT name, REPLACE(substr (text, 1, 40), '1. ', '') as short_text FROM songs WHERE text LIKE ")
                .append("'%").append(queryLow).append("%' OR name LIKE '%").append(queryLow).append("%' OR")
                .append(" text LIKE '%").append(queryUp).append("%' OR name LIKE '%").append(queryUp).append("%'")
                .append(" ORDER by name");
        Log.i("DB - getSongsByQuery", "Query is " + sqlquery.toString());
        try (Cursor c = db.rawQuery(sqlquery.toString(), null)) {
            ArrayList<Song> result = new ArrayList<>();
            while (c.moveToNext()) {
                Song song = new Song();
                song.setName(c.getString(c.getColumnIndex("name")));
                song.setText(c.getString(c.getColumnIndex("short_text")));
                result.add(song);
            }
            Log.i("DB", "Result size is " + result.size());
            return result;
        }
    }

    public List<Song> getEnSongsByQuery(String query) {
        // Приводим регистр.
        String queryLow = query.toLowerCase();
        String queryUp = Character.toString(queryLow.charAt(0)).toUpperCase() + queryLow.substring(1);
        StringBuilder sqlquery = new StringBuilder();

            /*
            SELECT DISTINCT name, en_name, REPLACE(substr (text, 1, 40), '1. ', '') as short_text
            FROM songs
            WHERE en_name LIKE '%what%' OR en_name LIKE '%What%'
            ORDER by en_name
             */
        sqlquery.append("SELECT DISTINCT name, en_name, REPLACE(substr (text, 1, 40), '1. ', '') as short_text FROM songs")
                .append(" WHERE en_name LIKE '%").append(queryLow).append("%' OR en_name LIKE '%").append(queryUp).append("%'")
                .append(" ORDER by en_name");

        Log.i("DB - getEnSongsByQuery", "Query is " + sqlquery.toString());
        try (Cursor c = db.rawQuery(sqlquery.toString(), null)) {
            ArrayList<Song> result = new ArrayList<>();
            while (c.moveToNext()) {
                Song song = new Song();
                song.setName(c.getString(c.getColumnIndex("name")));
                song.setEnName(c.getString(c.getColumnIndex("en_name")));
                song.setText(c.getString(c.getColumnIndex("short_text")));
                result.add(song);
            }
            Log.i("DB", "Result size is " + result.size());
            return result;
        }
    }

    public String getRandomSongName() {
        String sqlquery = "SELECT MAX(num) as num FROM songs";
        try (Cursor c = db.rawQuery(sqlquery, null)) {
            c.moveToFirst();
            int max = c.getInt(c.getColumnIndex(songs_num));

            Random rn = new Random();
            int rundom_song = rn.nextInt(max) + 1;

            Song tempSong = getSongByNumber(rundom_song);
            return tempSong.getName();
        }
    }

    public ArrayList<String> getSongsByTheme(String theme) {

        String sqlquery = "SELECT song_nums FROM themes WHERE theme = '" + theme + "';";
        Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        String song_nums = c.getString(c.getColumnIndex("song_nums")).trim();


        String sqlquery2 = "SELECT name FROM songs WHERE num IN (" + song_nums + ") ORDER BY name";
        c = db.rawQuery(sqlquery2, null);
        ArrayList<String> result = new ArrayList<>();

        while (c.moveToNext()) {
            String song = c.getString(c.getColumnIndex("name"));
            result.add(song);
        }
        c.close();
        return result;
    }
}