package fr.insalyon.mxyns.icrc.dna;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import fr.insalyon.mxyns.icrc.dna.data_gathering.DataGatheringOnScreenChangeListener;
import fr.insalyon.mxyns.icrc.dna.data_gathering.FormScreenAdapter;
import fr.insalyon.mxyns.icrc.dna.data_gathering.FormScreenFragment;
import fr.insalyon.mxyns.icrc.dna.data_gathering.TabLayout;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputResult;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputTemplateFragment;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

/**
 * Tabbed activity. Each tab is a "screen" with its input fragments whose inputs will be retrieved and passed to ResultActivity
 */
public class DataGatheringActivity extends AppCompatActivity {

    /**
     * Inputs' data modified in real time by the fragments
     */
    public static HashMap<String, JsonObject> data = null;

    /**
     * Tabs container and manager
     */
    private FormScreenAdapter formScreenAdapter;
    private ViewPager viewPager;

    private TabLayout tabs;
    /**
     * JSON File, null if new case. not null if case loaded from a file
     */
    private String path;

    private boolean changesDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("data-oncreate", "CREATE DATAGATHERING");

        setContentView(R.layout.activity_data_gathering);

        TextView title = findViewById(R.id.title);
        title.setText(FileUtils.nameCase(new File(getFilesDir(), getResources().getString(R.string.files_path)), this));

        // load case data from disk
        path = getIntent().getStringExtra("load");
        if (path != null) {
            data = loadDataFromFile(path);
            JsonObject obj = FileUtils.loadJsonFromFile(path);
            if (obj.has("displayName"))
                title.setText(obj.get("displayName").getAsString());
            setTitle(R.string.title_activity_data_gathering_edit);
        } else {
            setTitle(R.string.title_activity_data_gathering_new);
            data = new HashMap<>();
        }

        Log.d("data-oncreate", "data = " + data);

        formScreenAdapter = new FormScreenAdapter(this, getSupportFragmentManager());

        // setup swipeable views
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(formScreenAdapter);

        tabs = findViewById(R.id.tabs);
        tabs.init(formScreenAdapter, viewPager);

        // setup swipe buttons behaviour
        ImageButton left_swiper = findViewById(R.id.datagathering_left_chevron);
        left_swiper.setOnClickListener(e -> viewPager.setCurrentItem((viewPager.getCurrentItem() - 1 + formScreenAdapter.getCount()) % formScreenAdapter.getCount()));
        left_swiper.setOnLongClickListener(e -> {
            viewPager.setCurrentItem(0);
            return true;
        });
        ImageButton right_swiper = findViewById(R.id.datagathering_right_chevron);
        right_swiper.setOnClickListener(e -> viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % formScreenAdapter.getCount()));
        right_swiper.setOnLongClickListener(e -> {
            viewPager.setCurrentItem(formScreenAdapter.getCount() - 1);
            return true;
        });

        DataGatheringOnScreenChangeListener longAssName;
        viewPager.addOnPageChangeListener(longAssName = new DataGatheringOnScreenChangeListener(findViewById(R.id.datagathering_page_index), left_swiper, right_swiper, formScreenAdapter, this));
        longAssName.onPageSelected(0);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.datagathering_appbar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Handling Action Bar button click
    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        // Handle item selection
        boolean result = super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_validate_case) {
            validateCase();
        } else if (item.getItemId() == R.id.action_help) {
            openPedigreeHelpDialog();
        }

        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("data-oncreate", "resume datagathering");
        Log.d("data-oncreate", formScreenAdapter.inputNameToFragment.toString());
    }

    // Back button behaviour
    @Override
    public void onBackPressed() {

        if (changesDone)
            new AlertDialog.Builder(this)
                    .setTitle(R.string.datagathering_exit_without_saving_title)
                    .setMessage(R.string.datagathering_exit_without_saving_msg)

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DataGatheringActivity.super.onBackPressed();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        else
            super.onBackPressed();
    }

    /**
     * Shows a Snackbar popup that allows the user to pass the gathered data to the next (ResultActivity) screen
     */
    private void validateCase() {

        Snackbar.make(findViewById(R.id.action_validate_case), R.string.datagathering_fab_u_sure, Snackbar.LENGTH_LONG)
                .setAction(R.string.datagathering_fab_accept, e -> {

                    Intent intent = new Intent(DataGatheringActivity.this, ResultActivity.class);
                    intent.putExtra("all-values", gatherValues());
                    intent.putExtra("path", path);
                    startActivity(intent);

                }).show();
    }

    /**
     * Opens the help dialog
     */
    private void openPedigreeHelpDialog() {

        Fragment frag = formScreenAdapter.tabs.get(viewPager.getCurrentItem());
        FormScreenFragment formScreenFragment = (FormScreenFragment) frag;
        Integer pedigreeHelpId = formScreenFragment.getPedigreeHelpId();
        if (pedigreeHelpId == null) {
            pedigreeHelpId = R.string.template_text;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.datagathering_pedigree_help_dialog_title)
                .setMessage(pedigreeHelpId)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_baseline_help_outline_24)
                .show();
    }

    /**
     * Loads a case data from a JSON File, finds all inputs' values in the json.
     *
     * @param path file path
     * @return map : input name(eg: tier_1_screen_3_option_4) => JsonObject (eg { raw: "true", count: "1", input:"tier_1_screen_3_option_4" })
     */
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

    /**
     * Gathers inputs' values from all of the InputFragments (stored in 'data')
     *
     * @return map : tier => list of InputResult
     * @see this#data
     */
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

    public void setInputValue(String input_name, JsonObject result, boolean notify) {

        DataGatheringActivity.data.put(input_name, result);
        if (notify)
            changesDone = true;
    }
}