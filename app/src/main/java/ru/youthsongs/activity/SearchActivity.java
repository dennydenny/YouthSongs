package ru.youthsongs.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.youthsongs.R;
import ru.youthsongs.util.DatabaseHelper;
import ru.youthsongs.util.SearchViewFormatter;

//import android.support.v7.widget.SearchView;

public class SearchActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ListView search_result = (ListView) findViewById(R.id.search_result_list);
        handleIntent(getIntent());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView t = (TextView) view.findViewById(R.id.item_title);
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
            String query = intent.getStringExtra(SearchManager.QUERY);
            DatabaseHelper sql = new DatabaseHelper(this);

            ListView search_result = (ListView) findViewById(R.id.search_result_list);
            search_result.setEmptyView(findViewById(R.id.empty_list_item));

            if (query.matches("[0-9]+")) {
                // Digit input
                String [] result = sql.GetSongByNumber(Integer.valueOf(query));
                if (result.length > 0) {
                    ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(result.length);
                    Map<String, Object> m;
                    m = new HashMap<String, Object>();
                    m.put("name", result[0]);
                    m.put("short_text", result[1]);
                    data.add(m);
                    SimpleAdapter adapter = new SimpleAdapter(this, data,
                            R.layout.search_result_item,
                            new String[] {"name", "short_text"},
                            new int[] {R.id.item_title,
                                    R.id.item_subtitle});
                    search_result.setAdapter(adapter);
                }
                else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, result);
                    search_result.setAdapter(adapter);
                }
            }
            else {
                // Text input
                ArrayList<String> result = sql.GetSongsByQuery(query);
                if (result.size() > 0) {

                    ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(result.size());
                    HashMap<String, Object> m;

                    for (int i = 0; result.size() > i; i += 2) {
                        int j = i + 1;
                        m = new HashMap<String, Object>();
                        m.put("name", result.get(i));
                        m.put("short_text", result.get(j));
                        data.add(m);
                    }

                    SimpleAdapter adapter = new SimpleAdapter(this, data,
                            R.layout.search_result_item,
                            new String[]{"name", "short_text"},
                            new int[]{R.id.item_title,
                                    R.id.item_subtitle});
                    search_result.setAdapter(adapter);
                }
                else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, result);
                    search_result.setAdapter(adapter);
                }
            }
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        SearchView searchView =  (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
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
}