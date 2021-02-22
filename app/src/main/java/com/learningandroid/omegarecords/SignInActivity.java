package com.learningandroid.omegarecords;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * SignInActivity allows end users to sign in this app using their Gmail accounts
 * When logged out, this activity will also show up to allow the end user to sign in again
 */
public class SignInActivity extends AppCompatActivity{

    private static final int SIGN_IN = 9001;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // create a google signin client which provides signin intent later
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // if this intent is accompanied with message sign out, sign out current account
        if(getIntent().hasExtra("sign_out")) {
            googleSignInClient.signOut();
            //Toast.makeText(this, "successfully logged out!", Toast.LENGTH_SHORT).show();
        }

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        findViewById(R.id.sign_in_button).setOnClickListener((View view) -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, SIGN_IN);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    /**
     * update the user interface once this activity is started
     * first update ME in navigationPane, then start the AppInfoActivity
     */
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            NavigationPane.updateAccount(account);

            Intent appInfoIntent = new Intent(this, AppInfoActivity.class);
            startActivity(appInfoIntent);
        }
    }

    /**
     * handles the result of signin activity
     * if the end user successfully signed in, invoke updateUI to redirect to AppInfoActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) { // result for sign in intent
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                updateUI(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}

