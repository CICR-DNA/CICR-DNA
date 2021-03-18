package fr.insalyon.mxyns.icrc.dna.utils.tasks;

import android.content.Context;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.sync.RestSync;

public class LoginAsyncTask extends DialogBlockingAsyncTask<String, Void, Boolean> {

    public LoginAsyncTask(Context context) {
        super(context);

        this.pending_title_id = R.string.settings_default_restAPI_try_pending_title;
        this.pending_msg_id = R.string.settings_default_restAPI_try_pending_message;
        this.error_title_id = R.string.settings_default_restAPI_try_error_title;
        this.error_msg_id = R.string.settings_default_restAPI_try_error_message;
        this.success_title_id = R.string.settings_default_restAPI_try_success_title;
        this.success_msg_id = R.string.settings_default_restAPI_try_success_message;

        this.success_button_text_id = android.R.string.ok;
        this.error_button_text_id = android.R.string.ok;
    }

    @Override
    protected Boolean doInBackground(String... url_usr_pwd) {

        boolean result = RestSync.login(mContext, url_usr_pwd[0], url_usr_pwd[1], url_usr_pwd[2]);

        if (!result)
            super.cancel(false);

        return result;
    }
}
