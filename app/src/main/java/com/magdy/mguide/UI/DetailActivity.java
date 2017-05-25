package com.magdy.mguide.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.magdy.mguide.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle extra = getIntent().getExtras();
        if (savedInstanceState == null) {
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            detailActivityFragment.setArguments(extra);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_panel2, detailActivityFragment)
                    .commit();
        }
    }

}
