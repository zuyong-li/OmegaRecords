package com.learningandroid.omegarecords;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends NavigationPane {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

//        onCreateDrawer(findViewById(R.id.drawer_layout));
        setData();
        findViewById(R.id.profile_save_button).setOnClickListener(this::saveData);
    }

    private void setData() {
        account = GoogleSignIn.getLastSignedInAccount(this);
        ((EditText) findViewById(R.id.profile_user_name)).setText(account.getDisplayName());
        ((EditText) findViewById(R.id.profile_user_email)).setText(account.getEmail());
        Uri personPhoto = account.getPhotoUrl();
        if (personPhoto != null) {
            ImageView userPhoto = findViewById(R.id.profile_user_photo);
            Picasso.get().load(account.getPhotoUrl()).into(userPhoto);
        }
    }

    private void saveData(View view) {
    }
}