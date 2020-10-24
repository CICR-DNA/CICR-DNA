package fr.insalyon.mxyns.icrc.dna.data_gathering;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.data_gathering.TierOneFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final ArrayList<Integer> TAB_TITLES = new ArrayList<>();
    private static final ArrayList<TierOneFragment> TABS = new ArrayList<>();

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

        registerTierOneFragments();
    }

    private void registerTierOneFragments() {

        registerTierOneFragment(R.string.tier_one_screen_1_title,
                R.string.tier_one_screen_1_description,
                R.drawable.ic_baseline_crop_square_24,
                R.string.tier_one_screen_1_option_1,
                R.string.tier_one_screen_1_option_2);

        registerTierOneFragment(R.string.tier_one_screen_3_title,
                R.string.tier_one_screen_3_description,
                R.drawable.ic_baseline_double_check_24,
                R.string.tier_one_screen_3_option_1,
                R.string.tier_one_screen_3_option_2);
    }

    private void registerTierOneFragment(
            @StringRes int title,
            @StringRes int description,
            @DrawableRes int image,
            @StringRes int option1,
            @StringRes int option2) {

        TAB_TITLES.add(title);

        TierOneFragment fragment = TierOneFragment.newInstance(TABS.size(), title, description, image, option1, option2);
        TABS.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return TABS.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES.get(position));
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return TABS.size();
    }
}