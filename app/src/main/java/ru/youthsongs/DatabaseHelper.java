package ru.youthsongs;

/**
 * Created by Lobzik on 30.01.2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import java.util.ArrayList;
import java.util.Random;

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

        private static final String DATABASE_NAME = "Sbornik_v8_t.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper (Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        public String [] getsongbynumber(int num) {

            SQLiteDatabase db = getReadableDatabase();

            String sqlquery = "SELECT name, substr (text, 1, 40) as text FROM songs WHERE num = " + num;

            //String [] sqlSelect = {songs_name, songs_text};
            //String sqlTables = songs;
            //String where = songs_num + " = " + num;

            //qb.setTables(sqlTables);
           // qb.appendWhere(where);
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
            return answer;
        }


        /*
        Returns song's data by name of song.
        @param name name of song
        @return ArrayList<String>:
        [0] songs_name
        [1] songs_text
        [2] songs_en_name if exists
        [3] songs_authors if exists
        [4] songs_alt_name if exists
        [5] songs_number
         */
        public ArrayList<String> getsongbyname(String name) {
            long timeout= System.currentTimeMillis();

            SQLiteDatabase db = getReadableDatabase();
            String sqlquery = "SELECT name, text, en_name, authors, alt_name, num FROM songs WHERE name ='" + name + "'";
            Cursor c = db.rawQuery(sqlquery, null);
            c.moveToFirst();
            ArrayList<String> result = new ArrayList<String> ();

            if (c.getCount() == 0) {
                return result;
            }

            // Song name and text always exists
            result.add(c.getString(c.getColumnIndex(songs_name)));
            result.add(c.getString(c.getColumnIndex(songs_text)));



            if (c.getString(c.getColumnIndex(songs_en_name)) != null) {
                result.add(c.getString(c.getColumnIndex(songs_en_name)));
            }
                else {
                    result.add(null);
                }
            if (c.getString(c.getColumnIndex(songs_authors)) != null) {
                result.add(c.getString(c.getColumnIndex(songs_authors)));
            }
                else {
                    result.add(null);
                }
            if (c.getString(c.getColumnIndex(songs_alt_name)) != null) {
                result.add(c.getString(c.getColumnIndex(songs_alt_name)));
            }
            else {
                result.add(null);
            }

            // Song number always exists
            result.add(c.getString(c.getColumnIndex(songs_num)));

            timeout = System.currentTimeMillis() - timeout;

            Log.i("DB", "Time of getsongbyname " + timeout + " ms");
            Log.i("getsongbyname", "Size of output array is " + result.size());

            return result;
        }

        public ArrayList<String> getsongsfirstletter (){
            SQLiteDatabase db = getReadableDatabase();
            //String sqlquery = "SELECT DISTINCT substr (name, 1, 1) as letter FROM songs ORDER BY letter";
            String sqlquery = "SELECT DISTINCT substr (name, 1, 1) as letter FROM songs WHERE letter != '' ORDER BY letter";
            Cursor c = db.rawQuery(sqlquery, null);
            ArrayList<String> result = new ArrayList<String>();
            while (c.moveToNext()){
                String letter = c.getString(c.getColumnIndex("letter"));
                result.add(letter);
            }
            /*
            SELECT DISTINCT substr (name, 1, 1) as letter
FROM songs
WHERE letter != ""
ORDER BY letter
             */

            Log.i("getsongsfirstletter", "Result is " + result.size());
            return result;
        }

        public ArrayList<String> getsongsbyfirstletter (String first_letter){
            SQLiteDatabase db = getReadableDatabase();
            String sqlquery = "SELECT name FROM songs WHERE name like '" + first_letter + "%' ORDER BY name";
            Cursor c = db.rawQuery(sqlquery, null);
            ArrayList<String> songsbyfirstletter = new ArrayList<String>();
            while (c.moveToNext()){
                String letter = c.getString(c.getColumnIndex(songs_name));
                songsbyfirstletter.add(letter);
            }

            return songsbyfirstletter;
        }

        public ArrayList<String> getsongsbyquery (String query) {
            SQLiteDatabase db = getReadableDatabase();
            String querylow = query.toLowerCase();
            String queryup = Character.toString(querylow.charAt(0)).toUpperCase()+querylow.substring(1);

            String sqlquery = "SELECT DISTINCT name, substr (text, 1, 40) as short_text " +
                    "FROM songs " +
                    "WHERE text LIKE " + "'%" + querylow + "%'" +
                    " OR" +
                    " name LIKE " + "'%" + querylow + "%'" +
                     " OR" + " text LIKE " + "'%" + queryup + "%'" +
            " OR" +
                    " name LIKE " + "'%" + queryup + "%'";
            Log.i("DB", "Query is "+ sqlquery);
            Cursor c = db.rawQuery(sqlquery, null);
            ArrayList<String> result = new ArrayList<String>();
            while (c.moveToNext()){
                String name = c.getString(c.getColumnIndex("name"));
                String short_text = c.getString(c.getColumnIndex("short_text"));
                result.add(name);
                result.add(short_text);
            }
            Log.i("DB", "Result size is "+ result.size());
            return result;
        }

        public String [] getimagesongbyname(String name) {
            SQLiteDatabase db = getReadableDatabase();
            String sqlquery = "SELECT name, text, file FROM songs WHERE name ='" + name + "'";
            Cursor c = db.rawQuery(sqlquery, null);
            c.moveToFirst();

            if (c.getCount() == 0) {
                String [] empty = {};
                return empty;
            }
            //TODO: Write errors if result is empty

            String s_name = c.getString(c.getColumnIndex(songs_name));
            String s_filename = c.getString(c.getColumnIndex(songs_file));
            String [] answer = {s_name, s_filename};
            return answer;
        }

        public String getrundomsong () {
            SQLiteDatabase db = getReadableDatabase();
            String sqlquery = "SELECT MAX(num) as num FROM songs";
            Cursor c = db.rawQuery(sqlquery, null);
            c.moveToFirst();
            int max = c.getInt(c.getColumnIndex(songs_num));

            Random rn = new Random();
            int rundom_song = rn.nextInt(max) + 1;

            String [] temp_result = getsongbynumber(rundom_song);
            String result = temp_result[0];
            return result;
        }

        public ArrayList<String> getsongsbytheme (String theme) {
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

            return result;
        }
    }