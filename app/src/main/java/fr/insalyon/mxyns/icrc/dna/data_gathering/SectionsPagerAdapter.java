package fr.insalyon.mxyns.icrc.dna.data_gathering;

import android.content.Context;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputDescription;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputTemplateFragment;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputTemplateViewModel;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private final ArrayList<Integer> tab_titles = new ArrayList<>();
    private final HashMap<String, FormScreenFragment> inputToFragment = new HashMap<>();
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
                new InputDescription("integer", R.string.tier_1_screen_1_option_3)

        );

        registerFormFragment(1, R.string.tier_1_screen_3_title,
                R.string.tier_1_screen_3_description,
                R.drawable.ic_baseline_double_check_24,
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_1),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_2),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_3),
                new InputDescription("checkbox", R.string.tier_1_screen_3_option_4)
        );
    }

    private void registerFormFragment(
            int tier,
            @StringRes int title,
            @StringRes int description,
            @DrawableRes int image,
            InputDescription... inputs_desc) {

        tab_titles.add(title);
        FormScreenFragment formFragment = FormScreenFragment.newInstance(tier, title, description, image, inputs_desc);

        tabs.add(formFragment);

        for (InputDescription input : inputs_desc)
            inputToFragment.put(mContext.getResources().getResourceEntryName(input.viewTextId), formFragment);
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

    public void loadDataFromFile(String path) {

        JsonObject data = FileUtils.loadJsonFromFile(path);
        ArrayList<String> jsonPaths = FileUtils.findAll("input", data.getAsJsonObject("entries"));

        JsonObject temp;
        String input_name;
        for (String jPath : jsonPaths) {

            temp = FileUtils.getJsonFromPath("entries." + jPath, data);

            // e.g. : tier_1_screen_3_option_4
            input_name = temp.get("input").getAsString();
            Log.d("json-data-found", "found data : " + input_name + " => " + temp.get("raw").getAsString());

            ArrayList<InputTemplateFragment> frags = inputToFragment.get(input_name).inputFragments;

            int frag_index = Integer.parseInt(input_name.split("_")[5]) - 1;

            Log.d("json-data-val", "INSERTING : " + temp.toString() + " ==> " + temp.get("raw").getAsString());
            Log.d("json-data-val", "IN : " + frags.get(frag_index));

            if (frags.get(frag_index) != null) // try to avoid crashing if input is not found
                frags.get(frag_index).setValue(temp.get("raw").getAsString());
        }
    }
}