package fr.insalyon.mxyns.icrc.dna.data_gathering;

import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.insalyon.mxyns.icrc.dna.DataGatheringActivity;
import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputDescription;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputTemplateFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class FormScreenAdapter extends FragmentPagerAdapter {

    @StringRes
    private final ArrayList<Integer> tab_titles = new ArrayList<>();
    public final HashMap<String, InputTemplateFragment> inputNameToFragment = new HashMap<>();
    public final ArrayList<Fragment> tabs = new ArrayList<>();

    private final DataGatheringActivity mContext;

    public FormScreenAdapter(DataGatheringActivity activity, FragmentManager fm) {
        super(fm);

        mContext = activity;

        Log.d("data-oncreate", "new instance form screen adapter");
        registerFragments();
        Log.d("data-oncreate", "after register ");
    }

    // TODO read from json
    private void registerFragments() {

        registerFormFragment(1, R.string.tier_1_screen_1_title,
                R.string.tier_1_screen_1_description,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("checkbox", R.string.tier_1_screen_1_option_1, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_1_screen_1_option_2, mContext.getResources()),
                new InputDescription("integer", R.string.tier_1_screen_1_option_3, mContext.getResources())

        );

        registerFormFragment(1, R.string.tier_1_screen_3_title,
                R.string.tier_1_screen_3_description,
                R.drawable.ic_baseline_double_check_24,
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_1, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_2, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_3, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_4, mContext.getResources())
        );

        registerFormFragment(1, R.string.tier_1_screen_5_title,
                R.string.tier_1_screen_5_description,
                R.drawable.ic_baseline_double_check_24,
                new InputDescription("integer", R.string.tier_1_screen_5_option_1, mContext.getResources()),
                new InputDescription("integer", R.string.tier_1_screen_5_option_2, mContext.getResources()),
                new InputDescription("integer", R.string.tier_1_screen_5_option_3, mContext.getResources()),
                new InputDescription("integer", R.string.tier_1_screen_5_option_4, mContext.getResources())
        );
    }

    @SafeVarargs
    private final void registerFormFragment(
            int tier,
            @StringRes int title,
            @StringRes int description,
            @DrawableRes int image,
            InputDescription... inputs_desc) {

        tab_titles.add(title);
        FormScreenFragment formFragment = FormScreenFragment.newInstance(tier, title, description, image, inputs_desc);

        tabs.add(formFragment);

        for (InputTemplateFragment input : formFragment.inputFragments)
            inputNameToFragment.put(input.name, input);
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

    public List<Fragment> getTabs() {
        
        return tabs;
    }
}