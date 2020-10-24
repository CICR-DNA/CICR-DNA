package fr.insalyon.mxyns.icrc.dna;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
                    .setAction("FIN", e -> startMainActivity());
            snackbar.show();
        });

        FloatingActionButton go_back_fab = findViewById(R.id.go_back);
        go_back_fab.setOnClickListener(view -> finish());
    }

    private void startMainActivity() {

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