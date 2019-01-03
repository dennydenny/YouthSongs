package ru.youthsongs.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import ru.youthsongs.R;
import ru.youthsongs.util.DatabaseHelper;

public class Fragment_contents_byname_alphbet_backup extends Fragment {
    /*
    Fragment that show buttons with uniq first letter of every song in database
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fraglayout_contents_byname, null);

        TableLayout table_songs_byname;
        TableRow tr = null;


        final DatabaseHelper sql = new DatabaseHelper(getActivity().getApplicationContext());
        ArrayList<String> songsfirstletter = sql.getSongsFirstLetter();

        table_songs_byname = (TableLayout) v.findViewById(R.id.table_songs_byname);

        int i = 0;

        while (i < songsfirstletter.size()) {
            if (i % 5 == 0) {
                tr = new TableRow(getActivity());
                table_songs_byname.addView(tr);
            }
            Button btn = new Button(getActivity().getApplicationContext());
            btn.setText(songsfirstletter.get(i));
            btn.setId(i);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Button b = (Button)v;
                    String letter = b.getText().toString();

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
            tr.addView(btn);
            i++;
        }


        return v;
    }


}
