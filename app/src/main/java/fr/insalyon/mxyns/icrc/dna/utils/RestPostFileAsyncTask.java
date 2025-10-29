package fr.insalyon.mxyns.icrc.dna.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.sync.RestSync;
import fr.insalyon.mxyns.icrc.dna.sync.Sync;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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
    private String responseBody;

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

        Log.d("rest-sync", url_str + " " + token + " " + filePath);
        if (url_str == null || token == null || filePath == null) return false;

        url_str += (url_str.endsWith("/") ? "" : "/");

        RequestBody post_body;
        if (filePath.endsWith(".json")) {
            post_body = RequestBody.create(
                    MediaType.parse("application/json"),
                    FileUtils.loadJsonFromFile(filePath).toString()
            );
            url_str += mContext.getResources().getString(R.string.settings_default_restAPI_url_post_json_path);
        } else if (filePath.endsWith(".zip")) {
            File file = new File(filePath);
            post_body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("sampleFile", file.getName(),
                            RequestBody.create(MediaType.parse("application/zip"), file))
                    .build();
            url_str += mContext.getResources().getString(R.string.settings_default_restAPI_url_post_zip_path);
        } else {
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

        Log.d("rest-sync", req.toString());
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
        } else {
            try {
                this.responseBody = body.string();
            } catch (IOException e) {
                super.cancel(true);
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        // TODO response code must be handled better, will see when this part of the server is done
        boolean request_success = response != null && (response.code() == 200 || response.code() == 201);

        String error = "no error?";

        if (responseBody == null) {
            Log.d("server-error", "empty response body");
            error = "empty response body";
        }
        else if (!request_success) {
            Log.d("server-error", responseBody);
            JsonObject json;
            if ((json = JsonParser.parseString(responseBody).getAsJsonObject()) == null) {
                error =  "unexpected JSON responseBody: " + responseBody;
            } else {
                JsonElement el;
                error = "";
                if ((el = json.get("err_type")) != null) {
                    error += "err_type: " + el.getAsString() + "\n";
                }
                if ((el = json.get("error")) != null) {
                    error += "err: " + el.getAsString() + "\n";
                }
                if (error.isEmpty()) {
                    error = "unexpected server error response";
                }
            }
        } else {
            Log.d("rest-sync-success", responseBody);
        }

        Sync.showSyncResultDialog(mContext, request_success, error);
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
