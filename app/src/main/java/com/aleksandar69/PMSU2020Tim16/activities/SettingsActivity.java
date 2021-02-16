package com.aleksandar69.PMSU2020Tim16.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.aleksandar69.PMSU2020Tim16.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat /*implements SharedPreferences.OnSharedPreferenceChangeListener*/ {

        //SharedPreferences sharedPreferences;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
   /*         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
           // onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_sync));
            onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_sync_list));*/


        }

        private static SettingsFragment newInstance(){
            Bundle args = new Bundle();
            SettingsFragment settingsf = new SettingsFragment();
            settingsf.setArguments(args);
            return settingsf;
        }


/*        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else {
                preference.setSummary(sharedPreferences.getString(key, ""));

            }
        }*/

        @Override
        public void onResume() {
            super.onResume();
            //register the preferenceChange listener
/*            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);*/
        }

        @Override
        public void onPause() {
            super.onPause();
            //unregister the preference change listener
/*            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);*/
        }
    }
}