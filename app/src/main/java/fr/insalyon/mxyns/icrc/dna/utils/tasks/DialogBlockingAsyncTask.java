package fr.insalyon.mxyns.icrc.dna.utils.tasks;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

public abstract class DialogBlockingAsyncTask<T, R, S> extends AsyncTask<T, R, S> {

    protected final Context mContext;
    private AlertDialog currentDialog;

    @StringRes
    protected int pending_title_id, pending_msg_id,
        error_title_id, error_msg_id,
        success_title_id, success_msg_id;

    @StringRes
    protected int success_button_text_id, error_button_text_id;

    public DialogBlockingAsyncTask(Context context) {

        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (currentDialog != null) // always expect the unexpected
            currentDialog.cancel();

        currentDialog = new AlertDialog.Builder(mContext)
                .setTitle(pending_title_id)
                .setMessage(pending_msg_id)
                .setCancelable(false)
                .create();

        currentDialog.show();
    }

    @Override
    protected void onCancelled(S unused) {
        super.onCancelled(unused);
        cancel();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel();
    }

    public void cancel() {

        if (currentDialog != null)
            currentDialog.cancel();

        currentDialog = new AlertDialog.Builder(mContext)
                .setTitle(error_title_id)
                .setMessage(error_msg_id)
                .setPositiveButton(error_button_text_id, null)
                .create();

        currentDialog.show();
    }

    @Override
    protected void onPostExecute(S unused) {
        super.onPostExecute(unused);

        if (currentDialog != null)
            currentDialog.cancel();

        currentDialog = new AlertDialog.Builder(mContext)
                .setTitle(success_title_id)
                .setMessage(success_msg_id)
                .setPositiveButton(success_button_text_id, null)
                .create();

        currentDialog.show();
    }
}