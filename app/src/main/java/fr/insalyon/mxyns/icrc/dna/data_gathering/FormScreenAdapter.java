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
 * one of the screens/tabs.
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

    /**
     * Screens definition
     * TODO read from json ?
     */
    private void registerFragments() {

        // Parents
        registerFormFragment(1, R.string.tier_1_screen_1_title,
                R.string.tier_1_screen_1_description,
                R.string.pedigree_help_template,
                R.drawable.parents,
                new InputDescription("checkbox", R.string.tier_1_screen_1_option_1, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_1_screen_1_option_2, mContext.getResources())
        );

        // Children
        registerFormFragment(1, R.string.tier_1_screen_2_title,
                R.string.tier_1_screen_2_description,
                R.string.pedigree_help_template,
                R.drawable.children,
                new InputDescription("integer", R.string.tier_1_screen_2_option_1, mContext.getResources()),
                new InputDescription("integer", R.string.tier_1_screen_2_option_2, mContext.getResources())
        );

        // Spouse
        registerFormFragment(1, R.string.tier_1_screen_3_title,
                R.string.tier_1_screen_3_description,
                R.string.pedigree_help_template,
                R.drawable.spouse,
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_1, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_2, mContext.getResources())
        );

        // Children from second spouse
        registerFormFragment(1, R.string.tier_1_screen_4_title,
                R.string.tier_1_screen_4_description,
                R.string.pedigree_help_template,
                R.drawable.children2,
                new InputDescription("integer", R.string.tier_1_screen_4_option_1, mContext.getResources()),
                new InputDescription("integer", R.string.tier_1_screen_4_option_2, mContext.getResources())
        );

        // Second spouse
        registerFormFragment(1, R.string.tier_1_screen_5_title,
                R.string.tier_1_screen_5_description,
                R.string.pedigree_help_template,
                R.drawable.spouse2,
                new InputDescription("checkbox", R.string.tier_1_screen_5_option_1, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_1_screen_5_option_2, mContext.getResources())
        );

        // Siblings
        registerFormFragment(1, R.string.tier_1_screen_6_title,
                R.string.tier_1_screen_6_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("integer", R.string.tier_1_screen_6_option_1, mContext.getResources()),
                new InputDescription("integer", R.string.tier_1_screen_6_option_2, mContext.getResources(), true,
                        () -> InputTemplateFragment.atLeastOneDependency("tier_1_screen_5_option_2")),
                new InputDescription("integer", R.string.tier_1_screen_6_option_3, mContext.getResources())
        );

        // Tier 2
        // Grandparents
        registerFormFragment(2, R.string.tier_2_screen_1_title,
                R.string.tier_2_screen_1_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("checkbox", R.string.tier_2_screen_1_option_1, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_2_screen_1_option_2, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_2_screen_1_option_3, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_2_screen_1_option_4, mContext.getResources())
        );

        // Grandchildren
        registerFormFragment(2, R.string.tier_2_screen_2_title,
                R.string.tier_2_screen_2_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("integer", R.string.tier_2_screen_2_option_1, mContext.getResources())
        );

        // Daughter/Son-in-law
        registerFormFragment(2, R.string.tier_2_screen_3_title,
                R.string.tier_2_screen_3_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("integer", R.string.tier_2_screen_3_option_1, mContext.getResources()),
                new InputDescription("integer", R.string.tier_2_screen_3_option_2, mContext.getResources())
        );

        // Cousins
        registerFormFragment(2, R.string.tier_2_screen_4_title,
                R.string.tier_2_screen_4_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("integer", R.string.tier_2_screen_4_option_1, mContext.getResources())
        );

        // Aunts and Uncles
        registerFormFragment(2, R.string.tier_2_screen_5_title,
                R.string.tier_2_screen_5_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("integer", R.string.tier_2_screen_5_option_1, mContext.getResources()),
                new InputDescription("integer", R.string.tier_2_screen_5_option_2, mContext.getResources()),
                new InputDescription("integer", R.string.tier_2_screen_5_option_3, mContext.getResources()),
                new InputDescription("integer", R.string.tier_2_screen_5_option_4, mContext.getResources())
        );
        
        // Nephews
        registerFormFragment(2, R.string.tier_2_screen_6_title,
                R.string.tier_2_screen_6_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("integer", R.string.tier_2_screen_6_option_1, mContext.getResources()),
                new InputDescription("integer", R.string.tier_2_screen_6_option_2, mContext.getResources())
        );

        // Siblings spouses
        registerFormFragment(2, R.string.tier_2_screen_7_title,
                R.string.tier_2_screen_7_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("integer", R.string.tier_2_screen_7_option_1, mContext.getResources()),
                new InputDescription("integer", R.string.tier_2_screen_7_option_2, mContext.getResources())
        );


        // Half-siblings
        registerFormFragment(2, R.string.tier_2_screen_8_title,
                R.string.tier_2_screen_8_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("integer", R.string.tier_2_screen_8_option_1, mContext.getResources()),
                new InputDescription("integer", R.string.tier_2_screen_8_option_2, mContext.getResources()),
                new InputDescription("integer", R.string.tier_2_screen_8_option_3, mContext.getResources()),
                new InputDescription("integer", R.string.tier_2_screen_8_option_4, mContext.getResources())
        );

        // Step-parents
        registerFormFragment(2, R.string.tier_2_screen_9_title,
                R.string.tier_2_screen_9_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription("checkbox", R.string.tier_2_screen_9_option_1, mContext.getResources()),
                new InputDescription("checkbox", R.string.tier_2_screen_9_option_2, mContext.getResources())
        );
    }

    /**
     * Registers a screen
     *
     * @param tier           input tier
     * @param titleId        title android resource id
     * @param descriptionId  description android resource id
     * @param pedigreeHelpId pedigree help text android resource id
     * @param imageId        image android resource id
     * @param inputs_desc    all of the screen's input descriptions
     */
    @SafeVarargs
    private final void registerFormFragment(
            int tier,
            @StringRes int titleId,
            @StringRes int descriptionId,
            @StringRes int pedigreeHelpId,
            @DrawableRes int imageId,
            InputDescription... inputs_desc) {

        tab_titles.add(titleId);
        FormScreenFragment formFragment = FormScreenFragment.newInstance(tier, titleId, descriptionId, pedigreeHelpId, imageId, inputs_desc);

        tabs.add(formFragment);

        for (InputTemplateFragment input : formFragment.inputFragments)
            inputNameToFragment.put(input.input_name, input);
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