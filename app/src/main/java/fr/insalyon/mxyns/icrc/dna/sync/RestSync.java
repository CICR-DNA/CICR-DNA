package fr.insalyon.mxyns.icrc.dna.sync;

import android.content.Context;
import android.util.Log;

/**
 * Synchronizer that uses a RESTful API to send the cases data
 */
public class RestSync extends Sync {

    @Override
    public boolean send(Context context, String filePath) {

        return false;
    }

    public static boolean checkSettings(String url, String username, String password) {

        Log.d("rest-sync-check", ""+url);
        Log.d("rest-sync-check", ""+username);
        Log.d("rest-sync-check", ""+password);

        // TODO
        return Math.random() < 0.5;
    }
}
