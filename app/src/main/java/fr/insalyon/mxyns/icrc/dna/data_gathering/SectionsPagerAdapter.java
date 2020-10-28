package fr.insalyon.mxyns.icrc.dna.data_gathering;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputDescription;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private final ArrayList<Integer> tab_titles = new ArrayList<>();
    private final ArrayList<Fragment> tabs = new ArrayList<>();

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

        registerFragments();
    }

    // TODO read from json
    private void registerFragments() {

        registerFormFragment(1, R.string.tier_1_screen_1_title,
                R.string.tier_1_screen_1_description,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("checkbox", R.string.tier_1_screen_1_option_1),
                new InputDescription("checkbox", R.string.tier_1_screen_1_option_2),
                new InputDescription("integer", R.string.tier_1_screen_1_option_2)

        );

        registerFormFragment(1, R.string.tier_1_screen_3_title,
                R.string.tier_1_screen_3_description,
                R.drawable.ic_baseline_double_check_24,
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_1),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_2),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_3),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_4)
        );

        registerFormFragment(1, R.string.tier_1_screen_3_title,
                R.string.tier_1_screen_3_description,
                R.drawable.ic_baseline_double_check_24,
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_1),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_2),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_3),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_4));

        registerFormFragment(1, R.string.tier_1_screen_3_title,
                R.string.tier_1_screen_3_description,
                R.drawable.ic_baseline_double_check_24,
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_1),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_2),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_3),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_4));

        registerFormFragment(1, R.string.tier_1_screen_3_title,
                R.string.tier_1_screen_3_description,
                R.drawable.ic_baseline_double_check_24,
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_1),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_2),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_3),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_4));
    }

    private void registerFormFragment(
            int tier,
            @StringRes int title,
            @StringRes int description,
            @DrawableRes int image,
            InputDescription... inputs_desc) {

        tab_titles.add(title);
        tabs.add(FormScreenFragment.newInstance(tier, title, description, image, inputs_desc));
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(tab_titles.get(position));
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    public ArrayList<Fragment> getTabs() {
        return tabs;
    }
}