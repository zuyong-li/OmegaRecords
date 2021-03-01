package com.learningandroid.omegarecords.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewSource;
import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.domain.Address;
import com.learningandroid.omegarecords.domain.Company;
import com.learningandroid.omegarecords.domain.Geography;
import com.learningandroid.omegarecords.domain.LoggedInUser;
import com.learningandroid.omegarecords.domain.User;
import com.learningandroid.omegarecords.utils.ActivityUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * this activity displays the user detail
 * if the user is ME and ME.address/company are not set, redirect to EditProfileActivity
 */
public class ViewUserDetailsActivity extends NavigationPane
        implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {

    private User user = null;
    private static final String USER_URL = "https://robohash.org/";
    private static final String COM_URL = "https://source.unsplash.com/random/200x200?sig=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_details);

        fetchData();
        setData();
    }

    /**
     * Extract the index of user in USERS array whose detail is going to displayed
     */
    private void fetchData() {
        if(getIntent().hasExtra("user_details")) {
            user = ActivityUtils.getGsonParser().fromJson(getIntent().getStringExtra("user_details"), User.class);
        } else if(getIntent().hasExtra("logged_in_user_details")){
            user = ActivityUtils.getGsonParser().fromJson(getIntent().getStringExtra("logged_in_user_details"), LoggedInUser.class);
        }
    }

    /**
     * setup the layout to display user details
     * if position >= USERS.length, then the detail of ME will be displayed
     * if LoggedInUser.address/company are not set, redirect to EditProfileActivity
     */
    private void setData() {
        if (user != null) {
            // find the user and the user has detailed information to show
            if (user.getAddress() != null && user.getCompany() != null) {
                // set up the personal info card view
                ((TextView) findViewById(R.id.user_details_name)).setText(user.getName());
                ((TextView) findViewById(R.id.user_details_phone)).setText(user.getPhone());
                ((TextView) findViewById(R.id.user_details_email)).setText(user.getEmail());
                ((TextView) findViewById(R.id.user_details_website)).setText(user.getWebsite());
                ImageView userDetailsPhoto = findViewById(R.id.user_details_photo);
                if(user instanceof LoggedInUser && ((LoggedInUser) user).getSelfPortraitPath() != null) {
                    File file = new File(((LoggedInUser) user).getSelfPortraitPath());
                    userDetailsPhoto.setImageURI(Uri.fromFile(file));
                } else {
                    Picasso.get().load(USER_URL + user.getName()).into(userDetailsPhoto);
                }

                // set up the address info card view
                Address address = user.getAddress();
                ((TextView) findViewById(R.id.user_details_address_street_and_suite))
                        .setText(String.format("%s, %s", address.getStreet(), address.getSuite()));
                ((TextView) findViewById(R.id.user_details_address_city_and_zipcode))
                        .setText(String.format("%s, %s", address.getCity(), address.getZipcode()));
                Geography geo = address.getGeo();
                ((TextView) findViewById(R.id.user_details_address_geo))
                        .setText(String.format("%s, %s", geo.getLat(), geo.getLng()));

                SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.user_details_address_photo, mapFragment, null)
                        .commit();
                mapFragment.getMapAsync(this);

                // set up the company info card view
                Company company = user.getCompany();
                ((TextView) findViewById(R.id.user_details_com_name)).setText(company.getName());
                ((TextView) findViewById(R.id.user_details_com_catch_phrase))
                        .setText(company.getCatchPhrase());
                ((TextView) findViewById(R.id.user_details_com_business)).setText(company.getBs());
                ImageView userCompanyPhoto = findViewById(R.id.user_details_com_photo);
                Picasso.get().load(COM_URL + user.getId()).into(userCompanyPhoto);

                findViewById(R.id.user_details).setVisibility(View.VISIBLE);
            } else {
                // find the user, but lack of details
                findViewById(R.id.user_details).setVisibility(View.GONE);
                Toast.makeText(this, "Please update profile", Toast.LENGTH_SHORT).show();
                Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
                startActivity(editProfileIntent);
            }
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    private LatLng getLocation() {
        double lat = 38.578874, lng = -121.502319;
        if(user != null && user.getAddress() != null && user.getAddress().getGeo() != null) {
            Geography geo = user.getAddress().getGeo();
            if(!TextUtils.isEmpty(geo.getLat())) {
                lat = Double.parseDouble(geo.getLat());
            }
            if(!TextUtils.isEmpty(geo.getLng())) {
                lng = Double.parseDouble(geo.getLng());
            }
        }
        return new LatLng(lat, lng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng position = this.getLocation();
        googleMap.addMarker(new MarkerOptions().position(position));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        Toast.makeText(this, "click on address info for street view", Toast.LENGTH_SHORT).show();
        findViewById(R.id.user_details_address_info).setOnClickListener(v -> {
            SupportStreetViewPanoramaFragment supportStreetViewPanoramaFragment = SupportStreetViewPanoramaFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.street_view_fragment, supportStreetViewPanoramaFragment, null)
                    .commit();
            supportStreetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        });
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        LatLng position = this.getLocation();
        streetViewPanorama.setPosition(position, StreetViewSource.OUTDOOR);
        streetViewPanorama.setStreetNamesEnabled(true);
        streetViewPanorama.setPanningGesturesEnabled(true);
        streetViewPanorama.setZoomGesturesEnabled(true);
        streetViewPanorama.setUserNavigationEnabled(true);
    }
}