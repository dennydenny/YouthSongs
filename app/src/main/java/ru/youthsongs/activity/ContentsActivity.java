package ru.youthsongs.activity;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

import ru.youthsongs.R;

public class ContentsActivity extends AppCompatActivity {
    LocalActivityManager mLocalActivityManager = new LocalActivityManager(this, false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.contents_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setTitle("");
        toolbar.setTitle("Выберите песню");
        setSupportActionBar(toolbar);

        // Setting TABS
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);


        mLocalActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(mLocalActivityManager);

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("По названию");
        tabSpec.setContent(new Intent(this, ContentsbynameActivity.class));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("По темам");
        tabSpec.setContent(new Intent(this, ContentsbythemeActivity.class));
        tabHost.addTab(tabSpec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contents_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_about:
                Toast.makeText(this, "Хорошее приложение", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume ()
    {
        mLocalActivityManager.dispatchResume();
        super.onResume();
    }

    @Override
    protected void onPause ()
    {
        mLocalActivityManager.dispatchPause(isFinishing());
        super.onPause();
    }

    @Override
    protected void onStop ()
    {
        mLocalActivityManager.dispatchStop();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        mLocalActivityManager.saveInstanceState();

    }

}
