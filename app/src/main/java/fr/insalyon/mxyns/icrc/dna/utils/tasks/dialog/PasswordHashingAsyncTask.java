package fr.insalyon.mxyns.icrc.dna.utils.tasks.dialog;

import android.content.Context;

import androidx.preference.PreferenceManager;

import java.security.SecureRandom;

import at.favre.lib.bytes.Bytes;
import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies;
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
    protected Void doInBackground(String... credentials) {

        if (credentials.length == 0 || credentials[0] == null)
            return null;

        String hashed = hashPassword(credentials);
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(mContext.getResources().getString(R.string.settings_default_restAPI_pwd_key), hashed).apply();

        return null;
    }

    public String hashPassword(String[] credentials) {

        String sha_salt = credentials[0] + "78b2b9043cf3768e23a29fbe9719aa8362a048f0cc92479b07508de63b32783c41f7611c5ca7539d9e4425a4715150548ea346077486363516e900efa7c42aa3" + credentials[1];
        Bytes sha512 = Bytes.from((credentials[1] + credentials[0] + sha_salt).toCharArray()).hash("SHA-512");
        BCrypt.Hasher hasher = BCrypt.with(BCrypt.Version.VERSION_2A, new SecureRandom(), LongPasswordStrategies.hashSha512(BCrypt.Version.VERSION_2A));
        byte[] hashed = hasher.hash(cost, new byte[] {77, 97, 120, 111, 117, 80, 108, 97, 116, 65, 65, 112, 51, 120, 84, 67}, sha512.encodeUtf8ToBytes());

        return new String(hashed);
    }
}
