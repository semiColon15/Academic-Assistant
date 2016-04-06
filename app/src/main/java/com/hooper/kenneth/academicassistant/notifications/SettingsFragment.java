package com.hooper.kenneth.academicassistant.notifications;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.hooper.kenneth.academicassistant.R;


public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}