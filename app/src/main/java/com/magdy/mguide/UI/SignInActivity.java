package com.magdy.mguide.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.magdy.mguide.R;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{


    Button loginBut, signupBut ;
    EditText emailt, passt ;
    TextView forgotT;

    ProgressDialog progressDialog;
    DatabaseReference mDatabase;

    FirebaseAuth mauth ;
    FirebaseAuth.AuthStateListener mauthListener;

    final static String EMAIL_TEXT = "email";
    final static String PASS_TEXT = "pass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        progressDialog = new ProgressDialog(this);

        loginBut = (Button) findViewById(R.id.login);
        signupBut = (Button) findViewById(R.id.signup);

        forgotT = (TextView) findViewById(R.id.forgotpass);

        emailt = (EditText) findViewById(R.id.emailText);
        passt = (EditText) findViewById(R.id.passwordText);

        if(savedInstanceState!=null)
        {
            emailt.setText(savedInstanceState.getString(EMAIL_TEXT));
            passt.setText(savedInstanceState.getString(PASS_TEXT));
        }
        signupBut.setOnClickListener(this);
        loginBut.setOnClickListener(this);

        mauth = FirebaseAuth.getInstance();
        mauthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    finish();
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(mauthListener);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        String email = emailt.getText().toString();
        String pass = passt.getText().toString();
        outState.putString(EMAIL_TEXT,(emailt!=null||TextUtils.isEmpty(email))?email:"");
        outState.putString(PASS_TEXT,(passt!=null||TextUtils.isEmpty(pass))?pass:"");
    }

    @Override
    public void onClick(View view) {

        if (view==loginBut)
        {
            setUpUser();
        }
        else  if (view==signupBut)
        {
            Intent intent = new Intent(getBaseContext(),RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    }

    private void setUpUser()
    {
        String email = emailt.getText().toString();
        String pass = passt.getText().toString();
        progressDialog.setMessage("Signing in ...");
        progressDialog.show();
        if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(pass) )
        {
            Toast.makeText(getBaseContext(),"email or password is empty",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
        else
            mauth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful())
                    {
                        Toast.makeText(getBaseContext(),"The Log in failed",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                    else {
                        progressDialog.dismiss();
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                }
            });



    }
}
