package fr.insalyon.mxyns.icrc.dna;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import java.util.Locale;
import java.util.function.Consumer;

import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputResult;
import fr.insalyon.mxyns.icrc.dna.sync.RestSync;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);


            Log.d("auto-single-line",getPreferenceManager().getPreferenceScreen().getPreferenceCount() + "");
            EditTextPreference.OnBindEditTextListener noMultilineAllowed = el -> el.setSingleLine(true);
            recursiveSetSingleLine(getPreferenceScreen(), noMultilineAllowed);

            findPreference("sync_try_rest").setVisible(String.valueOf(findPreference("sync_method").getKey()).equalsIgnoreCase("restAPI"));
            findPreference("sync_method").setOnPreferenceChangeListener((pref, key) -> {
                findPreference("sync_try_rest").setVisible(String.valueOf(key).equalsIgnoreCase("restAPI"));
                return true;
            });

            findPreference("sync_try_rest").setOnPreferenceClickListener(e -> {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsFragment.this.getContext());
                boolean result = sharedPreferences != null && RestSync.checkSettings(
                        sharedPreferences.getString("default_restAPI_url", null),
                        sharedPreferences.getString("default_restAPI_usr", null),
                        sharedPreferences.getString("default_restAPI_pwd", null)
                );

                new AlertDialog.Builder(SettingsFragment.this.getContext())
                        .setTitle("REST API " + (result ? "Succes" : "Error") )
                        .setMessage("Connection to the REST API " + (result ? "succeeded" : "failed. Perhaps the actual settings are wrong."))
                        .setPositiveButton("OK", null).create().show();

                return result;
            });
        }

        public void recursiveSetSingleLine(PreferenceGroup screen, EditTextPreference.OnBindEditTextListener noMultilineAllowed) {
            for (int i = 0; i < screen.getPreferenceCount(); i++) {
                Preference preference = screen.getPreference(i);
                if (preference instanceof PreferenceCategory)
                    recursiveSetSingleLine((PreferenceCategory) preference, noMultilineAllowed);
                else if (preference instanceof EditTextPreference)
                    ((EditTextPreference)preference).setOnBindEditTextListener(noMultilineAllowed);
            }
        }
    }
}