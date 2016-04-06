package com.hooper.kenneth.academicassistant.notifications;

import android.app.Activity;
import android.os.Bundle;

import com.hooper.kenneth.academicassistant.LogInActivity;

public class SettingsActivity extends Activity {
    public static final String SETTINGS_KEY_BACKEND_URL = "http://academicassistantservice2.azurewebsites.net";
    public static final String SETTINGS_KEY_USERNAME = LogInActivity.loggedInUser;
    public static final String SETTINGS_KEY_DEVICEGUID = "settings_deviceguid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}