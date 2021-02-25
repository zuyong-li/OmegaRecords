package com.learningandroid.omegarecords;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.learningandroid.omegarecords.domain.Address;
import com.learningandroid.omegarecords.domain.Company;
import com.learningandroid.omegarecords.domain.Geography;
import com.learningandroid.omegarecords.domain.LoggedInUser;
import com.learningandroid.omegarecords.utils.ActivityUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * allows the user to change his/her profile
 * Assumption: user name and email are fixed because they are extracted from GoogleSignInAccount
 * a user is uniquely defined by his/her name and email
 */
public class EditProfileActivity extends NavigationPane {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 103;
    public static final int CAMERA_INTENT_REQUEST_CODE = 104;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 105;
    public static final String USER_KEY = "loggedInUser";
    LoggedInUser loggedInUser;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // create and setup the menu
        onCreateDrawer(findViewById(R.id.drawer_layout));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(savedInstanceState == null) {
            loggedInUser = loadLoggedInUser();
        } else {
            loggedInUser = ActivityUtils.getGsonParser().fromJson(savedInstanceState.getString(USER_KEY), LoggedInUser.class);
        }

        setData();
        findViewById(R.id.profile_save_button).setOnClickListener(this::saveData);

        findViewById(R.id.profile_user_photo).setOnClickListener((View view) -> {
            if(ContextCompat.checkSelfPermission(EditProfileActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermission("Allow camera access to take a profile photo",
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                dispatchTakePictureIntent();
            }
        });

        findViewById(R.id.profile_address_photo).setOnClickListener((View view) -> {
            if(ContextCompat.checkSelfPermission(EditProfileActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermission("Allow location access to fulfill or update address information",
                        Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                updateAddressInfo();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String loggedInUserJson = ActivityUtils.getGsonParser().toJson(loggedInUser);
        outState.putString(USER_KEY, loggedInUserJson);
    }

    /**
     * a helper method to set the text field with TEXT of a EditText identified by ID
     */
    private void setDataHelper(int id, String text) {
        if (text != null) {
            ((EditText) findViewById(id)).setText(text);
        }
    }

    /**
     * extract data from the loggedInUser and fill in the layout
     */
    private void setData() {
        ((TextView) findViewById(R.id.profile_user_username)).setText(loggedInUser.getName());
        setDataHelper(R.id.profile_user_phone, loggedInUser.getPhone());
        setDataHelper(R.id.profile_user_website, loggedInUser.getWebsite());

        Address addr = loggedInUser.getAddress();
        if(addr != null) {
            setDataHelper(R.id.profile_address_street, addr.getStreet());
            setDataHelper(R.id.profile_address_suite, addr.getSuite());
            setDataHelper(R.id.profile_address_city, addr.getCity());
            setDataHelper(R.id.profile_address_zipcode, addr.getZipcode());

            Geography geo = addr.getGeo();
            if (geo != null) {
                setDataHelper(R.id.profile_geo_lat, geo.getLat());
                setDataHelper(R.id.profile_geo_lng, geo.getLng());
            }
        }

        Company company = loggedInUser.getCompany();
        if(company != null) {
            setDataHelper(R.id.profile_com_name, company.getName());
            setDataHelper(R.id.profile_com_catch_phrase, company.getCatchPhrase());
            setDataHelper(R.id.profile_com_business, company.getBs());
        }

        if (loggedInUser.getSelfPortraitPath() != null) {
            File file = new File(loggedInUser.getSelfPortraitPath());
            ((ImageView) findViewById(R.id.profile_user_photo)).setImageURI(Uri.fromFile(file));
        } else {
            Toast.makeText(this, "no saved photo", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * save user inputs
     */
    private void saveData(View view) {
        Address address = new Address();
        address.setStreet(((EditText) findViewById(R.id.profile_address_street)).getText().toString());
        address.setSuite(((EditText) findViewById(R.id.profile_address_suite)).getText().toString());
        address.setCity(((EditText) findViewById(R.id.profile_address_city)).getText().toString());
        address.setZipcode(((EditText) findViewById(R.id.profile_address_zipcode)).getText().toString());

        Geography geo = new Geography();
        geo.setLat(((EditText) findViewById(R.id.profile_geo_lat)).getText().toString());
        geo.setLng(((EditText) findViewById(R.id.profile_geo_lng)).getText().toString());
        address.setGeo(geo);
        loggedInUser.setAddress(address);

        Company company = new Company();
        company.setName(((EditText) findViewById(R.id.profile_com_name)).getText().toString());
        company.setCatchPhrase(((EditText) findViewById(R.id.profile_com_catch_phrase)).getText().toString());
        company.setBs(((EditText) findViewById(R.id.profile_com_business)).getText().toString());
        loggedInUser.setCompany(company);

        loggedInUser.setPhone(((EditText) findViewById(R.id.profile_user_phone)).getText().toString());
        loggedInUser.setWebsite(((EditText) findViewById(R.id.profile_user_website)).getText().toString());

        String userText = ActivityUtils.getGsonParser().toJson(loggedInUser);
        String fileName = account.getEmail() + ".txt";
        ActivityUtils.saveData(this, userText, fileName);
    }

    /**
     * a helper method to request runtime permission
     * message: a string to show when shouldShowRequestRationale returns true
     * permission: the name of the permission to request
     */
    private void requestPermission(String message, @NonNull String permission, final int request_code) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton("Allow", (dialog, which) ->
                            ActivityCompat.requestPermissions(this, new String[] {permission}, request_code))
                    .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {permission}, request_code);
        }
    }

    /**
     * handle permission request result
     * if the permission is granted, start a camera activity by calling dispatchTakePictureIntent()
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                updateAddressInfo();
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * create a file for the profile photo
     * update ME.selfPortraitPath to this file
     * using timeStamp to create a collision-resistant file name
     */
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //createTempFile(prefix, suffix, directory)
        File image = File.createTempFile(imageFileName,".jpg", storageDir);
        loggedInUser.setSelfPortraitPath(image.getAbsolutePath());
        return image;
    }

    /**
     * start camera activity
     */
    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        // though (takePictureIntent.resolveActivity(getPackageManager()) != null) check is recommended
        // sometimes it does not work on camera app

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.learningandroid.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, CAMERA_INTENT_REQUEST_CODE);
        }
    }

    /**
     * handle activity result, specifically for camera activity invoked by dispatchTakePictureIntent()
     * if the activity succeeds, LOGGED_IN_USER.selfPortraitPath has to be correctly set
     * then display the profile image captured by the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_INTENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            File file = new File(loggedInUser.getSelfPortraitPath());
            ((ImageView) findViewById(R.id.profile_user_photo)).setImageURI(Uri.fromFile(file));
        }
    }

    /**
     * update the address information based on the current location
     */
    @SuppressLint("MissingPermission")
    private void updateAddressInfo() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, (Location location) -> {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<android.location.Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                    location.getLongitude(), 1);
                            android.location.Address address = addresses.get(0);
                            Log.d("location", address.toString());
                            ((EditText) findViewById(R.id.profile_address_street)).setText(address.getAddressLine(0));
                            ((EditText) findViewById(R.id.profile_address_suite)).setText(address.getAddressLine(1));
                            ((EditText) findViewById(R.id.profile_address_city)).setText(address.getLocality());
                            ((EditText) findViewById(R.id.profile_address_zipcode)).setText(address.getPostalCode());

                            ((EditText) findViewById(R.id.profile_geo_lat)).setText(String.valueOf(address.getLatitude()));
                            ((EditText) findViewById(R.id.profile_geo_lng)).setText(String.valueOf(address.getLongitude()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}