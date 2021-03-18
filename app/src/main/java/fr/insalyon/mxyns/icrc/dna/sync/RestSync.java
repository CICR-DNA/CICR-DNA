package fr.insalyon.mxyns.icrc.dna.sync;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

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

        String url_key = context.getResources().getString(R.string.settings_default_restAPI_url_key);
        String url_str = PreferenceManager.getDefaultSharedPreferences(context).getString(url_key, null);

        String token = PreferenceManager.getDefaultSharedPreferences(context).getString("auth_token", null);
        url_str += (url_str.endsWith("/") ? "" : "/") + context.getResources().getString(R.string.settings_default_restAPI_url_post_path);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        // TODO check if zips are handled correctly
        RequestBody post_body;
        if (filePath.endsWith(".json"))
            post_body = RequestBody.create(
                    MediaType.parse("application/json"),
                    FileUtils.loadJsonFromFile(filePath).toString()
            );
        else if (filePath.endsWith(".zip"))
            post_body = RequestBody.create(
                    MediaType.parse("application/zip"),
                    new File(filePath)
            );
        else {
            Log.d("rest-sync", "file format not handled : " + new File(filePath).getName());
            return false;
        }

        Request req = new Request.Builder()
                .url(url_str)
                .addHeader("Authorization", "Bearer " + token)
                .post(post_body)
                .build();

        Response rep = syncRequest(httpClient, req);

        if (rep == null) return false;

        Log.d("rest-sync", "response code : " + rep.code());
        try {
            ResponseBody body = rep.body();

            Log.d("rest-sync", "no response body");
            if (body == null) return false;

            String body_as_str = body.string();
            Log.d("rest-sync", "response body : " + body_as_str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO rep code must be handled better, will see when this part of the server is done
        return rep.code() == 200;
    }

    private static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static boolean login(Context context, String url_str, String username, String password) {

        password = "123456789";

        Log.d("rest-api-login", "attempt to login");
        Resources resources = context.getResources();
        url_str += (url_str.endsWith("/") ? "" : "/") + resources.getString(R.string.settings_default_restAPI_url_login_path);


        Log.d("rest-api-login", "url : " + url_str);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        RequestBody post_body = RequestBody.create(MediaType.parse("application/json"),
                resources.getString(R.string.settings_default_restAPI_body_login, username, password));

        Request req = new Request.Builder()
                .url(url_str)
                .post(post_body)
                .build();

        Log.d("rest-api-login", "post_body : \n" + bodyToString(req));

        Response rep = syncRequest(httpClient, req);

        Log.d("rest-api-login", rep != null ? " received code : " + rep.code() : "received null");
        if (!(rep != null && rep.code() == 201)) return false;

        String body_as_str = null;
        try {
            ResponseBody body = rep.body();

            Log.d("rest-api-login", "no response body");
            if (body == null) return false;

            body_as_str = body.string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("rest-api-login", "response : " + body_as_str);

        JsonObject response = new JsonParser().parse(body_as_str).getAsJsonObject();
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString("auth_token", response.getAsJsonPrimitive("token").getAsString())
                .putString("user_id", response.getAsJsonPrimitive("userId").getAsString())
                .apply();

        return true;
    }

    public static Response syncRequest(OkHttpClient httpClient, Request req) {

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
            return null;
        }

        return res.get();
    }
}
