package ru.youthsongs.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.youthsongs.R;
import ru.youthsongs.entity.Song;
import ru.youthsongs.service.FlurryTrackingService;
import ru.youthsongs.service.TrackingService;
import ru.youthsongs.util.DatabaseHelper;
import ru.youthsongs.util.Formatter;

public class MainActivity extends AppCompatActivity {

    // Название песни, выбранной в данный момент.
    String selected_song;
    private static String previousSelectedSong = "dummy";
    private SharedPreferences sp;
    private String previousTextSize;
    // List of ids of text views which should meet shared preferences.
    private List<Integer> updatableTextViewsIds = new ArrayList<>();
    private DatabaseHelper sql;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private TrackingService trackingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        setConfiguredTextSize();
        // Init tracking service
        this.trackingService = new FlurryTrackingService();
        setContentView(R.layout.activity_main);

        if (sp.contains("lastSelectedSong")) selected_song = sp.getString("lastSelectedSong", null);

        // Проверяем, были ли отправлена какая-либо песня в эту активити.
        if (getIntent().getStringExtra("selected_song") != null) {
            selected_song = getIntent().getStringExtra("selected_song");
        }

        // Выбираем случайную песню.
        sql = new DatabaseHelper(this);
        if (selected_song == null) selected_song = sql.getRandomSongName();

        Toolbar toolbar = (Toolbar) findViewById(R.id.reader_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Log.d("onCreate", "onCreate second song is " + selected_song);

        // Отображаем песню.
        showSong(selected_song);
    }

    private void saveLastSelectedSong() {
        SharedPreferences.Editor editor1 = sp.edit();
        editor1.putString("lastSelectedSong",selected_song);
        editor1.commit();
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
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
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
            case R.id.menu_random_song:
                // Smells bad.
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("selected_song", sql.getRandomSongName());
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Init screen mode
        initScreenMode();

        // Checking does setting was changed. If so we'll update size of text.
        if (!previousTextSize.equals(sp.getString("textSizePref", getResources().getString(R.string.textSizesDefault)))) {
            updateTextSize();
        }
        setConfiguredTextSize();
        // Saving current song to be able open it once app will be closed.
        saveLastSelectedSong();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selected_song", selected_song);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selected_song = savedInstanceState.getString("selected_song");
    }

