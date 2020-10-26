package fr.insalyon.mxyns.icrc.dna;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;

import fr.insalyon.mxyns.icrc.dna.data_gathering.FormScreenFragment;
import fr.insalyon.mxyns.icrc.dna.data_gathering.SectionsPagerAdapter;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputResult;

public class DataGatheringActivity extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_gathering);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.confirm);
        fab.setOnClickListener(view -> Snackbar.make(view, "Êtes-vous sûr ?", Snackbar.LENGTH_LONG)
                .setAction("Valider", e -> {

                    Intent intent = new Intent(DataGatheringActivity.this, ResultActivity.class);
                    intent.putExtra("all-values", getAllValues());
                    startActivity(intent);

                }).show());
        fab.hide();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == tabs.getTabCount() - 1) {
                    fab.show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                if (tab.getPosition() == tabs.getTabCount() - 1) {
                    fab.hide();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // Back button behaviour
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (intent.getBooleanExtra("plsreset", false)) {
            recreate();
        }
        super.onNewIntent(intent);
    }

    public HashMap<Integer, ArrayList<InputResult>> getAllValues() {

        HashMap<Integer, ArrayList<InputResult>> result = new HashMap<>();

        for (Fragment tab : sectionsPagerAdapter.getTabs()) {
            if (tab instanceof FormScreenFragment) { // if it's a form fragment it contains values to retrieve

                int tier = ((FormScreenFragment) tab).getViewModel().getTier().getValue();
                if (!result.containsKey(tier))
                    result.put(tier, ((FormScreenFragment) tab).getValues());
                else
                    result.get(tier).addAll(((FormScreenFragment) tab).getValues());
            }
        }

        return result;
    }
}