package fr.insalyon.mxyns.icrc.dna;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import fr.insalyon.mxyns.icrc.dna.data_gathering.SectionsPagerAdapter;

public class DataGatheringActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_gathering);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.confirm);
        fab.setOnClickListener(view -> Snackbar.make(view, "Êtes-vous sûr ?", Snackbar.LENGTH_LONG)
                .setAction("Valider", e -> {

                    startActivity(new Intent(DataGatheringActivity.this, ResultActivity.class));
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
}