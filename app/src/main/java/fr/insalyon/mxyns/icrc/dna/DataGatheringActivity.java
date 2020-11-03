package fr.insalyon.mxyns.icrc.dna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import fr.insalyon.mxyns.icrc.dna.data_gathering.FormScreenAdapter;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputResult;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputTemplateFragment;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

public class DataGatheringActivity extends AppCompatActivity {

    private FormScreenAdapter formScreenAdapter;
    private String path;
    public static HashMap<String, JsonObject> data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("data-oncreate", "CREATE DATAGATHERING");

        path = getIntent().getStringExtra("load");
        if (path != null)
            data = loadDataFromFile(path);
        else
            data = new HashMap<>();

        Log.d("data-oncreate", "data = " + data);

        setContentView(R.layout.activity_data_gathering);
        formScreenAdapter = new FormScreenAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(formScreenAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.confirm);
        fab.setOnClickListener(view -> Snackbar.make(view, "Êtes-vous sûr ?", Snackbar.LENGTH_LONG)
                .setAction("Valider", e -> {

                    Intent intent = new Intent(DataGatheringActivity.this, ResultActivity.class);
                    intent.putExtra("all-values", gatherValues());
                    intent.putExtra("path", path);
                    startActivity(intent);

                }).show());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("data-oncreate", "resume datagathering");
        Log.d("data-oncreate", formScreenAdapter.inputNameToFragment.toString());
    }

    public HashMap<String, JsonObject> loadDataFromFile(String path) {

        JsonObject jsonData = FileUtils.loadJsonFromFile(path);
        ArrayList<String> jsonPaths = FileUtils.findAll("input", jsonData.getAsJsonObject("entries"));

        // tier_1_screen_3_option_4 => { raw: "true", count: "1", input:"tier_1_screen_3_option_4" }
        HashMap<String, JsonObject> data = new HashMap<>();

        JsonObject temp;
        for (String jPath : jsonPaths) {
            temp = FileUtils.getJsonFromPath("entries." + jPath, jsonData);
            data.put(temp.get("input").getAsString(), temp);
        }

        return data;
    }

    public HashMap<Integer, ArrayList<InputResult>> gatherValues() {

        HashMap<Integer, ArrayList<InputResult>> result = new HashMap<>();

        Log.d("to-result", "gatherValues: " + data);
        Log.d("to-result", "gatherValues: " + formScreenAdapter.inputNameToFragment);

        for (String input_name : data.keySet()) {

            Log.d("to-result", "======");
            data.get(input_name).addProperty("input", input_name);

            Log.d("to-result", "gatherValues: name=" + input_name);
            InputTemplateFragment inputFrag = formScreenAdapter.inputNameToFragment.get(input_name);
            if (inputFrag == null) {
                Log.d("to-result", "fragment not found, maybe removed ? value will be lost");
                continue;
            }

            Log.d("to-result", "gatherValues: fragment=" + inputFrag);
            int tier = inputFrag.owner.tier;
            Log.d("to-result", "gatherValues: tier=" + tier);
            if (!result.containsKey(tier))
                result.put(tier, new ArrayList<>());

            InputResult inputResult = formScreenAdapter.inputNameToFragment.get(input_name).getInputResult();
            inputResult.setJsonPath(getResources().getString(getResources().getIdentifier(inputResult.getInputName() + "_path", "string", getPackageName())));

            result.get(tier).add(inputResult);
        }

        return result;
    }
}