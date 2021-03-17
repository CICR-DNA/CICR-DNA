package fr.insalyon.mxyns.icrc.dna;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputResult;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

/**
 * Activity that displays the result of the input given by the user in the DataGatheringActivity
 */
public class ResultActivity extends AppCompatActivity {

    /**
     * score evaluated in evaluateScore
     *
     * @see this#evaluateScore(HashMap)
     */
    private float score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // get data from intent
        Serializable data = getIntent().getSerializableExtra("all-values");
        if (data == null) {
            Toast.makeText(this, "No data to analyze", Toast.LENGTH_LONG).show();
            return;
        }

        String path = getIntent().getStringExtra("path");
        Log.d("result-path", "path is " + path + " so it means i'm in " + (path == null ? "create" : "edit") + " mode");

        HashMap<Integer, ArrayList<InputResult>> values = (HashMap<Integer, ArrayList<InputResult>>) data;

        FloatingActionButton confirm_fab = findViewById(R.id.fab_confirm);
        confirm_fab.setOnClickListener(view -> {

            Snackbar snackbar = Snackbar.make(view, R.string.results_fab_save_and_quit, Snackbar.LENGTH_LONG)
                    .setAction(R.string.results_fab_finish, e -> saveAndExit(values, path));
            snackbar.show();
        });

        FloatingActionButton go_back_fab = findViewById(R.id.go_back);
        go_back_fab.setOnClickListener(view -> finish());

        score = evaluateScore(values);

        // update UI colors & text
        findViewById(R.id.result_content).setBackgroundColor(Constants.getStatusColor(getResources(), score));
        ((TextView) findViewById(R.id.results_status_label)).setText(Constants.getStatusLabel(getResources(), score));
        ((TextView) findViewById(R.id.results_status_info)).setText(Constants.getStatusInfo(getResources(), score));

