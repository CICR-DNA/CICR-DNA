package fr.insalyon.mxyns.icrc.dna.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

/**
 * A synchronizer is a way to send data to any case data file to a receiver and then mark the file as sync-ed.
 */
public abstract class Sync {

    public boolean showDialog = true;

    private static HashMap<String, Sync> syncMap;

    static {
        reInit(null);
    }

    public abstract boolean send(Context context, String filePath);

    /**
     * Attempts to send a list of files using any synchronizer after zipping them.
     *
     * @param filePaths list of paths to the files to send.
     * @return true if synchronization was successful
     */
    public static Sync attemptFileSync(Context context, List<String> filePaths) {

        File zipFile;
        try {
            zipFile = FileUtils.randomFileInDir(context.getCacheDir(), "tmp", "zip");
        } catch (IOException ex) {
            return null;
        }
        zipFile = FileUtils.zip(filePaths, zipFile.getPath());

        Sync usedSynchronizer = attemptFileSync(context, zipFile.getPath());

        Log.d("email-zip-delete", "deleted zip");

        return usedSynchronizer;
    }

    /**
     * Attempts to send a single file using any synchronizer.
     *
     * @param filePath path of the file to send
     * @return true if synchronization was successful
     */
    public static Sync attemptFileSync(Context context, String filePath) {

        String selected = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.settings_sync_method_key), null);
        Sync sync = syncMap.get(selected);

        if (sync != null && sync.send(context, filePath))
            return sync;

        return null;
    }

    public static void reInit(Context context) {

        syncMap = new HashMap<>();

        if (context == null) return;

        Resources res = context.getResources();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String target_address = prefs.getString(res.getString(R.string.settings_default_email_key), null);
        if (target_address != null)
            syncMap.put("email", new EmailSync(target_address));

        String url = prefs.getString(res.getString(R.string.settings_default_restAPI_url_key), null);
        String usr = prefs.getString(res.getString(R.string.settings_default_restAPI_usr_key), null);
        String pwd = prefs.getString(res.getString(R.string.settings_default_restAPI_pwd_key), null);
        if (url != null && usr != null && pwd != null)
            syncMap.put("restAPI", new RestSync(url, usr, pwd));
    }

    public static void showSyncResultDialog(Context context, boolean result) {
        new AlertDialog.Builder(context)
                .setMessage("Super ça marche" + (result ? "" : " pas"))
                .setTitle("Synchronization " + (result ? "succes" : "failure"))
                .setCancelable(true)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
    public static void showSyncResultDialog(Context context, Sync sync) {

        boolean result = sync != null;
        if (result && sync.showDialog)
            showSyncResultDialog(context, true);
        else if (!result)
            showSyncResultDialog(context, false);
    }
}
