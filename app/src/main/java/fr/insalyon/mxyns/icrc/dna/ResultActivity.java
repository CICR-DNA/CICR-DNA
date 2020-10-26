package fr.insalyon.mxyns.icrc.dna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputResult;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton confirm_fab = findViewById(R.id.confirm);
        confirm_fab.setOnClickListener(view -> {

            Snackbar snackbar = Snackbar.make(view, "Enregistré ✓", Snackbar.LENGTH_LONG)
                    .setAction("FIN", e -> resetAppToInitialState());
            snackbar.show();
        });

        FloatingActionButton go_back_fab = findViewById(R.id.go_back);
        go_back_fab.setOnClickListener(view -> finish());

        doSomeCalculLol();
    }

    private void doSomeCalculLol() {

        Serializable data = getIntent().getSerializableExtra("all-values");
        if (data == null) {
            Toast.makeText(this, "No data to analyze", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<Integer, ArrayList<InputResult>> values = (HashMap<Integer, ArrayList<InputResult>>) data;
        float score = 0;

        for (Integer tier : values.keySet()) {
            Log.d("all-values", "Tier " + tier + " : ");
            for (InputResult result : values.get(tier))
                score += result.getScore();
        }

        Toast.makeText(this, "Score is " + score, Toast.LENGTH_LONG).show();
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