        Log.d("results-summary", "fillSummary: b4");
        fillSummary(values, findViewById(R.id.results_summary_layout));
    }

    private void fillSummary(HashMap<Integer, ArrayList<InputResult>> data, LinearLayout summaryLayout) {

        List<ArrayList<InputResult>> sortedData = sortIndexedMap(data, new ArrayList<>());
        Resources res = getResources();
        Context context = summaryLayout.getContext();
        ArrayList<TextView> tierLines = new ArrayList<>();
        for (int tier = 1; tier <= sortedData.size(); tier++) {
            String tierText = String.format(res.getString(R.string.results_summary_tier_item), tier);

            for (InputResult result : sortedData.get(tier - 1)) {
                if (result.getCount() > 0) {
                    String resultText = String.format(res.getString(R.string.results_summary_input_item), result.getDisplayName(), result.getCount());
                    tierLines.add(makeSummaryLine(resultText, 2, context));
                }
            }

            if (tierLines.size() > 0) {
                summaryLayout.addView(makeSummaryLine(tierText, 1, context));

                for (TextView tierLine : tierLines)
                    summaryLayout.addView(tierLine);

                tierLines.clear();
            }
        }
    }
    public TextView makeSummaryLine(String text, int numberOfTabs, Context context) {

        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding((int) (numberOfTabs * getResources().getDimension(R.dimen.activity_horizontal_margin)), 0, 0, 0);

        return textView;
    }

    /**
     * Fills a list containing the values of a map sorted by keys, assuming that said keys are integers in range [1; map.size()]
     * Using the toFill list as a parameter to let the choice of List implementation open
     *
     * @param data map to sort by key values
     * @param toFill List to fill
     * @return toFill List filled with data Map's data sorted by key values
     */
    private <T> List<T> sortIndexedMap(HashMap<Integer, T> data, List<T> toFill) {

        int i = 1;
        T current;
        while ((current = data.get(i++)) != null)
            toFill.add(current);

        return toFill;
    }

    /**
     * Math to evaluate "DNA score" from user inputs
     *
     * @param values map : tier => list of InputResult in this tier
     * @return score
     */
    private float evaluateScore(HashMap<Integer, ArrayList<InputResult>> values) {

        float score = 0;

        // used to get score corresponding to an input
        TypedValue unit_score_holder = new TypedValue();

        // First pass needed for bonuses
        short grandparents = 0, niecesAndNephews = 0, grandChildren = 0, halfSiblings = 0;
        short[] children = new short[2]; // 2 maximum at the moment
        for (Integer tier : values.keySet())
            for (InputResult result : values.get(tier)) {

                String lowerJsonPath = result.getJsonPath().toLowerCase();
                if (lowerJsonPath.startsWith("grandparents"))
                    grandparents += result.getCount();

                if (lowerJsonPath.startsWith("children"))
                    children[extractMarriageIndex(lowerJsonPath) - 1] += result.getCount();

                if (lowerJsonPath.startsWith("siblings.children"))
                    niecesAndNephews += result.getCount();

                if (lowerJsonPath.startsWith("children.children"))
                    grandChildren += result.getCount();

                if (lowerJsonPath.matches("^siblings\\.[mp]aternal\\.half.+$"))
                    halfSiblings += result.getCount();
            }

        Log.d("results-counts", "grandparents="+grandparents);
        Log.d("results-counts", "niecesAndNephews="+niecesAndNephews);
        Log.d("results-counts", "grandChildren="+grandChildren);
        Log.d("results-counts", "halfSiblings="+halfSiblings);

        // Dumb sum
        for (Integer tier : values.keySet()) {
            Log.d("all-values", "Tier " + tier + " : ");
            for (InputResult result : values.get(tier))
                try {
                    String lowercaseJsonPath = result.getJsonPath().toLowerCase();

                    // TODO those multiple ifs could be changed to a list of predicates to check
                    if (lowercaseJsonPath.startsWith("spouses") && children[extractMarriageIndex(lowercaseJsonPath) - 1] < 1)
                        continue;

                    if (niecesAndNephews < 1 && lowercaseJsonPath.startsWith("siblings.spouses."))
                        continue;

                    if (grandChildren < 1 && lowercaseJsonPath.startsWith("children.spouses."))
                        continue;

                    if (halfSiblings < 1 && lowercaseJsonPath.startsWith("stepparents."))
                        continue;

                    // load unit score of an input
                    getResources().getValue(getResources().getIdentifier(result.getInputName(), "dimen", getPackageName()), unit_score_holder, true);
                    score += unit_score_holder.getFloat() * result.getCount();

                } catch (Exception e) {
                    Log.d("result-calc", "error while looking for unit_score of Input " + result.getInputName());
                }
        }

        if (grandparents >= 4)
            score += 4;

        // toast popup score value
        Toast.makeText(this, String.format(getResources().getString(R.string.results_score_toast_text), score), Toast.LENGTH_LONG).show();

        return score;
    }

    /**
     * Saves inputs' results to a json file
     *
     * @param values user inputs
     * @param path   json output file : filesDir + R.string.files_path + / + case-x.json
     */
    private void saveAndExit(HashMap<Integer, ArrayList<InputResult>> values, String path) {

        JsonObject obj = path != null ? FileUtils.loadJsonFromFile(path) : new JsonObject();
        if (!obj.has("displayName"))
            obj.addProperty("displayName", FileUtils.nameCase(new File(getFilesDir(), getResources().getString(R.string.files_path)), this));

        obj.addProperty("version", getResources().getString(R.string.json_version));
        obj.addProperty("score", score);
        obj.add("entries", FileUtils.jsonFromValues(values));

        File root = new File(getFilesDir(), getResources().getString(R.string.files_path));
        FileUtils.saveJsonToFile(obj, path != null ? path : root.getPath() + "/" + FileUtils.nameCaseFile(root));

        Log.d("result-json", obj.toString());
        resetAppToInitialState();
    }

    /**
     * Reset app state and go back to MainActivity
     */
    private void resetAppToInitialState() {

        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP // Clear back stack, MainActivity included
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear task (destroy every activity)
                        | Intent.FLAG_ACTIVITY_NEW_TASK // Start new task
        );

        startActivity(intent);
        finish();
    }

    /**
     *
     * @param jsonPath
     * @return
     */
    private short extractMarriageIndex(String jsonPath) {
        String[] shards = jsonPath.split("\\.");
        String lastMember = shards[shards.length - 1];
        short parentId = 1;
        try {
            // if no number at the end then children are from 1st marriage
            parentId = Short.parseShort(String.valueOf(lastMember.charAt(lastMember.length() - 1)));
        } catch (NumberFormatException ignored) {}

        return parentId;
    }
}