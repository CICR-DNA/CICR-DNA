package fr.insalyon.mxyns.icrc.dna.data_gathering;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

public class TabLayout extends com.google.android.material.tabs.TabLayout {

    private FormScreenAdapter adapter;
    private ViewPager viewPager;
    private boolean wannaFastMove = true;

    public TabLayout(@NonNull @NotNull Context context) {
        super(context);
    }

    public TabLayout(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TabLayout(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(FormScreenAdapter adapter, ViewPager viewPager) {
        this.adapter = adapter;
        this.viewPager = viewPager;

        TreeMap<Integer, Integer> firstScreens = makeTabs();

        addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {

                if (wannaFastMove) // is a click
                    viewPager.setCurrentItem(firstScreens.get(tab.getPosition() + 1));

                tab.select();
                wannaFastMove = true;
            }

            @Override
            public void onTabUnselected(Tab tab) { }

            @Override
            public void onTabReselected(Tab tab) {

                if (wannaFastMove)
                    viewPager.setCurrentItem(firstScreens.get(tab.getPosition() + 1));
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                FormScreenFragment frag = fragmentToFormFragment(adapter.tabs.get(position));
                if (frag == null) return;

                if (frag.tier != getSelectedTabPosition() + 1) {
                    TabLayout.this.wannaFastMove = false;
                    selectTab(getTabAt(frag.tier - 1), false);
                }
            }
        });
    }

    private TreeMap<Integer, Integer> makeTabs() {

        TreeMap<Integer, Integer> firstScreenOfTier = new TreeMap<>();
        for (int i = 0; i < adapter.tabs.size(); i++)
            if (adapter.tabs.get(i) instanceof FormScreenFragment) {
                FormScreenFragment formFragment = (FormScreenFragment) adapter.tabs.get(i);
                if (firstScreenOfTier.containsKey(formFragment.tier)) continue;
                firstScreenOfTier.put(formFragment.tier, i);
            }

        for (Integer tier : firstScreenOfTier.keySet()) {
            Tab tab = newTab();
            tab.setText("Tier " + tier);
            Log.d("tabs", "Tier " + tier);
            addTab(tab, tier - 1, false);
        }
        if (getChildCount() > 0)
            getTabAt(0).select();

        return firstScreenOfTier;
    }

    private FormScreenFragment fragmentToFormFragment(Fragment frag) {

        return (frag instanceof FormScreenFragment) ? (FormScreenFragment) frag : null;
    }

}
