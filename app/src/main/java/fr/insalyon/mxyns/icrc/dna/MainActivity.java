package fr.insalyon.mxyns.icrc.dna;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.confirm);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DataGatheringActivity.class);
            startActivity(intent);
        });
    }

    // Back button behaviour
    @Override
    public void onBackPressed() {

        moveTaskToBack(true); // move app to background
    }
}