package com.magdy.mguide.UI;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magdy.mguide.Data.Constants;
import com.magdy.mguide.Data.User;
import com.magdy.mguide.R;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    EditText fname , lname, email,pass , confpass;
    Button register ;
    DatabaseReference mDatabase;
    FirebaseAuth mauth;
    User user;
    CoordinatorLayout coordinatorLayout;
    FrameLayout progress ;
    Snackbar snackbar ;
    TextView textSnack ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mauth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS);
        fname =  findViewById(R.id.first_name);
        lname = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        confpass = findViewById(R.id.conf_pass);
        register = findViewById(R.id.register );
        coordinatorLayout =findViewById(R.id.coordinator);
        progress = findViewById(R.id.progress);
        snackbar = Snackbar.make(coordinatorLayout,"",Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundResource(R.color.grey);
        textSnack = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textSnack.setTextSize(18);
        user = new User();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
        confpass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    startRegister();
                    return true;
                }
                return false;
            }
        });

    }
    void startRegister()
    {
        progress.setVisibility(View.VISIBLE);
        final String fnames = fname.getText().toString();
        final String lnames = lname.getText().toString();
        final String emails = email.getText().toString();
        String passw = pass.getText().toString() ;
        String cpass = confpass.getText().toString() ;
        if(passw.isEmpty())
        {
            progress.setVisibility(View.GONE);
            textSnack.setText(getString(R.string.empty_pass));
            snackbar.show();
            return;
        }
        if(cpass.isEmpty())
        {
            progress.setVisibility(View.GONE);
            textSnack.setText(getString(R.string.empty_conf_pass));
            snackbar.show();
            return;
        }
        if (!cpass.equals(passw))
        {
            progress.setVisibility(View.GONE);
            textSnack.setText(getString(R.string.conf_pass_mismatch));
            snackbar.show();
            return;
        }
        if(!TextUtils.isEmpty(fnames) && !TextUtils.isEmpty(lnames) && !TextUtils.isEmpty(emails)&& !TextUtils.isEmpty(passw))
        {
            user.setEmail(emails);
            user.setFirst_name(fnames);
            user.setLast_name(lnames);
            mauth.createUserWithEmailAndPassword(emails,passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        mDatabase.child(Objects.requireNonNull(mauth.getCurrentUser()).getUid()).child(Constants.INFO).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    progress.setVisibility(View.GONE);
                                }
                            }
                        });
                        if (mauth.getCurrentUser()!=null)
                            mauth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        progress.setVisibility(View.GONE);
                                        snackbar.getView().setBackgroundResource(R.color.greenDark);
                                        textSnack.setTextColor(getResources().getColor(android.R.color.white));
                                        textSnack.setText(getString(R.string.email_conf));
                                        snackbar.show();
                                        /*Intent i = new Intent(getBaseContext(), MainActivity.class);
                                        startActivity(i);*/
                                        back();
                                    }
                                    else
                                    {
                                        snackbar.getView().setBackgroundResource(R.color.grey);
                                        textSnack.setTextColor(getResources().getColor(R.color.redDark));
                                        textSnack.setText(getString(R.string.email_conf_fail));
                                        snackbar.show();
                                    }
                                }
                            });
                    }
                }
            });
        }
        else {
            progress.setVisibility(View.GONE);
            textSnack.setText(getString(R.string.sign_up_failed));
        }
    }
    void beforeExitAlert()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.exiting));
        builder.setMessage(getString(R.string.are_you_sure));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                back();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog =builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.redDark));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccentDark));
    }
    void back()
    {
        progress.setVisibility(View.GONE);
        super.onBackPressed();
    }
    @Override
    public void onBackPressed() {
        if (fname.getText().toString().isEmpty()&&lname.getText().toString().isEmpty()&&email.getText().toString().isEmpty()&&pass.getText().toString().isEmpty()&&confpass.getText().toString().isEmpty())
        {
            back();
        }
        else
            beforeExitAlert();
    }

}