package ru.youthsongs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.youthsongs.R;

public class FragmentContentsByNameController extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_contents_byname, null);
        FragmentContentsByNameAlphbet frag2 = new FragmentContentsByNameAlphbet();

        FragmentTransaction ftrans = getFragmentManager().beginTransaction();
        ftrans.add(R.id.fragmFrame, frag2);
        ftrans.commit();
        return v;
    }
}
