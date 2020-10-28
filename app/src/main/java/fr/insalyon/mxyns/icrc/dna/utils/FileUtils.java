package fr.insalyon.mxyns.icrc.dna.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

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

    public static JsonObject jsonFromValues(HashMap<Integer, ArrayList<InputResult>> values, float score) {

        JsonObject obj = new JsonObject();
        obj.addProperty("displayName", new Date().toString());
        obj.addProperty("score", score);

        JsonObject entries = new JsonObject();
        JsonObject entry;
        for (Integer tier : values.keySet())
            for (InputResult result : values.get(tier)) {
                if (result.getJsonPath().length() == 0) {
                    Log.d("json-entry", "No path for " + result.getName());
                    continue;
                }

                entry = new JsonObject();
                entry.addProperty("input", result.getName());
                entry.addProperty("raw", result.getRaw().toString());
                entry.addProperty("count", result.getCount());
                Log.d("json-entry", entry.toString());
                addJsonEntry(entries, result.getJsonPath(), entry);
            }

        obj.add("entries", entries);

        return obj;
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
}