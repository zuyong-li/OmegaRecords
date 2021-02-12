package com.learningandroid.omegarecords;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.learningandroid.omegarecords.domain.Address;
import com.learningandroid.omegarecords.domain.Company;
import com.learningandroid.omegarecords.domain.Geography;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends NavigationPane {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        onCreateDrawer(findViewById(R.id.drawer_layout));
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
        // save inputs
        Address address = new Address();
        address.setStreet(((EditText) findViewById(R.id.profile_address_street)).getText().toString());
        address.setSuite(((EditText) findViewById(R.id.profile_address_suite)).getText().toString());
        address.setCity(((EditText) findViewById(R.id.profile_address_city)).getText().toString());
        address.setZipcode(((EditText) findViewById(R.id.profile_address_zipcode)).getText().toString());

        Geography geo = new Geography();
        geo.setLat(((EditText) findViewById(R.id.profile_geo_lat)).getText().toString());
        geo.setLng(((EditText) findViewById(R.id.profile_geo_lng)).getText().toString());
        address.setGeo(geo);
        me.setAddress(address);

        Company company = new Company();
        company.setName(((EditText) findViewById(R.id.profile_com_name)).getText().toString());
        company.setCatchPhrase(((EditText) findViewById(R.id.profile_com_catch_phrase)).getText().toString());
        company.setBs(((EditText) findViewById(R.id.profile_com_business)).getText().toString());
        me.setCompany(company);

        me.setName(((EditText) findViewById(R.id.profile_user_name)).getText().toString());
        me.setPhone(((EditText) findViewById(R.id.profile_user_phone)).getText().toString());
        me.setEmail(((EditText) findViewById(R.id.profile_user_email)).getText().toString());
        me.setWebsite(((EditText) findViewById(R.id.profile_user_website)).getText().toString());

        // start to show user details
        Intent viewUserDetailsIntent = new Intent(this, ViewUserDetailsActivity.class);
        viewUserDetailsIntent.putExtra("user_position", users.length);
        startActivity(viewUserDetailsIntent);
    }
}