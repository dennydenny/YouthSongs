package ru.youthsongs.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.youthsongs.R;
import ru.youthsongs.entity.Song;
import ru.youthsongs.service.FlurryTrackingService;
import ru.youthsongs.service.TrackingService;
import ru.youthsongs.util.DatabaseHelper;
import ru.youthsongs.util.SearchSortingUtil;
import ru.youthsongs.util.SearchViewFormatter;


public class SearchActivity extends AppCompatActivity {

    private int easterEggCounter = 0;
    private TrackingService trackingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        // Init tracking service
        this.trackingService = new FlurryTrackingService();

        NeverEmptyListView search_result = (NeverEmptyListView) findViewById(R.id.search_result_list);
        handleIntent(getIntent());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search_result.getListview().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView t = (TextView) view.findViewById(R.id.item_title);

                // If t null it's mean that it's a english text.
                if (t == null) {
                    t = (TextView) view.findViewById(R.id.en_item_title);
                }
                String selected_song_name = t.getText().toString();

                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("selected_song", selected_song_name);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // Hiding search suggestion.
            TextView searchSuggestion = (TextView) findViewById(R.id.search_suggestion);
            searchSuggestion.setVisibility(View.GONE);

            String query = intent.getStringExtra(SearchManager.QUERY);
            DatabaseHelper sql = new DatabaseHelper(this);

            //ListView search_result = (ListView) findViewById(R.id.search_result_list);
            //search_result.setEmptyView(findViewById(R.id.empty_list_item));

            NeverEmptyListView search_result = (NeverEmptyListView) findViewById(R.id.search_result_list);

            // Tiny Easter egg
            search_result.setHolderClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    easterEggCounter++;
                    tinyEasterEgg();
                }
            });

            if (query.matches("[0-9]+")) {
                // Digit input
                Song resultSong = sql.getSongByNumber(Integer.valueOf(query));
                if (resultSong != null) {
                    ArrayList<Map<String, Object>> data = new ArrayList<>();
                    Map<String, Object> m;
                    m = new HashMap<>();
                    m.put("name", resultSong.getName());
                    m.put("short_text", resultSong.getText());
                    data.add(m);
                    SimpleAdapter adapter = new SimpleAdapter(this, data,
                            R.layout.search_result_item,
                            new String[]{"name", "short_text"},
                            new int[]{R.id.item_title,
                                    R.id.item_subtitle});
                    search_result.setAdapter(adapter);
                } else {
                    setEmptyAdapter(search_result);
                }
            } else if (query.matches("^[a-zA-Z ]+$")) {
                // English text
                List<Song> result = sql.getEnSongsByQuery(query);
                if (result.size() > 0) {

                    ArrayList<Map<String, Object>> data = new ArrayList<>(result.size());
                    HashMap<String, Object> m;

                    for (int i = 0; result.size() > i; i++) {
                        m = new HashMap<>();
                        Song song = result.get(i);
                        // Russian_name (English name)
                        m.put("name", song.getName());
                        m.put("en_name", "Англ: " + song.getEnName());
                        m.put("short_text", song.getText());
                        data.add(m);
                    }

                    SimpleAdapter adapter = new SimpleAdapter(this, data,
                            R.layout.search_result_english_item,
                            new String[]{"name", "en_name", "short_text"},
                            new int[]{R.id.en_item_title, R.id.en_item_subtitle,
                                    R.id.en_item_short_text});
                    search_result.setAdapter(adapter);
                } else {
                    setEmptyAdapter(search_result);
                }
            } else {
                // Text input
                List<Song> result = sql.getSongsByQuery(query);
                if (result.size() > 0) {
                    // Sort songs by Levenstain distance.
                    List<Song> sortedSongs = this.sortListByLevenstaindistanse(result, query);

                    ArrayList<Map<String, Object>> data = new ArrayList<>(sortedSongs.size());
                    HashMap<String, Object> m;

                    for (int i = 0; sortedSongs.size() > i; i++) {
                        m = new HashMap<>();
                        Song song = sortedSongs.get(i);
                        m.put("name", song.getName());
                        m.put("short_text", song.getText());
                        data.add(m);
                    }

                    SimpleAdapter adapter = new SimpleAdapter(this, data,
                            R.layout.search_result_item,
                            new String[]{"name", "short_text"},
                            new int[]{R.id.item_title,
                                    R.id.item_subtitle});
                    search_result.setAdapter(adapter);
                } else {
                    setEmptyAdapter(search_result);
                }
            }
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        searchView.setAlpha(0.87f);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        /*
        new SearchViewFormatter()
            .setSearchBackGroundResource(R.drawable.my_bg)
            .setSearchIconResource(R.drawable.my_ic, true, false) //true to icon inside edittext, false to outside
            .setSearchVoiceIconResource(R.drawable.my_ic)
            .setSearchTextColorResource(R.color.my_color)
            .setSearchHintColorResource(R.color.my_color)
            .setSearchCloseIconResource(R.drawable.my_ic)
            .setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
            .format(mSearchView);

            from (http://stackoverflow.com/questions/14137141/android-actionbar-customize-search-view)
         */
        new SearchViewFormatter()
                .setSearchIconResource(R.drawable.ic_search, true, false) //true to icon inside edittext, false to outside
                .setSearchTextColorResource(R.color.white)
                .setSearchHintColorResource(R.color.white)
                .setSearchCloseIconResource(R.drawable.ic_clear)
                .setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                .setSearchHintText("Поиск")
                .format(searchView);
        return true;
    }

    private void tinyEasterEgg() {
        if (easterEggCounter == 5) {
            Log.i("tinyEasterEgg()", "First stage of easter egg was found");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.searchEasterEggPopup), Toast.LENGTH_LONG).show();
            trackingService.trackEasterEggFound(1);
        }
        if (easterEggCounter == 10) {
            Log.i("tinyEasterEgg()", "Second stage of easter egg was found");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.searchEasterEggPopup2), Toast.LENGTH_LONG).show();
            trackingService.trackEasterEggFound(2);
        }
        if (easterEggCounter == 15) {
            Log.i("tinyEasterEgg()", "Last stage of easter egg was found. He is crazy.");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.searchEasterEggPopup3), Toast.LENGTH_LONG).show();
            trackingService.trackEasterEggFound(3);
            easterEggCounter = 0;
        }
    }

    private void setEmptyAdapter(NeverEmptyListView view) {
        String[] values = {};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        //Set NeverEmptyListView's adapter
        view.setAdapter(adapter);
    }

    private List<Song> sortListByLevenstaindistanse(List<Song> rawSongs, String query) {
        return SearchSortingUtil.sortListByLevenstaindistanse(rawSongs, query);
    }
}