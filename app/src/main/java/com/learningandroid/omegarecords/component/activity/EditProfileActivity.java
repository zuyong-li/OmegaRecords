package com.learningandroid.omegarecords.component.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.storage.entity.Address;
import com.learningandroid.omegarecords.storage.entity.Company;
import com.learningandroid.omegarecords.storage.entity.Geography;
import com.learningandroid.omegarecords.storage.entity.LoggedInUser;
import com.learningandroid.omegarecords.utils.GsonProvider;

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

    private static final String USER_KEY = "loggedInUser";
    private static final String TAG = EditProfileActivity.class.getSimpleName();

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // create and setup the menu
        onCreateDrawer(findViewById(R.id.drawer_layout));

        initialize(savedInstanceState);
        setData();
        setListeners();
    }

    /**
     * initialize variables fusedLocationProviderClient, locationRequest and loggedInUser
     */
    private void initialize(Bundle savedInstanceState) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // after successfully updating the location, use the current location to fill out the address info
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                List<Location> locations = locationResult.getLocations();
                Location location = locations.size() > 0 ? locations.get(0) : null;
                if (location != null) {
                    updateAddressInfo(location);
                    stopLocationUpdates();
                }
            }
        };

        if (savedInstanceState == null) {
            loggedInUser = loadLoggedInUser();
        } else {
            loggedInUser = GsonProvider.getInstance().fromJson(savedInstanceState.getString(USER_KEY), LoggedInUser.class);
        }
    }

    /**
     * set OnClickListeners for save button, profile and address images
     */
    private void setListeners() {
        // click save button to save the user input
        findViewById(R.id.profile_save_button).setOnClickListener(this::saveData);

        // click profile image, displays a dialog and allows user to choose between
        // using a camera or picking image from gallery
        findViewById(R.id.profile_user_photo).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage("Set profile image by taking a photo using camera or choosing a photo from gallery")
                .setPositiveButton("Camera", (dialog, which) -> setProfileImageFromCamera())
                .setNegativeButton("Gallery", (dialog, which) -> setProfileImageFromGallery())
                .create().show());

        // click the address image to allow using current location
        findViewById(R.id.profile_address_photo).setOnClickListener((View view) -> {
            if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermission("Allow location access to auto fill or update address information",
                        Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);
            } else {
                checkSettingsAndUpdateAddressInfo();
            }
        });
    }

    /**
     * set the profile image by starting camera activity with runtime permission request
     */
    private void setProfileImageFromCamera() {
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission("Allow camera access to take a profile photo",
                    Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    /**
     * set the profile image by picking from gallery with runtime permission request
     */
    private void setProfileImageFromGallery() {
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission("Allow gallery access to choose profile photo",
                    Manifest.permission.READ_EXTERNAL_STORAGE, IMAGE_PERMISSION_CODE);
        } else {
            pickImageFromGallery();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String loggedInUserJson = GsonProvider.getInstance().toJson(loggedInUser);
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
        if (addr != null) {
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
        if (company != null) {
            setDataHelper(R.id.profile_com_name, company.getName());
            setDataHelper(R.id.profile_com_catch_phrase, company.getCatchPhrase());
            setDataHelper(R.id.profile_com_business, company.getBs());
        }

        Uri selfPortrait = loggedInUserViewModel.loadSelfPortrait(loggedInUser);
        if (selfPortrait != null) {
            ((ImageView) findViewById(R.id.profile_user_photo)).setImageURI(selfPortrait);
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

        loggedInUserViewModel.saveLoggedInUser(loggedInUser);
    }

    /**
     * handle permission request result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // if camera permission is granted, start a camera intent to set up profile image
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        // if location permission is granted, auto fill the address info
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                checkSettingsAndUpdateAddressInfo();
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        // if read external storage permission is granted, set profile image by picking from gallery
        if (requestCode == IMAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Gallery Permission Denied", Toast.LENGTH_SHORT).show();
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Gallery Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * create a file for the profile photo
     * update logged in user's selfPortraitPath to this file
     * using timeStamp to create a collision-resistant file name
     */
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
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
            startActivityForResult(takePictureIntent, CAMERA_INTENT_CODE);
        }
    }

    /**
     * handle activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if taking selfPortrait using camera is successful
        // save the image to gallery and set the imageView of profile user photo
        if (requestCode == CAMERA_INTENT_CODE && resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "image picked from camera");
            loggedInUserViewModel.saveSelfPortrait(loggedInUser);
            ((ImageView) findViewById(R.id.profile_user_photo)).setImageURI(loggedInUserViewModel.loadSelfPortrait(loggedInUser));
        }

        // if picking selfPortrait from gallery is successful
        // set the real path of the image as the selfPortraitPath of the logged in user
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Log.i(TAG, "image picked from gallery");
            loggedInUser.setSelfPortraitPath(loggedInUserViewModel.getFilePathFromUri(data.getData()));
            ((ImageView) findViewById(R.id.profile_user_photo)).setImageURI(loggedInUserViewModel.loadSelfPortrait(loggedInUser));
        }
    }

    /**
     * update the address information based on the given location
     */
    private void updateAddressInfo(@NonNull Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            android.location.Address address = addresses.get(0);

            ((EditText) findViewById(R.id.profile_address_street)).
                    setText(address.getAddressLine(0).split(",")[0]);
            ((EditText) findViewById(R.id.profile_address_city)).setText(address.getLocality());
            ((EditText) findViewById(R.id.profile_address_zipcode)).setText(address.getPostalCode());

            ((EditText) findViewById(R.id.profile_geo_lat)).setText(String.valueOf(address.getLatitude()));
            ((EditText) findViewById(R.id.profile_geo_lng)).setText(String.valueOf(address.getLongitude()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * check the location settings request
     * if the settings meet requirements, start updating location
     * otherwise, check if is possible to resolve the requirements by updating settings
     */
    private void checkSettingsAndUpdateAddressInfo() {
        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = settingsClient.checkLocationSettings(locationSettingsRequest);
        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> startLocationUpdates());
        locationSettingsResponseTask.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                try {
                    resolvableApiException.startResolutionForResult(EditProfileActivity.this, 1000);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });
    }

    /**
     * update the location
     */
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * cancel location updates
     */
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /**
     * pick the self portrait image from gallery
     */
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
}