    public void showTextSong(Song song) {
        long timeout = System.currentTimeMillis();

        String song_name = song.getName();
        String song_text = song.getText();
        String song_num = song.getNumber();
        String songs_en_name = "";
        String songs_authors = "";
        String songs_alt_name = "";

        // Проверяем существование дополнительныйх данных песни.
        if (song.getEnName() != null) {
            songs_en_name = song.getEnName();
        }
        if (song.getAuthors() != null) {
            songs_authors = song.getAuthors();
        }
        if (song.getAltName() != null) {
            songs_alt_name = song.getAltName();
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

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
        params.addRule(RelativeLayout.ABOVE, R.id.reader_toolbar);
        params.setMargins(16, 0, 0, 0);
        scrollView.setLayoutParams(params);

        // Formatting song's name in toolbar (title)
        Toolbar toolbar = (Toolbar) findViewById(R.id.reader_toolbar);
        Log.i("Showsong", "Song name is " + song_name);
        toolbar.setTitle(song_name);

        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/calibri.otf");

        // Formatting song's number
        TextView song_number = new TextView(this);
        song_number.setId(R.id.song_number);
        // Adding to updatable array to be able to increase text size within preferences.
        this.updatableTextViewsIds.add(R.id.song_number);
        song_number.setTypeface(type);
        song_number.setTypeface(song_number.getTypeface(), Typeface.ITALIC);
        song_number.setGravity(Gravity.START);
        song_number.setPadding(0, 8, 0, 0);
        song_number.setText(song_num);

        ll.addView(song_number);

        // Formatting main text of song
        TextView tv = new TextView(this);

        tv.setTypeface(type);
        tv.setId(R.id.main_text);
        this.updatableTextViewsIds.add(R.id.main_text);
        //tv.setTextSize(getResources().getDimension(R.dimen.reader_main_text));
        tv.setLineSpacing(0.0f, 1.3f);

        ll.addView(tv);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        llp.setMargins(0, 16, 0, 20);
        tv.setLayoutParams(llp);

        SpannableString song_text_format = Formatter.makeFormatForText_v2(this, song_text);
        tv.setText(song_text_format);

        // Проверяем существование альт. названия и вставляем в подзаголовок тулбара.
        if (!songs_alt_name.isEmpty()) {
            Log.i("Formatter", "Subtitle is " + songs_alt_name);
            toolbar.setSubtitle("(" + songs_alt_name + ")");
            toolbar.setSubtitleTextColor(Color.WHITE);
        }

        // Проверяем существование анг. названия и вставляем под текстом песни.
        if (!songs_en_name.isEmpty()) {
            Log.i("Formatter", " songs_en_name is " + songs_en_name);

            TextView en_name = new TextView(this);
            en_name.setId(R.id.en_name);
            this.updatableTextViewsIds.add(R.id.en_name);
            en_name.setTextSize(getResources().getDimension(R.dimen.reader_secondary_text));
            en_name.setTypeface(type);
            en_name.setTypeface(en_name.getTypeface(), Typeface.BOLD_ITALIC);
            en_name.setText(Formatter.makeTextBold(songs_en_name));

            // Добавляем в UI.
            ll.addView(en_name);
        }

        if (!songs_authors.isEmpty()) {
            // Formatting authtors info
            TextView authors = new TextView(this);
            authors.setId(R.id.authors);
            this.updatableTextViewsIds.add(R.id.authors);
            authors.setTextSize(getResources().getDimension(R.dimen.reader_secondary_text));
            authors.setTypeface(type);
            SpannableString songs_authors_format = Formatter.makeFormatForAuthors(this, songs_authors);
            authors.setText(songs_authors_format);
            authors.setPadding(0, 0, 0, 16);
            // Adding to UI
            ll.addView(authors);
        }
        updateTextSize();

        // Checking worktime
        timeout = System.currentTimeMillis() - timeout;
        Log.i("Showtextsong", "Time of Showtextsong " + timeout + " ms");
    }

    /*
    Основной метод отображения песни.
     */
    private void showSong(String selected_song) {
        Log.d("showSong", "Opening song " + selected_song);
        DatabaseHelper sql = new DatabaseHelper(getApplicationContext());
        Song song = sql.getSongByName(selected_song);

        // Send statistics if new song opens
        if (!this.previousSelectedSong.equals(selected_song)) {
            this.trackSongOpened(song.getNumber());
            // Setting previous song to prevent stats duplication in future.
            this.previousSelectedSong = selected_song;
        }
        showTextSong(song);
    }

    // Method which reads text size from shared preferences and updates song's text size.
    private void updateTextSize() {

        for (Integer updatableTextViewsId : updatableTextViewsIds) {
            TextView tv = (TextView) findViewById(updatableTextViewsId);
            switch (sp.getString("textSizePref", getResources().getString(R.string.textSizesDefault))) {
                case "s":
                    tv.setTextAppearance(this, R.style.TextAppearance_AppCompat_Small);
                    break;
                case "m":
                    tv.setTextAppearance(this, R.style.TextAppearance_AppCompat_Medium);
                    break;
                case "l":
                    tv.setTextAppearance(this, R.style.TextAppearance_AppCompat_Large);
                    break;
            }
        }
    }

    private void setConfiguredTextSize() {
        this.previousTextSize = sp.getString("textSizePref", getResources().getString(R.string.textSizesDefault));
    }

    private void initScreenMode() {
        if (sp.getBoolean("keepScreenOn", false)) {
            // Указание держать экран включенным на всё время работы активити.
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
    // Wrapper under FlurryTrackingService.trackSongOpened
    private void trackSongOpened(final String songNumber) {
        // Checking internet permissions.
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.INTERNET ) != PackageManager.PERMISSION_GRANTED) {
            // If internet is not granted we will ask about it.
            Log.d("tracking", "WAT, we have no internet permission! Requesting...");
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.INTERNET},
                    0);
        }
        else {
            Log.d("tracking", "Good, we already have internet permission.");
            trackingService.trackSongOpened(songNumber);
        }
    }

    // Callback implementation of request permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RequestPermissions", "Good, permissions has been granted.");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.d("RequestPermissions", "Damn, permissions has not been granted :(");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}