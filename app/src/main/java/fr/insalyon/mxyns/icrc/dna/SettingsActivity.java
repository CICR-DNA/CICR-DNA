package fr.insalyon.mxyns.icrc.dna;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;

import fr.insalyon.mxyns.icrc.dna.sync.Sync;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;
import fr.insalyon.mxyns.icrc.dna.utils.tasks.dialog.LoginAsyncTask;
import fr.insalyon.mxyns.icrc.dna.utils.tasks.dialog.PasswordHashingAsyncTask;

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

    @Override
    protected void onDestroy() {

        Sync.reInit(this);
        super.onDestroy();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final Resources res = getResources();

            Log.d("auto-single-line", getPreferenceManager().getPreferenceScreen().getPreferenceCount() + "");
            EditTextPreference.OnBindEditTextListener noMultilineAllowed = el -> {
                el.setSingleLine(true);
                el.setSelection(el.getText().length());
            };
            recursiveSetSingleLine(getPreferenceScreen(), noMultilineAllowed);

            findPreference(res.getString(R.string.settings_try_rest_sync_key)).setVisible(getPreferenceScreen().getSharedPreferences().getString(res.getString(R.string.settings_sync_method_key), null).equalsIgnoreCase("restAPI"));
            findPreference(res.getString(R.string.settings_sync_method_key)).setOnPreferenceChangeListener((pref, key) -> {
                findPreference(res.getString(R.string.settings_try_rest_sync_key)).setVisible(String.valueOf(key).equalsIgnoreCase("restAPI"));
                return true;
            });

            EditTextPreference pref = findPreference(res.getString(R.string.settings_default_restAPI_pwd_key));
            pref.setOnBindEditTextListener(el -> {
                el.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                el.setSelection(el.getText().length());
            });
            pref.setOnPreferenceChangeListener((e, v) -> {
                if (v == null) return false;

                new PasswordHashingAsyncTask(getContext(), res.getInteger(R.integer.hashing_cost)).execute(v.toString()); // TODO cost => res
                return false;
            });

            findPreference(res.getString(R.string.settings_try_rest_sync_key)).setOnPreferenceClickListener(e -> {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsFragment.this.getContext());

                LoginAsyncTask login = new LoginAsyncTask(getContext());
                login.execute(
                        sharedPreferences.getString(res.getString(R.string.settings_default_restAPI_url_key), null),
                        sharedPreferences.getString(res.getString(R.string.settings_default_restAPI_usr_key), null),
                        sharedPreferences.getString(res.getString(R.string.settings_default_restAPI_pwd_key), null)
                );

                return false;
            });

            findPreference(res.getString(R.string.settings_clear_zips_key)).setOnPreferenceClickListener(e -> {
                FileUtils.clearDir(SettingsFragment.this.getContext().getCacheDir());
                return true;
            });
        }

        public void recursiveSetSingleLine(PreferenceGroup screen, EditTextPreference.OnBindEditTextListener noMultilineAllowed) {
            for (int i = 0; i < screen.getPreferenceCount(); i++) {
                Preference preference = screen.getPreference(i);
                if (preference instanceof PreferenceCategory)
                    recursiveSetSingleLine((PreferenceCategory) preference, noMultilineAllowed);
                else if (preference instanceof EditTextPreference)
                    ((EditTextPreference) preference).setOnBindEditTextListener(noMultilineAllowed);
            }
        }
    }
}