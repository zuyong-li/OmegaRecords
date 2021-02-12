package com.learningandroid.omegarecords;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.learningandroid.omegarecords.utils.GsonParser;

public class AppInfoActivity extends NavigationPane {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        updateAccount();
        onCreateDrawer(findViewById(R.id.drawer_layout));
    }

    private void updateAccount() {
        if (account != null) { return; }
        if(getIntent().hasExtra("account")) {
            Bundle args = getIntent().getBundleExtra("account");
            String accountJson = args.getString("sign_in_account");
            account = GsonParser.getGsonParser().fromJson(accountJson, GoogleSignInAccount.class);
            me.setName(account.getDisplayName());
            me.setEmail(account.getEmail());
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_LONG).show();
        }
    }
}