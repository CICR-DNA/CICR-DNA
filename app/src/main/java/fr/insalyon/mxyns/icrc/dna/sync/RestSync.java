package fr.insalyon.mxyns.icrc.dna.sync;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Synchronizer that uses a RESTful API to send the cases data
 */
public class RestSync extends Sync {

    String url;
    String username;
    String password_hash;

    public RestSync(String url, String username, String password_hash) {
        this.url = url;
        this.username = username;
        this.password_hash = password_hash;
    }

    @Override
    public boolean send(Context context, String filePath) {

        return false;
    }

    public static boolean checkSettings(String url_str, String username, String password) {

        Log.d("rest-sync-check", "" + url_str);
        Log.d("rest-sync-check", "" + username);
        Log.d("rest-sync-check", "" + password);

        OkHttpClient httpClient = new OkHttpClient();
        Request req = new Request.Builder()
                .url(url_str)
                .get()
                .build();

        // TODO add authentication
        AtomicReference<Response> res = new AtomicReference<>();
        Thread thread = new Thread(() -> {

            try {
                res.set(httpClient.newCall(req).execute());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            return false;
        }

        return res.get() != null && res.get().code() == 200;
    }
}
