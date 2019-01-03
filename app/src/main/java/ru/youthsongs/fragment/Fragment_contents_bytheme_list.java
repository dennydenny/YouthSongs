package ru.youthsongs.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.youthsongs.activity.ContentsbythemeActivity;
import ru.youthsongs.util.DatabaseHelper;

public class Fragment_contents_bytheme_list extends ListFragment {

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
        ((ContentsbythemeActivity) getActivity()).selectsong(selected_song_name);
    }

}
