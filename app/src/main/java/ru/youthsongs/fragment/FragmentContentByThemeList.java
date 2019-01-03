package ru.youthsongs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.youthsongs.activity.MainActivity;
import ru.youthsongs.util.DatabaseHelper;

public class FragmentContentByThemeList extends ListFragment {

    /*
    Fragment that show list of songs, starts with selected first letter.
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Receiving selected theme from Budle from previous Fragment
        String selected_theme = getArguments().getString("selected_theme");
        super.onActivityCreated(savedInstanceState);

        //Fill view by content
        long timeout= System.currentTimeMillis();

        DatabaseHelper sql = new DatabaseHelper(getActivity());
        ArrayList <String> songsbytheme = sql.getSongsByTheme(selected_theme);

        timeout = System.currentTimeMillis() - timeout;

        Log.i("contents_bytheme_list", "Time of gettings song is " + timeout + " ms");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, songsbytheme);
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TextView t = (TextView) v;
        String selected_song_name = t.getText().toString();

        // Pass selected song to MainActivity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("selected_song", selected_song_name);
        startActivity(intent);
    }

}
