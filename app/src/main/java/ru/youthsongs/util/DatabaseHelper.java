package ru.youthsongs.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import java.util.ArrayList;
import java.util.Random;

import ru.youthsongs.entity.Song;

public class DatabaseHelper extends SQLiteAssetHelper {
        static final String songs = "songs";;

        public static final String songs_num = "num";
        public static final String songs_name = "name";
        public static final String songs_en_name = "en_name";
        public static final String songs_authors = "authors";
        public static final String songs_file = "file";
        public static final String songs_theme = "theme";
        public static final String songs_text = "text";
        public static final String songs_alt_name = "alt_name";

        private static final String DATABASE_NAME = "Sbornik_v8.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper (Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public String [] getSongByNumber(int num) {

            SQLiteDatabase db = getReadableDatabase();

            String sqlquery = "SELECT name, substr (text, 1, 40) as text FROM songs WHERE num = " + num;

            Cursor c = db.rawQuery(sqlquery, null);
            c.moveToFirst();

            String s_name;
            String s_text;

            if (c.getCount() == 0) {
                String [] empty = {};
                return empty;
            }
            s_name = c.getString(c.getColumnIndex(songs_name));
            s_text = c.getString(c.getColumnIndex(songs_text));
            String [] answer = {s_name, s_text};
            db.close();
            return answer;
        }

        /*
        Возвращает объект песни, найденный по входному названию.
         */
        public Song getSongByName(String name) {
            long timeout= System.currentTimeMillis();

            SQLiteDatabase db = getReadableDatabase();
            String sqlquery = "SELECT name, text, en_name, authors, alt_name, num FROM songs WHERE name ='" + name + "'";
            Cursor c = db.rawQuery(sqlquery, null);
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
            }
                else {
                song.setEnName(null);
                }
            if (c.getString(c.getColumnIndex(songs_authors)) != null) {
                song.setAuthors(c.getString(c.getColumnIndex(songs_authors)));
            }
                else {
                    song.setAuthors(null);
                }
            if (c.getString(c.getColumnIndex(songs_alt_name)) != null) {
                song.setAltName(c.getString(c.getColumnIndex(songs_alt_name)));
            }
            else {
                song.setAltName(null);
            }

            timeout = System.currentTimeMillis() - timeout;

            Log.i("DB", "Time of getsongbyname " + timeout + " ms");
            db.close();
            return song;
        }

        // Возвращает список уникальных первых букв всех названий песен в БД.
        public ArrayList<String> getSongsFirstLetter(){
            SQLiteDatabase db = getReadableDatabase();

            String sqlquery = "SELECT DISTINCT substr (name, 1, 1) as letter FROM songs WHERE letter != '' ORDER BY letter";
            Cursor c = db.rawQuery(sqlquery, null);
            ArrayList<String> result = new ArrayList<String>();
            while (c.moveToNext()){
                String letter = c.getString(c.getColumnIndex("letter"));
                result.add(letter);
            }

            Log.i("getsongsfirstletter", "Result is " + result.size());
            db.close();
            return result;
        }

        // Возвращает список песен, названия которых начинаются на входную букву.
        public ArrayList<String> getSongsByFirstLetter(String first_letter){
            SQLiteDatabase db = getReadableDatabase();

            String sqlquery = "SELECT name FROM songs WHERE name like '" + first_letter + "%' ORDER BY name";
            Cursor c = db.rawQuery(sqlquery, null);
            ArrayList<String> songsbyfirstletter = new ArrayList<String>();
            while (c.moveToNext()){
                String letter = c.getString(c.getColumnIndex(songs_name));
                songsbyfirstletter.add(letter);
            }
            db.close();
            return songsbyfirstletter;
        }

        // Возвращает список названий песен, содержащих данных из входной строки.
        public ArrayList<String> getSongsByQuery(String query) {
            SQLiteDatabase db = getReadableDatabase();

            // Приводим регистр.
            String queryLow = query.toLowerCase();
            String queryUp = Character.toString(queryLow.charAt(0)).toUpperCase()+queryLow.substring(1);
            StringBuilder sqlquery = new StringBuilder();

            sqlquery.append("SELECT DISTINCT name, substr (text, 1, 40) as short_text FROM songs WHERE text LIKE ")
                    .append("'%").append(queryLow).append("%'").append(" OR name LIKE '%").append(queryLow).append("%' OR")
                    .append(" text LIKE '%").append(queryUp).append("%' OR name LIKE '%").append(queryUp).append("%'");
            Log.i("DB", "Query is "+ sqlquery.toString());
            Cursor c = db.rawQuery(sqlquery.toString(), null);
            ArrayList<String> result = new ArrayList<String>();
            while (c.moveToNext()){
                String name = c.getString(c.getColumnIndex("name"));
                String short_text = c.getString(c.getColumnIndex("short_text"));
                result.add(name);
                result.add(short_text);
            }
            Log.i("DB", "Result size is "+ result.size());
            db.close();
            return result;
        }

        public String getRandomSongName() {
            SQLiteDatabase db = getReadableDatabase();
            String sqlquery = "SELECT MAX(num) as num FROM songs";
            Cursor c = db.rawQuery(sqlquery, null);
            c.moveToFirst();
            int max = c.getInt(c.getColumnIndex(songs_num));

            Random rn = new Random();
            int rundom_song = rn.nextInt(max) + 1;

            String [] temp_result = getSongByNumber(rundom_song);
            String result = temp_result[0];
            db.close();
            return result;
        }

        public ArrayList<String> getSongsByTheme(String theme) {
            SQLiteDatabase db = getReadableDatabase();
            String sqlquery = "SELECT song_nums FROM themes WHERE theme = '" + theme + "';";
            Cursor c = db.rawQuery(sqlquery, null);
            c.moveToFirst();
            String song_nums = c.getString(c.getColumnIndex("song_nums")).trim();

            String sqlquery2 = "SELECT name FROM songs WHERE num IN (" + song_nums + ")";
            c = db.rawQuery(sqlquery2, null);
            ArrayList<String> result = new ArrayList<String>();

            while (c.moveToNext()){
                String song = c.getString(c.getColumnIndex("name"));
                result.add(song);
            }
            db.close();
            return result;
        }
    }