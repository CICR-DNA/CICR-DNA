package fr.insalyon.mxyns.icrc.dna.utils;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputResult;
import fr.insalyon.mxyns.icrc.dna.sync.EmailSync;
import fr.insalyon.mxyns.icrc.dna.sync.RestSync;
import fr.insalyon.mxyns.icrc.dna.sync.Sync;

public class FileUtils {

    // FIXME move to CaseListFragment ? depends on how it'll be used
    /**
     * Data synchronizers, used to send the cases data to any type of receiver, sorted in order of use.
     * If the first one fails the next one will be used until one is etc.
     * @see FileUtils#attemptFileSync
     */
    public static LinkedList<Sync> syncs = new LinkedList<>(Arrays.asList(new RestSync(), new EmailSync()));

    /**
     * Lists and sorts files in a dir (with API Level < 21 constraint)
     * @param path path to dir
     * @return sorted ArrayList of paths to files in the provided dir
     */
    public static ArrayList<File> listFiles(String path) {

        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        ArrayList<File> list = new ArrayList<>(Arrays.asList(dir.listFiles()));

        Collections.sort(list, (a, b) -> a.getPath().compareTo(b.getPath()));
        Collections.reverse(list);

        return list;
    }

    /**
     * Serializes and saves a JsonObject to a file
     * @param json json to save
     * @param path path of file
     */
    public static void saveJsonToFile(JsonObject json, String path) {

        try {
            Log.d("json-save", "out path : " + path);

            File file = new File(path);
            if (!file.exists()) {
                if (!file.getParentFile().exists())
                    Log.d("json-mkdirs", String.valueOf(file.getParentFile().mkdirs()));
                Log.d("json-mkfile", String.valueOf(file.createNewFile()));
            }

            FileWriter writer = new FileWriter(file);
            writer.write(json.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the element 'property' in 'json' to 'value' and writes the result to file at 'path'
     * @param json source object
     * @param path path to saved file
     * @param property key to alter in 'json'
     * @param value new value of 'property'
     * @return the altered property's JsonElement
     */
    public static JsonElement alterProperty(JsonObject json, String path, String property, JsonElement value) {

        json.add(property, value);

        try {
            File file = new File(path);
            FileWriter writer = new FileWriter(file);
            writer.write(json.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json.get(property);
    }

    /**
     * Makes a JsonObject from a map containing some inputs' result values.
     * Map : tier => list of inputs' results
     * @see InputResult
     *
     * uses FileUtils#addJsonEntry to insert
     * {
     *      "input" : InputResult.inputName,
     *      "raw" : InputResult.rawValue,
     *      "count" : InputResult.count
     *  }
     *  at InputResult.jsonPath in a JsonObject which is the returned result
     * @see FileUtils#addJsonEntry(JsonObject, String, JsonElement)
     * @param values map to convert to JsonObject
     * @return JsonObject representing the 'values' map
     */
    public static JsonObject jsonFromValues(HashMap<Integer, ArrayList<InputResult>> values) {

        JsonObject entries = new JsonObject();
        JsonObject entry;
        for (Integer tier : values.keySet())
            for (InputResult result : values.get(tier)) {
                if (result.getJsonPath().length() == 0) {
                    Log.d("json-entry", "No path for " + result.getInputName());
                    continue;
                }

                entry = new JsonObject();
                entry.addProperty("input", result.getInputName());
                entry.addProperty("raw", result.getRaw());
                entry.addProperty("count", result.getCount());
                Log.d("json-entry", entry.toString());
                addJsonEntry(entries, result.getJsonPath(), entry);
            }

        return entries;
    }

    /**
     * Inserts a JsonElement at a specified json path in a JsonObject
     * @param dest json object into which the element is inserted
     * @param jsonPath json path the element will have after insertion
     * @param value JsonElement to put at path 'jsonPath' in 'dest'
     */
    private static void addJsonEntry(JsonObject dest, String jsonPath, JsonElement value) {

        String[] shards = jsonPath.split("\\.");

        if (shards.length == 1) {
            dest.add(shards[0], value);
            return;
        }

        JsonObject root;
        if (dest.has(shards[0]))
            root = dest.getAsJsonObject(shards[0]);
        else {
            root = new JsonObject();
            dest.add(shards[0], root);
        }

        JsonObject prev = root;
        int i = 1;
        while (i < shards.length - 1) {

            JsonObject tmp;
            if (prev.has(shards[i]))
                tmp = prev.getAsJsonObject(shards[i]);
            else
                prev.add(shards[i], tmp = new JsonObject());

            prev = tmp;
            i++;
        }

        prev.add(shards[shards.length - 1], value);
    }

    /**
     * Generates an unique (in the directory given) file name
     * If the directory provided doesn't exist the file name will be : somePseudoRandomLongValue + ".json"
     * If it exists : "case-" + smallestUniqueInt(in this dir) + ".json"
     * @param root directory in which the file name will be unique
     * @return random and unique (in the directory given) file name
     */
    public static String nameFile(File root) {

        if (!root.exists())
            return (long) (System.currentTimeMillis() + System.nanoTime() * Math.random()) + ".json";

        File[] files = root.listFiles();
        HashMap<String, File> filesMap = new HashMap<>();
        for (File file : files) filesMap.put(file.getName(), file);

        int i = 0;
        while(filesMap.get("case-"+i+".json") != null)
            i++;

        return "case-"+i+".json";
    }

    /**
     * Loads a JsonObject from an input file.
     * @param path path to file
     * @return JsonObject described by the file
     */
    public static JsonObject loadJsonFromFile(String path) {

        if (path == null)
            return null;

        return loadJsonFromFile(new File(path));
    }
    /**
     * Loads a JsonObject from an input file.
     * @param file the file to read the json from
     * @return JsonObject described by the file
     */
    public static JsonObject loadJsonFromFile(File file) {

        if (!file.exists())
            return null;

        try {
            return new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Uses FileUtils#find to find the json paths of all JsonObjects with key equals(ignoring case) to 'tag'
     * @param tag key value to search for
     * @param data JsonObject to search in
     * @see FileUtils#find
     * @return ArrayList<String> containing all of the json paths to JsonObjects with matching key value
     */
    public static ArrayList<String> findAll(String tag, JsonObject data) {

        Log.d("find-json-tag", "start findAll(" + tag + ") in : \n " + data.toString());
        ArrayList<String> matches;
        find(tag, ".", data, "", matches = new ArrayList<>());
        Log.d("find-json-tag", "result => " + Arrays.toString(matches.toArray()));

        return matches;
    }

    /**
     * Recursively finds all entries with key 'tag' in 'data' and adds its json path to 'matches'
     * @param tag key to search for
     * @param sep json path separator
     * @param data current JsonObject being searched in
     * @param current json path of the current JsonObject being searched in
     * @param matches array of matches to fill
     */
    private static void find(String tag, String sep, JsonObject data, String current, ArrayList<String> matches) {

        Log.d("find-json-tag", "   => " + current);
        for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(tag)) {
                matches.add(current + tag);
                Log.d("find-json-tag", "       => " + current + tag + " = " + entry.getValue().getAsString());
            } else if (!entry.getValue().isJsonNull() && !entry.getValue().isJsonPrimitive()){
                find(tag, sep, entry.getValue().getAsJsonObject(), current + entry.getKey() + sep, matches);
            }
        }
    }

    /**
     * Finds the JsonObject located at json path in a JsonObject
     * @param path json path to follow
     * @param data root json object in which the search is done
     * @return JsonObject at 'path' in 'data' if any or null if not found
     */
    public static JsonObject getJsonFromPath(String path, JsonObject data) {

        String[] shards = path.split("\\.");

        JsonObject prev = data;
        for (int i = 0; i < shards.length - 1; ++i) {
            if (prev.has(shards[i]))
                prev = prev.getAsJsonObject(shards[i]);
            else return null;
        }

        if (prev.has(shards[shards.length - 1]))
            return prev;
        else return null;
    }

    /**
     * Deletes a file
     * @param path path to file
     * @return true if file was deleted
     */
    public static boolean deleteFile(String path) {

        File file = new File(path);
        return file.exists() && file.delete();
    }

    /**
     * Attempts to send a file using any synchronizer.
     * @param filePath path of the file to send
     * @return true if synchronization was successful
     * @see FileUtils#syncs
     */
    public static Sync attemptFileSync(String filePath) {

        for (Sync sync : syncs) {
            if (sync.send(filePath)) {
                return sync;
            }
        }

        return null;
    }
}