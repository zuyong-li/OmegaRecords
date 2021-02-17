package com.learningandroid.omegarecords;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.learningandroid.omegarecords.domain.Address;
import com.learningandroid.omegarecords.domain.Company;
import com.learningandroid.omegarecords.domain.Geography;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * allows the user to change his/her profile
 * Assumption: user name and email are fixed because they are extracted from GoogleSignInAccount
 * a user is uniquely defined by his/her name and email
 */
public class EditProfileActivity extends NavigationPane {

    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // create and setup the menu
        onCreateDrawer(findViewById(R.id.drawer_layout));

        setData();
        findViewById(R.id.profile_save_button).setOnClickListener(this::saveData);
        findViewById(R.id.profile_user_photo).setOnClickListener((View view) -> {
            if(ContextCompat.checkSelfPermission(EditProfileActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission();
            } else {
                dispatchTakePictureIntent();
            }
        });
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
     * extract data from ME and fill in the layout
     */
    private void setData() {
        ((TextView) findViewById(R.id.profile_user_username)).setText(me.getName());
        setDataHelper(R.id.profile_user_phone, me.getPhone());
        setDataHelper(R.id.profile_user_website, me.getWebsite());

        Address addr = me.getAddress();
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

        Company company = me.getCompany();
        if(company != null) {
            setDataHelper(R.id.profile_com_name, company.getName());
            setDataHelper(R.id.profile_com_catch_phrase, company.getCatchPhrase());
            setDataHelper(R.id.profile_com_business, company.getBs());
        }

        if (me.getSelfPortraitPath() != null) {
            File file = new File(me.getSelfPortraitPath());
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
        me.setAddress(address);

        Company company = new Company();
        company.setName(((EditText) findViewById(R.id.profile_com_name)).getText().toString());
        company.setCatchPhrase(((EditText) findViewById(R.id.profile_com_catch_phrase)).getText().toString());
        company.setBs(((EditText) findViewById(R.id.profile_com_business)).getText().toString());
        me.setCompany(company);

        me.setPhone(((EditText) findViewById(R.id.profile_user_phone)).getText().toString());
        me.setWebsite(((EditText) findViewById(R.id.profile_user_website)).getText().toString());

        saveMe();
    }

    /**
     * a helper method to request runtime camera permission
     */
    private void requestCameraPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setMessage("Allow camera access to take a profile photo.")
                    .setPositiveButton("Allow", (dialog, which) -> ActivityCompat
                            .requestPermissions(EditProfileActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE))
                    .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    /**
     * handle permission request result
     * if the permission is granted, start a camera activity by calling dispatchTakePictureIntent()
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
        me.setSelfPortraitPath(image.getAbsolutePath());
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
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    /**
     * handle activity result, specifically for camera activity invoked by dispatchTakePictureIntent()
     * if the activity succeeds, me.selfPortraitPath has to be correctly set
     * then display the profile image captured by the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            File file = new File(me.getSelfPortraitPath());
            ((ImageView) findViewById(R.id.profile_user_photo)).setImageURI(Uri.fromFile(file));
        }
    }
}