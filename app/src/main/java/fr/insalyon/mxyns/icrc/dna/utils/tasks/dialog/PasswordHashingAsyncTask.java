package fr.insalyon.mxyns.icrc.dna.utils.tasks.dialog;

import android.content.Context;

import androidx.preference.PreferenceManager;

import org.springframework.security.crypto.bcrypt.BCrypt;

import at.favre.lib.bytes.Bytes;
import fr.insalyon.mxyns.icrc.dna.R;

public class PasswordHashingAsyncTask extends DialogBlockingAsyncTask<String, Void, String> {

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
    protected String doInBackground(String... credentials) {

        if (credentials.length == 0 || credentials[0] == null)
            return null;

        // credentials = { password, email }
        String hashed = hashPassword(credentials);
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(mContext.getResources().getString(R.string.settings_default_restAPI_pwd_key), hashed).apply();

        return hashed;
    }

    public String hashPassword(String[] credentials) {

        String sha_salt = "78b2b9043cf3768e23a29fbe9719aa8362a048f0cc92479b07508de63b32783c41f7611c5ca7539d9e4425a4715150548ea346077486363516e900efa7c42aa3";
        Bytes sha512 = Bytes.from(credentials[0] + sha_salt + credentials[1]).hash("SHA-512");
        String hash = BCrypt.hashpw(sha512.encodeHex(), "$2a$" + cost + "$" + new String(encode(new byte[]{77, 97, 120, 111, 117, 80, 108, 97, 116, 65, 65, 112, 51, 120, 84, 67}, MAP)));
        System.out.println(hash);
        System.out.println(hash);
        System.out.println(hash);
        System.out.println(hash);
        return hash;
    }

    // RADIX64 ENCODING FROM at.favre.lib.bcrypt
    private static final byte[] MAP = new byte[]{
            '.', '/', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9'
    };

    private static byte[] encode(byte[] in, byte[] map) {
        int length = 4 * (in.length / 3) + (in.length % 3 == 0 ? 0 : in.length % 3 + 1);
        byte[] out = new byte[length];
        int index = 0, end = in.length - in.length % 3;
        for (int i = 0; i < end; i += 3) {
            out[index++] = map[(in[i] & 0xff) >> 2];
            out[index++] = map[((in[i] & 0x03) << 4) | ((in[i + 1] & 0xff) >> 4)];
            out[index++] = map[((in[i + 1] & 0x0f) << 2) | ((in[i + 2] & 0xff) >> 6)];
            out[index++] = map[(in[i + 2] & 0x3f)];
        }
        switch (in.length % 3) {
            case 1 : {
                out[index++] = map[(in[end] & 0xff) >> 2];
                out[index] = map[(in[end] & 0x03) << 4];
                break;
            }
            case 2 : {
                out[index++] = map[(in[end] & 0xff) >> 2];
                out[index++] = map[((in[end] & 0x03) << 4) | ((in[end + 1] & 0xff) >> 4)];
                out[index] = map[((in[end + 1] & 0x0f) << 2)];
                break;
            }
        }
        return out;
    }
}
