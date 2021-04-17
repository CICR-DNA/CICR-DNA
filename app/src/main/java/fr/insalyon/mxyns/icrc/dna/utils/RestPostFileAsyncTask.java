package fr.insalyon.mxyns.icrc.dna.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.sync.RestSync;
import fr.insalyon.mxyns.icrc.dna.sync.Sync;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

// TODO check if zips are handled correctly
public class RestPostFileAsyncTask extends AsyncTask<String, Void, Boolean> {

    private final Context mContext;
    private OkHttpClient client;
    private Response response;

    public RestPostFileAsyncTask(Context context) {

        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        client = new OkHttpClient.Builder()
                .readTimeout(mContext.getResources().getInteger(R.integer.requests_timeout_ms), TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        String url_str = strings[0],
                token = strings[1],
                filePath = strings[2];

        if (url_str == null || token == null || filePath == null) return false;

        url_str += (url_str.endsWith("/") ? "" : "/") + mContext.getResources().getString(R.string.settings_default_restAPI_url_post_path);

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

        Request req;
        try {
            req = new Request.Builder()
                    .url(url_str)
                    .addHeader("Authorization", "Bearer " + token)
                    .post(post_body)
                    .build();

        } catch (IllegalArgumentException ignored) {
            return false;
        }

        response = RestSync.syncRequest(mContext, client, req);

        if (response == null) {
            super.cancel(true);
            return false;
        }

        Log.d("rest-sync", "response code : " + response.code());
            ResponseBody body = response.body();

            if (body == null) {
                Log.d("rest-sync", "no response body");
                super.cancel(true);
                return false;
            }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        // TODO response code must be handled better, will see when this part of the server is done
        boolean success = response != null && (response.code() == 200 || response.code() == 201);
        String error = null;
        ResponseBody body = response.body();
        if (!success && body != null) {
            try {
                String body_as_str = body.string();
                Log.d("server-error", body_as_str);
                JsonObject json = new JsonParser().parse(body_as_str).getAsJsonObject();
                error = json.get("error").getAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Sync.showSyncResultDialog(mContext, success, error);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel();
    }

    @Override
    protected void onCancelled(Boolean result) {
        super.onCancelled(result);
        cancel();
    }

    public void cancel() {

        Sync.showSyncResultDialog(mContext, false, null);
    }
}
