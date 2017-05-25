package com.magdy.mguide.UI;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.magdy.mguide.R;

public class RegisterActivity extends AppCompatActivity {

    EditText name ;
    EditText email;
    EditText pass ;
    Button register ;
    ProgressDialog progressDialog;
    DatabaseReference mDatabase;

    FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mauth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        progressDialog = new ProgressDialog(this);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.pass);
        register = (Button) findViewById(R.id.register );

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }
    void startRegister()
    {
        final String names = name.getText().toString();
        final String emails = email.getText().toString();
        String passw = pass.getText().toString() ;
        progressDialog.setMessage(getResources().getString(R.string.registering));
        progressDialog.show();
        if(!TextUtils.isEmpty(names) && !TextUtils.isEmpty(names) && !TextUtils.isEmpty(names))
        {
            mauth.createUserWithEmailAndPassword(emails,passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        if(mauth.getCurrentUser().getUid()!=null) {
                            DatabaseReference userDatabase = mDatabase.child(mauth.getCurrentUser().getUid()).child("info");
                            userDatabase.child("name").setValue(names);
                            userDatabase.child("email").setValue(emails);
                            userDatabase.child("image").setValue("default");
                            finish();
                        }
                    }

                }
            });

        }
        else {

            progressDialog.setMessage("Sign up failed");
            progressDialog.dismiss();
        }
    }
}
