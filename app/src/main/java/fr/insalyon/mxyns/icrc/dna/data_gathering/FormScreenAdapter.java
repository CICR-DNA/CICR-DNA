package fr.insalyon.mxyns.icrc.dna.data_gathering;

import android.content.res.Resources;
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
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputDescription.InputType;
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

        Resources r = mContext.getResources();

        registerFormFragment(0, R.string.tier_0_screen_0_title,
                R.string.tier_0_screen_0_description,
                R.string.pedigree_help_template,
                R.drawable.ic_baseline_crop_square_24,
                new InputDescription(InputType.StringListForMap, R.string.tier_0_screen_0_option_1, r)
        );

        // Missing person
        registerFormFragment(1, R.string.tier_0_screen_1_title,
                R.string.tier_0_screen_1_description,
                R.string.pedigree_help_template,
                R.drawable.sex,
                new InputDescription(InputType.StringList, R.string.tier_0_screen_1_option_1, r)
        );

        // Parents
        registerFormFragment(1, R.string.tier_1_screen_1_title,
                R.string.tier_1_screen_1_description,
                R.string.tier_1_screen_1_pedigreeHelp,
                R.drawable.parents,
                new InputDescription(InputType.Checkbox, R.string.tier_1_screen_1_option_1, r),
                new InputDescription(InputType.Checkbox, R.string.tier_1_screen_1_option_2, r)
        );

        // Children
        registerFormFragment(1, R.string.tier_1_screen_2_title,
                R.string.tier_1_screen_2_description,
                R.string.tier_1_screen_2_pedigreeHelp,
                R.drawable.children,
                new InputDescription(InputType.Spinner, R.string.tier_1_screen_2_option_1, r),
                new InputDescription(InputType.Spinner, R.string.tier_1_screen_2_option_2, r)
        );

        // Spouse
        registerFormFragment(1, R.string.tier_1_screen_3_title,
                R.string.tier_1_screen_3_description,
                R.string.tier_1_screen_3_pedigreeHelp,
                R.drawable.spouse,
                new InputDescription(InputType.Checkbox, R.string.tier_1_screen_3_option_1, r,
                        () -> InputTemplateFragment.atLeastOneDependency(r, R.string.tier_1_screen_2_option_1, R.string.tier_1_screen_2_option_2)),
                new InputDescription(InputType.Checkbox, R.string.tier_1_screen_3_option_2, r,
                        () -> InputTemplateFragment.atLeastOneDependency(r, R.string.tier_1_screen_2_option_1, R.string.tier_1_screen_2_option_2))
        );

        // Children from second spouse
        registerFormFragment(1, R.string.tier_1_screen_4_title,
                R.string.tier_1_screen_4_description,
                R.string.tier_1_screen_4_pedigreeHelp,
                R.drawable.children_spouse2,
                new InputDescription(InputType.Spinner, R.string.tier_1_screen_4_option_1, r),
                new InputDescription(InputType.Spinner, R.string.tier_1_screen_4_option_2, r)
        );

        // Second spouse
        registerFormFragment(1, R.string.tier_1_screen_5_title,
                R.string.tier_1_screen_5_description,
                R.string.tier_1_screen_5_pedigreeHelp,
                R.drawable.spouse2,
                // TODO reset input value when disabled? => dialog
                new InputDescription(InputType.Checkbox, R.string.tier_1_screen_5_option_1, r,
                        () -> InputTemplateFragment.atLeastOneDependency(r,
                                R.string.tier_1_screen_4_option_1,
                                R.string.tier_1_screen_4_option_2
                        )
                ),
                new InputDescription(InputType.Checkbox, R.string.tier_1_screen_5_option_2, r,
                        () -> InputTemplateFragment.atLeastOneDependency(r,
                                R.string.tier_1_screen_4_option_1,
                                R.string.tier_1_screen_4_option_2
                        )
                )
        );

        // Siblings
        registerFormFragment(1, R.string.tier_1_screen_6_title,
                R.string.tier_1_screen_6_description,
                R.string.tier_1_screen_6_pedigreeHelp,
                R.drawable.siblings_twins,
                new InputDescription(InputType.Spinner, R.string.tier_1_screen_6_option_1, r),
                new InputDescription(InputType.Spinner, R.string.tier_1_screen_6_option_2, r),
                new InputDescription(InputType.Spinner, R.string.tier_1_screen_6_option_3, r)
        );

        // Tier 2
        // Grandparents
        registerFormFragment(2, R.string.tier_2_screen_1_title,
                R.string.tier_2_screen_1_description,
                R.string.tier_2_screen_1_pedigreeHelp,
                R.drawable.grandparents,
                new InputDescription(InputType.Checkbox, R.string.tier_2_screen_1_option_1, r),
                new InputDescription(InputType.Checkbox, R.string.tier_2_screen_1_option_2, r),
                new InputDescription(InputType.Checkbox, R.string.tier_2_screen_1_option_3, r),
                new InputDescription(InputType.Checkbox, R.string.tier_2_screen_1_option_4, r)
        );

        // Grandchildren
        registerFormFragment(2, R.string.tier_2_screen_2_title,
                R.string.tier_2_screen_2_description,
                R.string.tier_2_screen_2_pedigreeHelp,
                R.drawable.grandchildren,
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_2_option_1, r)
        );

        // Cousins
        registerFormFragment(2, R.string.tier_2_screen_4_title,
                R.string.tier_2_screen_4_description,
                R.string.tier_2_screen_4_pedigreeHelp,
                R.drawable.cousins,
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_4_option_1, r)
        );

        // Aunts and Uncles
        registerFormFragment(2, R.string.tier_2_screen_5_title,
                R.string.tier_2_screen_5_description,
                R.string.tier_2_screen_5_pedigreeHelp,
                R.drawable.aunts_uncles,
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_5_option_1, r),
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_5_option_2, r),
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_5_option_3, r),
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_5_option_4, r)
        );

        // Nephews
        registerFormFragment(2, R.string.tier_2_screen_6_title,
                R.string.tier_2_screen_6_description,
                R.string.tier_2_screen_6_pedigreeHelp,
                R.drawable.nieces_nephews,
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_6_option_1, r),
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_6_option_2, r)
        );

        // Half-siblings
        registerFormFragment(2, R.string.tier_2_screen_8_title,
                R.string.tier_2_screen_8_description,
                R.string.tier_2_screen_8_pedigreeHelp,
                R.drawable.half_siblings,
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_8_option_1, r),
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_8_option_2, r),
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_8_option_3, r),
                new InputDescription(InputType.Spinner, R.string.tier_2_screen_8_option_4, r)
        );

        // Step-parents
        registerFormFragment(2, R.string.tier_2_screen_9_title,
                R.string.tier_2_screen_9_description,
                R.string.tier_2_screen_9_pedigreeHelp,
                R.drawable.step_parents,
                new InputDescription(InputType.Checkbox, R.string.tier_2_screen_9_option_1, r,
                        () -> InputTemplateFragment.atLeastOneDependency(r,
                                R.string.tier_2_screen_8_option_1,
                                R.string.tier_2_screen_8_option_2,
                                R.string.tier_2_screen_8_option_3,
                                R.string.tier_2_screen_8_option_4
                        )
                ),
                new InputDescription(InputType.Checkbox, R.string.tier_2_screen_9_option_2, r,
                        () -> InputTemplateFragment.atLeastOneDependency(r,
                                R.string.tier_2_screen_8_option_1,
                                R.string.tier_2_screen_8_option_2,
                                R.string.tier_2_screen_8_option_3,
                                R.string.tier_2_screen_8_option_4
                        )
                )
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