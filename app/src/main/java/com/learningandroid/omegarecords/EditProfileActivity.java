package com.learningandroid.omegarecords;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.learningandroid.omegarecords.domain.Address;
import com.learningandroid.omegarecords.domain.Company;
import com.learningandroid.omegarecords.domain.Geography;

public class EditProfileActivity extends NavigationPane {

    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        onCreateDrawer(findViewById(R.id.drawer_layout));
        setData();
        findViewById(R.id.profile_save_button).setOnClickListener(this::saveData);
        findViewById(R.id.profile_user_photo).setOnClickListener((View view) -> {
            if(ContextCompat.checkSelfPermission(EditProfileActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission();
            } else {
                openCamera();
            }
        });
    }

    private void requestCameraPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setMessage("Allow camera access to take a profile photo. You can revoke camera access in settings.")
                    .setPositiveButton("Allow", (dialog, which) -> ActivityCompat
                            .requestPermissions(EditProfileActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE))
                    .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE) {
            selfPortrait = (Bitmap) data.getExtras().get("data");
            Log.d("Writing image", "Write profile image");
            System.out.println(selfPortrait);
            ((ImageView) findViewById(R.id.profile_user_photo)).setImageBitmap(selfPortrait);
        }
    }

    private void setDataHelper(int id, String text) {
        if (text != null) {
            ((EditText) findViewById(id)).setText(text);
        }
    }

    private void setData() {
        setDataHelper(R.id.profile_user_name, me.getName());
        setDataHelper(R.id.profile_user_phone, me.getPhone());
        setDataHelper(R.id.profile_user_email, me.getEmail());
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

        if (selfPortrait != null) {
            ((ImageView) findViewById(R.id.profile_user_photo)).setImageBitmap(selfPortrait);
        } else {
            Toast.makeText(this, "no saved photo", Toast.LENGTH_SHORT).show();
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
    }
}