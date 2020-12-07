package fr.insalyon.mxyns.icrc.dna.case_list;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.File;

import fr.insalyon.mxyns.icrc.dna.Constants;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

/**
 * Data carried by a CaseItem in the CaseListFragment. Adapted to a View by CaseRecyclerViewAdapter.
 *
 * @see CaseRecyclerViewAdapter
 * @see CaseListFragment
 */
public class CaseItemContent {

    /**
     * Case file path
     */
    public final transient String path;

    /**
     * Status color
     */
    public final transient int color;

    /**
     * Json content
     */
    public final transient JsonObject json;
    /**
     * Case score used to find status color.
     */
    public final float score;
    /**
     * Named displayed in the list item. Stored as 'displayName' in the case file
     */
    public String displayName;

    /**
     * new CaseItemContent and determines case status color
     *
     * @param context required context to get color
     * @param path    file path
     * @param json
     */
    public CaseItemContent(Context context, String path, JsonObject json) {
        this.path = path;
        this.json = json;

        this.displayName = json.get("displayName").getAsString();
        this.score = json.get("score").getAsFloat();

        this.color = Constants.getStatusColor(context.getResources(), score);
    }

    /**
     * Makes a CaseItemContent from a case file
     *
     * @param context required context to get color resources in CaseItemContent constructor
     * @param file    case file object
     * @return CaseItemContent associated to 'file'
     */
    public static CaseItemContent fromFile(Context context, File file) {

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

    /**
     * Sets case display name and modifies it in the json file
     *
     * @param text new name
     * @return display name in json after modification attempt
     */
    public String setDisplayName(String text) {

        return this.displayName = FileUtils.alterProperty(json, path, "displayName", new JsonPrimitive(text)).getAsString();
    }
}