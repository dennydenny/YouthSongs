package ru.youthsongs.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.youthsongs.activity.ContentsbynameActivity;
import ru.youthsongs.util.DatabaseHelper;

public class Fragment_contents_byname_list extends ListFragment {

    /*
    Fragment that show list of songs, starts with selected first letter.
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Receiving selected letter from Budle from previous Fragment
        String selected_letter = getArguments().getString("selected_letter");
        super.onActivityCreated(savedInstanceState);

        //Fill view by content
        DatabaseHelper sql = new DatabaseHelper(getActivity());
        ArrayList <String> songsbyfirstletter = sql.getSongsByFirstLetter(selected_letter);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, songsbyfirstletter);
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TextView t = (TextView) v;
        String selected_song_name = t.getText().toString();

        // Pass selected song to MainActivity
        ((ContentsbynameActivity) getActivity()).selectsong(selected_song_name);
    }

}
