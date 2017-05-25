package com.magdy.mguide.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magdy.mguide.Data.Contract;
import com.magdy.mguide.R;
import com.google.firebase.auth.FirebaseAuth;


public class ReviewEntryActivity extends AppCompatActivity {
    FirebaseAuth mauth ;
    FirebaseAuth.AuthStateListener mauthListener;
    DatabaseReference dbref;
    int movieID ;
    EditText editText ;
    Snackbar snackbar;
    CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_entry);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator);
        snackbar =  Snackbar.make(coordinatorLayout, getString(R.string.review_add), Snackbar.LENGTH_LONG);
        Intent intent = getIntent();
        movieID = intent.getIntExtra(Contract.Movie.COLUMN_MOVIE_ID,0);

        Toolbar toolbar = (Toolbar)findViewById(R.id.review_toolbar);
        toolbar.setTitle(R.string.add_review);

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mauth = FirebaseAuth.getInstance();
        mauthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent intent = new Intent(getBaseContext(),SignInActivity.class);
                    startActivity(intent);
                }
            }
        };
        mauth.addAuthStateListener(mauthListener);
        dbref =  FirebaseDatabase.getInstance().getReference().child("movies");
        editText = (EditText)findViewById(R.id.review_edittext);
        Button submit = (Button) findViewById(R.id.submit_review);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String review = editText.getText().toString();
                if(review!=null|| TextUtils.isEmpty(review)) {
                    dbref.child(String.valueOf(movieID)).child("user_reviews").child(mauth.getCurrentUser().getUid()).child("review").setValue(review);
                    snackbar.show();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
