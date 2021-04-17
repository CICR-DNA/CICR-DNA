package fr.insalyon.mxyns.icrc.dna;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

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

            EditTextPreference password_pref = findPreference(res.getString(R.string.settings_default_restAPI_pwd_key));
            password_pref.setOnBindEditTextListener(el -> {
                el.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                el.setSelection(el.getText().length());
                el.setText("");
            });
            password_pref.setOnPreferenceChangeListener((e, v) -> {
                if (v == null) return false;

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsFragment.this.getContext());
                new PasswordHashingAsyncTask(getContext(), res.getInteger(R.integer.hashing_cost)).execute(v.toString(), sharedPreferences.getString(getContext().getResources().getString(R.string.settings_default_restAPI_usr_key), null));

                return false;
            });

            findPreference(res.getString(R.string.settings_try_rest_sync_key)).setOnPreferenceClickListener(e -> {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsFragment.this.getContext());

                new LoginAsyncTask(getContext()).execute();
                return false;
            });

            findPreference(res.getString(R.string.settings_clear_zips_key)).setOnPreferenceClickListener(e -> {
                boolean result = FileUtils.clearDir(SettingsFragment.this.getContext().getCacheDir());
                Toast.makeText(this.getContext(), result ? R.string.settings_misc_clear_zip_success_message : R.string.settings_misc_clear_zip_error_message, Toast.LENGTH_SHORT).show();
                return result;
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