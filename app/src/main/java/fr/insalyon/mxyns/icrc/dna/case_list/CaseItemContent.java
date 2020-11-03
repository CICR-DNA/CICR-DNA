package fr.insalyon.mxyns.icrc.dna.case_list;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.io.FileNotFoundException;

import fr.insalyon.mxyns.icrc.dna.Constants;
import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

public class CaseItemContent {
    public final transient String path;
    public final transient int color;
    public final transient JsonObject json;

    public String displayName;
    public final float score;

    public CaseItemContent(Context context, String path, JsonObject json) {
        this.path = path;
        this.json = json;

        this.displayName = json.get("displayName").getAsString();
        this.score = json.get("score").getAsFloat();

        this.color = context.getResources().getColor(score >= Constants.getSecondThreshold(context.getResources()) ?
                            R.color.status_ok
                    : score >= Constants.getFirstThreshold(context.getResources()) ?
                            R.color.status_medium
                    : R.color.status_bad);
    }

    public static CaseItemContent fromFile(Context context, File file) throws FileNotFoundException {

        JsonObject json = FileUtils.loadJsonFromFile(file);
        Log.d("loading-json", file.getPath() + " => " + json.toString());
        return new CaseItemContent(context, file.getPath(), json);
    }

    @Override
    public String toString() {
        return displayName;
    }

    public int getColor() {

        return this.color;
    }

    public String setDisplayName(String text) {

        return this.displayName = FileUtils.alterProperty(json, path, "displayName", new JsonPrimitive(text)).getAsString();
    }
}