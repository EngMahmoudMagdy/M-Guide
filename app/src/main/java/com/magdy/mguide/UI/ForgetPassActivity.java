package com.magdy.mguide.UI;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.magdy.mguide.R;

import java.util.Locale;

public class ForgetPassActivity extends AppCompatActivity {
    EditText email;
    Toolbar toolbar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // for toolbar_home
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.forgot_password));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Button send =  findViewById(R.id.send);
        email =  findViewById(R.id.email);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty())
                {
                    Toast.makeText(getBaseContext(),getString(R.string.field_empty),Toast.LENGTH_SHORT).show();
                    return ;
                }
                forgotPass();
            }
        });
        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if(email.getText().toString().isEmpty())
                    {
                        Toast.makeText(getBaseContext(),getString(R.string.field_empty),Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    forgotPass();
                    return true;
                }
                return false;
            }
        });
    }
    void forgotPass()
    {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext()
                                    ,String.format(Locale.getDefault(),getString(R.string.email_reset) ,email.getText().toString()),Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),
                                    String.format(Locale.getDefault(),getString(R.string.error_email_pass_reset) ,email.getText().toString()),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
