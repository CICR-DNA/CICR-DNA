package fr.insalyon.mxyns.icrc.dna.sync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.SettingsActivity;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

/**
 * A synchronizer is a way to send data to any case data file to a receiver and then mark the file as sync-ed.
 */
public abstract class Sync {

    private static final ArrayList<Sync> syncList = new ArrayList<>();

    static {
        reInit(null);
    }

    public abstract void send(Context context, String filePath);

    /**
     * Attempts to send a list of files using any synchronizer after zipping them.
     *
     * @param filePaths list of paths to the files to send.
     */
    public static void attemptFileSync(Context context, List<String> filePaths) {

        File zipFile;
        try {
            zipFile = FileUtils.randomFileInDir(context.getCacheDir(), "tmp", "zip");
        } catch (IOException ex) {
            showSyncResultDialog(context, false);
            return;
        }
        zipFile = FileUtils.zip(filePaths, zipFile.getPath());

        attemptFileSync(context, zipFile.getPath());
    }

    /**
     * Attempts to send a single file using any synchronizer.
     *
     * @param filePath path of the file to send
     */
    public static void attemptFileSync(Context context, String filePath) {

        new AlertDialog.Builder(context)
                .setTitle(R.string.main_sync_selection_title)
                .setCancelable(true)
                .setItems(R.array.main_sync_selection_values, (dialog, which) -> {

                    Sync sync = syncList.get(which);

                    if (sync == null) {
                        showSyncResultDialog(context, false);
                        return;
                    }

                    sync.send(context, filePath);
                })
                .create()
                .show();
    }

    public static void reInit(Context context) {

        syncList.clear();

        if (context == null) return;

        Resources res = context.getResources();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String target_address = prefs.getString(res.getString(R.string.settings_default_email_key), null);
        syncList.add(new EmailSync(target_address));

        String url = prefs.getString(res.getString(R.string.settings_default_restAPI_url_key), null);
        String usr = prefs.getString(res.getString(R.string.settings_default_restAPI_usr_key), null);
        String pwd = prefs.getString(res.getString(R.string.settings_default_restAPI_pwd_key), null);
        syncList.add(new RestSync(url, usr, pwd));
    }

    public static void showSyncResultDialog(Context context, boolean result) {

        new AlertDialog.Builder(context)
                .setTitle(result ? R.string.main_sync_success_title : R.string.main_sync_failure_title)
                .setMessage(result ? R.string.main_sync_success_msg : R.string.main_sync_failure_msg)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null)
                .setNeutralButton(R.string.go_to_settings, (a,b) -> {
                    Intent intent = new Intent(context, SettingsActivity.class);
                    context.startActivity(intent);
                })
                .create()
                .show();
    }
}
