package ru.youthsongs;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;

public class Fragment_contents_byname_alphbet extends Fragment {
    /*
    Fragment that show buttons with uniq first letter of every song in database
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fraglayout_contents_byname, null);

        final DatabaseHelper sql = new DatabaseHelper(getActivity().getApplicationContext());
        ArrayList<String> songsfirstletter = sql.getsongsfirstletter();

        GridView gvMain = (GridView) v.findViewById(R.id.grid_songs_byname);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.griditem, R.id.push_button, songsfirstletter);
        gvMain.setAdapter(adapter);

        gvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String letter = parent.getItemAtPosition(position).toString();
                Log.i("Grid", "Item is " + letter);

                Fragment_contents_byname_list frag2 = new Fragment_contents_byname_list();

                Bundle bundle = new Bundle();
                bundle.putString("selected_letter", letter);
                frag2.setArguments(bundle);

                FragmentTransaction ftrans = getFragmentManager().beginTransaction();
                ftrans.replace(R.id.frgmCont, frag2);
                ftrans.addToBackStack(null);
                ftrans.commit();
            }
        });
        return v;
    }


}
