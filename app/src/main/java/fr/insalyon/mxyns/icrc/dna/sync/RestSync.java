package fr.insalyon.mxyns.icrc.dna.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.utils.RestPostFileAsyncTask;
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
    public void send(Context context, String filePath) {

        String url_key = context.getResources().getString(R.string.settings_default_restAPI_url_key);
        String url_str = PreferenceManager.getDefaultSharedPreferences(context).getString(url_key, null);
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString("auth_token", null);

        new RestPostFileAsyncTask(context).execute(url_str, token, filePath);
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

    public static boolean login(Context context) {

        Log.d("rest-api-login", "attempt to login");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources = context.getResources();

        String url_str = sharedPreferences.getString(resources.getString(R.string.settings_default_restAPI_url_key), null);
        String usr = sharedPreferences.getString(resources.getString(R.string.settings_default_restAPI_usr_key), null);
        String pwd = sharedPreferences.getString(resources.getString(R.string.settings_default_restAPI_pwd_key), null);

        if (url_str == null || usr == null || pwd == null) return false;

        url_str += (url_str.endsWith("/") ? "" : "/") + resources.getString(R.string.settings_default_restAPI_url_login_path);

        Log.d("rest-api-login", "url : " + url_str);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(resources.getInteger(R.integer.requests_timeout_ms), TimeUnit.MILLISECONDS)
                .build();

        RequestBody post_body = RequestBody.create(MediaType.parse("application/json"),
                resources.getString(R.string.settings_default_restAPI_body_login, usr, pwd));

        Request req = new Request.Builder()
                .url(url_str)
                .post(post_body)
                .build();

        Log.d("rest-api-login", "post_body : \n" + bodyToString(req));

        Response rep = syncRequest(context, httpClient, req);

        Log.d("rest-api-login", rep != null ? " received code : " + rep.code() : "received null");
        if (rep == null || rep.code() != 201) return false;

        String body_as_str = null;
        try {
            ResponseBody body = rep.body();

            if (body == null){
                Log.d("rest-api-login", "no response body");
                return false;
            }

            body_as_str = body.string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("rest-api-login", "response : " + body_as_str);

        JsonObject response = new JsonParser().parse(body_as_str).getAsJsonObject();
        sharedPreferences.edit()
                .putString("auth_token", response.getAsJsonPrimitive("token").getAsString())
                .putString("user_id", response.getAsJsonPrimitive("userId").getAsString())
                .apply();

        return true;
    }

    public static Response syncRequest(Context ctx, OkHttpClient httpClient, Request req) {

        if (req == null) return null;

        Response res;
        try {
            res = httpClient.newCall(req).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if ((res.code() == 401 || res.code() == 498) && login(ctx)) {
            String auth_token = PreferenceManager.getDefaultSharedPreferences(ctx).getString("auth_token", null);
            req = req.newBuilder().header("Authorization", "Bearer " + auth_token).build();
            return syncRequest(ctx, httpClient, req);
        }

        return res;
    }
}
