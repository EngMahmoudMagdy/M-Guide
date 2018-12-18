package com.magdy.mguide.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magdy.mguide.R;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {


    final static String EMAIL_TEXT = "email";
    final static String PASS_TEXT = "pass";
    Button loginBut, signupBut;
    EditText emailt, passt;
    TextView forgotT;
    ProgressDialog progressDialog;
    DatabaseReference mDatabase;
    FirebaseAuth mauth;
    FirebaseAuth.AuthStateListener mauthListener;
    CallbackManager callbackManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions("email","public_profile","user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        progressDialog = new ProgressDialog(this);

        loginBut = findViewById(R.id.login);
        signupBut = findViewById(R.id.signup);

        forgotT = findViewById(R.id.forgot_pass);
        forgotT.setOnClickListener(this);

        emailt = findViewById(R.id.emailText);
        passt = findViewById(R.id.passwordText);

        if (savedInstanceState != null) {
            emailt.setText(savedInstanceState.getString(EMAIL_TEXT));
            passt.setText(savedInstanceState.getString(PASS_TEXT));
        }
        signupBut.setOnClickListener(this);
        loginBut.setOnClickListener(this);

        mauth = FirebaseAuth.getInstance();
        mauthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    finish();
                }

            }
        };
    }
    void userUploadData(FirebaseUser user) {
        assert user != null;
        int x = Objects.requireNonNull(user.getDisplayName()).indexOf(' ');
        String personGivenName = user.getDisplayName().substring(0, x);
        String personFamilyName = user.getDisplayName().substring(x + 1, user.getDisplayName().length());
        String personEmail = user.getEmail();
        Uri personPhoto = user.getPhotoUrl();
        if (mauth.getCurrentUser() != null) {
            DatabaseReference usermDatabase = mDatabase.child(mauth.getCurrentUser().getUid());
            usermDatabase.child("info/first_name").setValue(personGivenName);
            usermDatabase.child("info/last_name").setValue(personFamilyName);
            usermDatabase.child("info/email").setValue(personEmail);
            assert personPhoto != null;
            usermDatabase.child("info/image").setValue(personPhoto.toString());
        }
    }
    void welcome()
    {
        Toast.makeText(SignInActivity.this,getString(R.string.welcome),
                Toast.LENGTH_SHORT).show();
    }
    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (mauth.getCurrentUser() != null) {
                                FirebaseDatabase.getInstance().getReference().child("users").child(mauth.getCurrentUser().getUid()).child("info").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Boolean b = dataSnapshot.child("is_first").getValue(Boolean.class);
                                        if (b==null)
                                        {
                                            userUploadData(mauth.getCurrentUser());
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                                welcome();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(),getString(R.string.fb_auth_wrong),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
        outState.putString(EMAIL_TEXT, (emailt != null || TextUtils.isEmpty(email)) ? email : "");
        outState.putString(PASS_TEXT, (passt != null || TextUtils.isEmpty(pass)) ? pass : "");
    }

    @Override
    public void onClick(View view) {

        if (view == loginBut) {
            setUpUser();
        } else if (view == signupBut) {
            Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        else if(view==forgotT)
        {
            startActivity(new Intent(getBaseContext(),ForgetPassActivity.class));
        }
    }

    private void setUpUser() {
        String email = emailt.getText().toString();
        String pass = passt.getText().toString();
        progressDialog.setMessage(getResources().getString(R.string.signing));
        progressDialog.show();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.email_pass_empty), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else
            mauth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.log_failed), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });


    }
}
