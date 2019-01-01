package ru.youthsongs.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.youthsongs.fragment.Fragment_contents_byname_alphbet;
import ru.youthsongs.R;

public class ContentsbynameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents_byname);

        Fragment_contents_byname_alphbet frag1 = new Fragment_contents_byname_alphbet();
        FragmentTransaction ftrans = getFragmentManager().beginTransaction();
        ftrans.add(R.id.frgmCont, frag1);
        ftrans.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void selectsong (String song_name) {
        Intent intent = new Intent(ContentsbynameActivity.this, MainActivity.class);
        intent.putExtra("selected_song", song_name);
        startActivity(intent);
    }


}
