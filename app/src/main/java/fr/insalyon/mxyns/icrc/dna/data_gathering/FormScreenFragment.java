package fr.insalyon.mxyns.icrc.dna.data_gathering;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputDescription;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputTemplateFragment;

public class FormScreenFragment extends Fragment {

    private FormScreenFragmentViewModel viewModel;

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_IMAGE = "image";

    public int tier;
    private String title;

    public final ArrayList<InputTemplateFragment> inputFragments = new ArrayList<>();

    public static FormScreenFragment newInstance(
            int tier,
            @StringRes int title,
            @StringRes int description,
            @DrawableRes int image,
            InputDescription... inputs_desc
    ) {


        Log.d("data-oncreate", "build fragment");
        FormScreenFragment fragment = new FormScreenFragment();
        fragment.tier = tier;

        Bundle bundle = new Bundle();

        bundle.putInt(ARG_TITLE, title);
        bundle.putInt(ARG_DESCRIPTION, description);
        bundle.putInt(ARG_IMAGE, image);

        fragment.setArguments(bundle);

        for (InputDescription inputDescription : inputs_desc) {
            try {
                Log.d("make-input", "inputDesc : " + inputDescription );
                fragment.inputFragments.add(inputDescription.make(fragment));
            } catch (IllegalAccessException | java.lang.InstantiationException e) {
                System.out.println("Unable to create input : " + inputDescription);
                e.printStackTrace();
            }
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FormScreenFragmentViewModel.class);

        int title = -1, description = -1, image = -1;

        if (getArguments() != null) {
            title = getArguments().getInt(ARG_TITLE);
            description = getArguments().getInt(ARG_DESCRIPTION);
            image = getArguments().getInt(ARG_IMAGE);
        }

        Resources res = getResources();
        viewModel.setTitle(res.getString(title));
        viewModel.setDescription(res.getString(description));
        viewModel.setImageId(image);

        Log.d("data-oncreate", "formscreen fragment create " + (this.title = res.getString(title)));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.form_screen_fragment, container, false);

        final LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        final TextView descView = root.findViewById(R.id.description);
        viewModel.getDescription().observe(lifecycleOwner, descView::setText);

        final ImageView imageView = root.findViewById(R.id.relationshipRepresentation);
        viewModel.getImageId().observe(lifecycleOwner, imageView::setImageResource);

        // TODO set title


        LinearLayout form = root.findViewById(R.id.input_list_lin_layout);

        if (form.getChildCount() != inputFragments.size()) {
            FragmentManager fragMan = getChildFragmentManager();
            FragmentTransaction fragTransaction = fragMan.beginTransaction();


            for (int i = 0; i < inputFragments.size(); ++i) {
                if (fragMan.findFragmentByTag("input_" + i) == null) {
                    Log.d("transac", "input_" + i + " doesn't exist, adding " + inputFragments.get(i));
                    fragTransaction.add(form.getId(), inputFragments.get(i), "input_" + i);
                }
            }

            fragTransaction.commit();
        }


        Log.d("data-oncreate", "formscreen fragment create view " + title);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("data-oncreate", "formscreen fragment resume " + title);
    }
}