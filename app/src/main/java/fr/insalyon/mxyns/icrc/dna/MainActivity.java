package fr.insalyon.mxyns.icrc.dna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import fr.insalyon.mxyns.icrc.dna.case_list.CaseListFragment;
import fr.insalyon.mxyns.icrc.dna.sync.Sync;

/**
 * Main Activity with a CaseList
 */
public class MainActivity extends AppCompatActivity {

    private boolean multiSelection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FloatingActionButton fab_confirm = findViewById(R.id.fab_confirm);
        fab_confirm.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DataGatheringActivity.class);
            startActivity(intent);
        });

        FloatingActionButton fab_sync = findViewById(R.id.fab_sync);
        fab_sync.setOnClickListener(view -> {
            Log.d("menu-file-sync", "Clicked");
            if (multiSelection) {
                CaseListFragment caseList = (CaseListFragment) getSupportFragmentManager().findFragmentById(R.id.case_list_fragment);
                ArrayList<String> paths = caseList.getSelectedPaths();
                Log.d("menu-file-sync", "Selected " + paths);

                Sync sync = null;
                if (paths.size() == 1)
                    sync = Sync.attemptFileSync(this, paths.get(0));
                else if (paths.size() > 0)
                    sync = Sync.attemptFileSync(this, paths);

                if (paths.size() != 0)
                    Sync.showSyncResultDialog(this, sync);
            }
            toggleMultiSelection();
        });

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sync.reInit(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //Handling Action Bar button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Back button behaviour
    @Override
    public void onBackPressed() {

        if (multiSelection) toggleMultiSelection();
        else moveTaskToBack(true); // move app to background
    }

    private void toggleMultiSelection() {
        CaseListFragment caseList = (CaseListFragment) getSupportFragmentManager().findFragmentById(R.id.case_list_fragment);
        if (caseList != null) {
            caseList.setMultiSelection(multiSelection = !multiSelection);
            FloatingActionButton fab_sync = findViewById(R.id.fab_sync);
            fab_sync.setImageDrawable(getResources().getDrawable(multiSelection ? R.drawable.ic_baseline_check_24 : R.drawable.ic_baseline_sync_24));
        }
    }
}