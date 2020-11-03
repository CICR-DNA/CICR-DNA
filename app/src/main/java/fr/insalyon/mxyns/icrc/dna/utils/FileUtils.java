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
import java.util.Map;

import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputResult;

public class FileUtils {


    public static ArrayList<File> listFiles(String path) {

        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        ArrayList<File> list = new ArrayList<>(Arrays.asList(dir.listFiles()));

        Collections.sort(list, (a, b) -> a.getPath().compareTo(b.getPath()));
        Collections.reverse(list);

        return list;
    }

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

    public static JsonObject loadJsonFromFile(String path) {

        if (path == null)
            return null;

        return loadJsonFromFile(new File(path));
    }
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

    public static ArrayList<String> findAll(String tag, JsonObject data) {

        Log.d("find-json-tag", "start findAll(" + tag + ") in : \n " + data.toString());
        ArrayList<String> matches;
        find(tag, ".", data, "", matches = new ArrayList<>());
        Log.d("find-json-tag", "result => " + Arrays.toString(matches.toArray()));

        return matches;
    }

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
}