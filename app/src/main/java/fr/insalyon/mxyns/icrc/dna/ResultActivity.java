package fr.insalyon.mxyns.icrc.dna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputResult;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

public class ResultActivity extends AppCompatActivity {

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

        FloatingActionButton confirm_fab = findViewById(R.id.confirm);
        confirm_fab.setOnClickListener(view -> {

            Snackbar snackbar = Snackbar.make(view, "Enregistrer et quitter ? ", Snackbar.LENGTH_LONG)
                    .setAction("FIN", e -> saveAndExit(values, path));
            snackbar.show();
        });

        FloatingActionButton go_back_fab = findViewById(R.id.go_back);
        go_back_fab.setOnClickListener(view -> finish());

        score = evaluateScore(values);

        findViewById(R.id.result_content).setBackgroundColor(Constants.getStatusColor(getResources(), score));

        ((TextView) findViewById(R.id.result_text)).setText(String.valueOf( (int)(10*score) / 10.0 ));
    }

    private float evaluateScore(HashMap<Integer, ArrayList<InputResult>> values) {

        float score = 0;

        TypedValue unit_score_holder = new TypedValue();

        // First pass needed for bonuses
        short grandparents = 0, children = 0, niecesAndNephews = 0;
        for (Integer tier : values.keySet())
            for (InputResult result : values.get(tier)) {

                if (result.getJsonPath().toLowerCase().startsWith("grandparents"))
                    grandparents += result.getCount();

                if (result.getJsonPath().toLowerCase().startsWith("children"))
                    children += result.getCount();

                if (result.getJsonPath().toLowerCase().startsWith("auntsanduncles.niecesandnephews"))
                    niecesAndNephews += result.getCount();
            }


        // Dumb sum
        for (Integer tier : values.keySet()) {
            Log.d("all-values", "Tier " + tier + " : ");
            for (InputResult result : values.get(tier))
                try {

                    if (children < 1 && result.getJsonPath().toLowerCase().startsWith("spouses"))
                        continue;

                    // FIXME is in "Scoring System" slide but not mentioned in the rest of the slideshow
                    if (niecesAndNephews < 1 && result.getJsonPath().toLowerCase().startsWith("siblings.spouses"))
                        continue;

                        getResources().getValue(getResources().getIdentifier(result.getInputName(), "dimen", getPackageName()), unit_score_holder, true);
                    score += unit_score_holder.getFloat() * result.getCount();

                } catch (Exception e) {
                    Log.d("result-calc", "error while looking for unit_score of Input " + result.getInputName());
                }
        }

        if (grandparents >= 4)
            score += 4;

        Toast.makeText(this, "Score is " + score, Toast.LENGTH_LONG).show();

        return score;
    }

    private void saveAndExit(HashMap<Integer, ArrayList<InputResult>> values, String path) {

        JsonObject obj = path != null ? FileUtils.loadJsonFromFile(path) : new JsonObject();
        if (!obj.has("displayName"))
            obj.addProperty("displayName", new Date().toString());

        obj.addProperty("score", score);
        obj.add("entries", FileUtils.jsonFromValues(values));

        File root = new File(getFilesDir(), getResources().getString(R.string.files_path));
        FileUtils.saveJsonToFile(obj, path != null ? path : root.getPath() + "/" + FileUtils.nameFile(root));

        Log.d("result-json", obj.toString());
        resetAppToInitialState();
    }

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
}