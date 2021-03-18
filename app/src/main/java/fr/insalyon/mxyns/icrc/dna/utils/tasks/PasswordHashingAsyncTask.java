package fr.insalyon.mxyns.icrc.dna.utils.tasks;

import android.content.Context;

import androidx.preference.PreferenceManager;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.insalyon.mxyns.icrc.dna.R;

public class PasswordHashingAsyncTask extends DialogBlockingAsyncTask<String, Void, Void> {

    private final int cost;

    public PasswordHashingAsyncTask(Context context, int cost) {
        super(context);
        this.cost = cost;
        
        this.pending_title_id = R.string.settings_default_restAPI_pwd_hashing_pending_title; 
        this.pending_msg_id = R.string.settings_default_restAPI_pwd_hashing_pending_message; 
        this.error_title_id = R.string.settings_default_restAPI_pwd_hashing_error_title; 
        this.error_msg_id = R.string.settings_default_restAPI_pwd_hashing_error_message; 
        this.success_title_id = R.string.settings_default_restAPI_pwd_hashing_success_title; 
        this.success_msg_id = R.string.settings_default_restAPI_pwd_hashing_success_message;

        this.success_button_text_id = android.R.string.ok;
        this.error_button_text_id = android.R.string.cancel;
    }

    @Override
    protected Void doInBackground(String... passwords) {

        if (passwords.length == 0 || passwords[0] == null)
            return null;

        String hashed = BCrypt.withDefaults().hashToString(cost, passwords[0].toCharArray());
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(mContext.getResources().getString(R.string.settings_default_restAPI_pwd_key), hashed).apply();

        return null;
    }
}
