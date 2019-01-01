package ru.youthsongs.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.youthsongs.fragment.Fragment_contents_bytheme;
import ru.youthsongs.R;

public class ContentsbythemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents_bytheme);

        Fragment_contents_bytheme frag1 = new Fragment_contents_bytheme();
        FragmentTransaction ftrans = getFragmentManager().beginTransaction();
        ftrans.add(R.id.contents_by_theme_frame, frag1);
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
        Intent intent = new Intent(ContentsbythemeActivity.this, MainActivity.class);
        intent.putExtra("selected_song", song_name);
        startActivity(intent);
    }
}
