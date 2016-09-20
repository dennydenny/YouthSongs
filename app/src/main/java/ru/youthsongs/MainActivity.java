package ru.youthsongs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Название песни, выбранной в данный момент.
    String selected_song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Записываем настройки. Это необходимо, чтобы в будущем не обновлять UI в методе onResume().
        /*
        Комментирую, чтобы избавиться от этой настройки в будущем.
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show_chords1 = sPref.getBoolean("show_chords", false);
        sPref.edit().putBoolean("show_chords1", show_chords1);
        */

        // Выбираем случайную песню.
        DatabaseHelper sql = new DatabaseHelper(this);
        selected_song = sql.getrundomsong();

        // Проверяем, были ли отправлена какая-либо песня в эту активити.
        if (getIntent().getStringExtra("selected_song") != null) {
            selected_song = getIntent().getStringExtra("selected_song");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.reader_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Отображаем песню.
        showsong(selected_song);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_about:
                Toast.makeText(this, "Меню!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_contents:
                startActivity(new Intent(MainActivity.this, ContentsActivity.class));
                break;
            case R.id.menu_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /*
        // Check is setting "show_chords" was changed (previos value of setting in show_chords1)
        // TODO: Create feature that checks is settings were changed. If so - show song with new settings, else do nothing

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show_chords = sPref.getBoolean("show_chords", false);
        boolean show_chords1 = sPref.getBoolean("show_chords1", false);

        Log.i("Settings", "Now (on restart) show_chords is " + show_chords);
        Log.i("Settings", "Now (on restart) show_chords1 is " + show_chords1);

        if (show_chords != show_chords1) {
            showsong(sPref.getString("selected_song", ""));
            Toast.makeText(this, "Settings were changed!", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Restart!", Toast.LENGTH_SHORT).show();
        */
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selected_song", selected_song);
        Log.d("onSaveInstanceState", "onSaveInstanceState song is " + selected_song);

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selected_song = savedInstanceState.getString("selected_song");
        Log.d("onRestoreInstanceState", "onRestoreInstanceState song is " + selected_song);
        //showsong(selected_song);
    }

    public void showimagesong (String [] song_data) {
        String song_name = song_data[0];
        String song_file = song_data[1];

        Log.i("Chords", "Our song file is " + song_file);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.reader_relative_body);

        ImageView imageView = new ImageView(this);
        int id = this.getResources().getIdentifier(song_file, "drawable", this.getPackageName());

        Log.i("Chords", "Our song ID is " + id);

        imageView.setImageResource(id);
        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundResource(R.color.white);

        relativeLayout.addView(imageView);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)imageView.getLayoutParams();
        params.addRule(RelativeLayout.ABOVE, R.id.reader_toolbar);

        imageView.setLayoutParams(params);

        //PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);

        TextView reader_title = (TextView) findViewById(R.id.reader_title);
        reader_title.setText(song_name);
    }

    public void showtextsong (ArrayList<String> song_data) {
        long timeout= System.currentTimeMillis();

        String song_name = song_data.get(0);
        String song_text = song_data.get(1);
        String song_num = song_data.get(5);
        String songs_en_name = "";
        String songs_authors = "";
        String songs_alt_name = "";
        // Checking additional song's data
        if (song_data.get(2) != null) {
            songs_en_name = song_data.get(2);
        }
        if (song_data.get(3) != null) {
            songs_authors = song_data.get(3);
        }
        if (song_data.get(4) != null) {
            songs_alt_name = song_data.get(4);
        }

        /* Creating main layout
        Relative
        -> Scroll
            -> Linear
                -> Textview (number)
                -> Textview (text)
                -> Textview (english name)
                -> Textview (authors)
        */
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.reader_relative_body);

        ScrollView scrollView = new ScrollView(this);
        relativeLayout.addView(scrollView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(ll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)scrollView.getLayoutParams();
        params.addRule(RelativeLayout.ABOVE, R.id.reader_toolbar);
        params.setMargins(16, 0, 0, 0);
        scrollView.setLayoutParams(params);

        // Formatting song's name in toolbar (title)
        Toolbar toolbar = (Toolbar) findViewById(R.id.reader_toolbar);
        Log.i("Showsong", "Song is " + song_name);
        toolbar.setTitle(song_name);

        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/calibri.otf");

        // Formatting song's number
        TextView song_number = new TextView(this);
        song_number.setTypeface(type);
        song_number.setTypeface(song_number.getTypeface(), Typeface.ITALIC);
        song_number.setTextSize(getResources().getDimension(R.dimen.reader_secondary_text));
        song_number.setGravity(Gravity.START);
        song_number.setPadding(0, 8, 0, 0);
        song_number.setText(song_num);

        ll.addView(song_number);

        // Formatting song's main text
        TextView tv = new TextView(this);

        tv.setTypeface(type);
        tv.setId(R.id.main_text);
        //tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP  , getResources().getDimension(R.dimen.reader_main_text));
        tv.setTextSize(getResources().getDimension(R.dimen.reader_main_text));
        tv.setLineSpacing(0.0f, 1.3f);

        ll.addView(tv);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        llp.setMargins(0, 16, 0, 20);
        tv.setLayoutParams(llp);

        SpannableString song_text_format = Formatter.makeFormatForText_v2(this, song_text);
        tv.setText(song_text_format);

        // Checking alternative song's name
        if (!songs_alt_name.isEmpty()) {
            Log.i("Formatter", "Subtitle is " + songs_alt_name);
            toolbar.setSubtitle("(" + songs_alt_name + ")");
            toolbar.setSubtitleTextColor(Color.WHITE);
        }

        if (!songs_en_name.isEmpty()) {
            Log.i("Formatter", " songs_en_name is " + songs_en_name);

            // Formatting en_name of song
            TextView en_name = new TextView(this);
            en_name.setTextSize(getResources().getDimension(R.dimen.reader_secondary_text));
            en_name.setTypeface(type);
            en_name.setTypeface(en_name.getTypeface(), Typeface.BOLD_ITALIC);
            en_name.setText(songs_en_name);
            // Adding to UI
            ll.addView(en_name);
        }

        if (!songs_authors.isEmpty()) {
            Log.i("Formatter", " songs_authors is " + songs_authors);

            // Formatting authtor's info
            TextView authors = new TextView(this);
            authors.setTextSize(getResources().getDimension(R.dimen.reader_secondary_text));
            authors.setTypeface(type);
            SpannableString songs_authors_format = Formatter.makeFormatForAuthors(this, songs_authors);
            authors.setText(songs_authors_format);
            authors.setPadding(0, 0, 0, 16);
            // Adding to UI
            ll.addView(authors);
        }
        // Testing
        Log.i("Showtextsong", "Child count is  " + relativeLayout.getChildCount());


        // Checking worktime
        timeout = System.currentTimeMillis() - timeout;
        Log.i("Showtextsong", "Time of Showtextsong " + timeout + " ms");
        }

    private boolean isshowchordsettingset(){
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show_chords = sPref.getBoolean("show_chords", false);
        if (show_chords) {
            return true;
        }
        else {
            return false;
        }
    }


    /*
    Основной метод отображения песни.
     */
    private void showsong (String selected_song) {
        DatabaseHelper sql = new DatabaseHelper(getApplicationContext());
        showtextsong(sql.getsongbyname(selected_song));
        }
}