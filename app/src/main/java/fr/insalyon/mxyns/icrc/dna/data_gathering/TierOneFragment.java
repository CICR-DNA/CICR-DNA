package fr.insalyon.mxyns.icrc.dna.data_gathering;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import fr.insalyon.mxyns.icrc.dna.R;

public class TierOneFragment extends Fragment {

    private TierOneFragmentViewModel viewModel;

    private static final String ARG_INDEX = "tab_index";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_IMAGE = "image";
    private static final String ARG_OPTION_1_TEXT = "option_1_text";
    private static final String ARG_OPTION_2_TEXT = "option_2_text";

    public static TierOneFragment newInstance(
            int index,
            @StringRes int title,
            @StringRes int description,
            @DrawableRes int image,
            @StringRes int option1_text,
            @StringRes int option2_text) {

        TierOneFragment fragment = new TierOneFragment();

        Bundle bundle = new Bundle();

        bundle.putInt(ARG_INDEX, index);
        bundle.putInt(ARG_TITLE, title);
        bundle.putInt(ARG_DESCRIPTION, description);
        bundle.putInt(ARG_IMAGE, image);
        bundle.putInt(ARG_OPTION_1_TEXT, option1_text);
        bundle.putInt(ARG_OPTION_2_TEXT, option2_text);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TierOneFragmentViewModel.class);
        int index = 1, title = -1, description = -1, option1_text = -1, option2_text = -1, image = -1;

        if (getArguments() != null) {
            index = getArguments().getInt(ARG_INDEX);
            title = getArguments().getInt(ARG_TITLE);
            description = getArguments().getInt(ARG_DESCRIPTION);
            image = getArguments().getInt(ARG_IMAGE);
            option1_text = getArguments().getInt(ARG_OPTION_1_TEXT);
            option2_text = getArguments().getInt(ARG_OPTION_2_TEXT);
        }

        Resources res = getResources();
        viewModel.setIndex(index);
        viewModel.setTitle(res.getString(title));
        viewModel.setDescription(res.getString(description));
        viewModel.setImageId(image);
        viewModel.setOption1Text(res.getString(option1_text));
        viewModel.setOption2Text(res.getString(option2_text));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tier_one_fragment, container, false);

        final LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        final TextView descView = root.findViewById(R.id.description);
        viewModel.getDescription().observe(lifecycleOwner, descView::setText);

        final CheckBox option1 = root.findViewById(R.id.firstOption);
        viewModel.getOption1Text().observe(lifecycleOwner, option1::setText);
        viewModel.getOption1Value().observe(lifecycleOwner, option1::setChecked);

        final CheckBox option2 = root.findViewById(R.id.secondOption);
        viewModel.getOption2Text().observe(lifecycleOwner, option2::setText);
        viewModel.getOption2Value().observe(lifecycleOwner, option2::setChecked);

        final ImageView imageView = root.findViewById(R.id.relationshipRepresentation);
        viewModel.getImageId().observe(lifecycleOwner, imageView::setImageResource);

        // TODO set title

        return root;
    }
}