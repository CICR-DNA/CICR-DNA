package fr.insalyon.mxyns.icrc.dna.data_gathering;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import fr.insalyon.mxyns.icrc.dna.Constants;
import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputDescription;
import fr.insalyon.mxyns.icrc.dna.data_gathering.input.InputTemplateFragment;

/**
 * DataGatheringActivity tab / screen, containing some input fragments
 */
public class FormScreenFragment extends Fragment {

    /**
     * Bundle arguments
     */
    private static final String ARG_TITLE = "title";
    private static final String ARG_PEDIGREE_HELP = "pedigree_help";
    private static final String ARG_DESCRIPTION = "description";

    /**
     * Input fragments
     */
    public final ArrayList<InputTemplateFragment> inputFragments = new ArrayList<>();

    /**
     * Image ID (pedigree)
     */
    private static final String ARG_IMAGE = "image";

    /**
     * Input tier
     */
    public int tier;

    /**
     * Associated ViewModel
     */
    private FormScreenFragmentViewModel viewModel;

    /**
     * Unused (could be displayed in toolbar)
     */
    private String title;

    private boolean containsConditional = false;

    public static FormScreenFragment newInstance(
            int tier,
            @StringRes int titleId,
            @StringRes int descriptionId,
            @StringRes int helpDescriptionId,
            @DrawableRes int imageId,
            InputDescription... inputs_desc
    ) {

        Log.d("data-oncreate", "build fragment");
        FormScreenFragment fragment = new FormScreenFragment();
        fragment.tier = tier;

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TITLE, titleId);
        bundle.putInt(ARG_DESCRIPTION, descriptionId);
        bundle.putInt(ARG_PEDIGREE_HELP, helpDescriptionId);
        bundle.putInt(ARG_IMAGE, imageId);

        fragment.setArguments(bundle);

        // generate input fragments from descriptions
        for (InputDescription inputDescription : inputs_desc) {
            try {
                Log.d("make-input", "inputDesc : " + inputDescription);
                fragment.inputFragments.add(inputDescription.make(fragment));
                fragment.containsConditional |= inputDescription.conditional;
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | java.lang.InstantiationException e) {
                System.out.println("Unable to create input : " + inputDescription);
                e.printStackTrace();
            }
        }

        return fragment;
    }

    /**
     * Load values from Bundle and saves it to ViewModel
     *
     * @param savedInstanceState bundle filled in newInstance
     * @see FormScreenFragment#newInstance(int, int, int, int, int, InputDescription...)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FormScreenFragmentViewModel.class);

        int titleId = -1, descriptionId = -1, imageId = -1, pedigreeHelpId = -1;

        if (getArguments() != null) {
            titleId = getArguments().getInt(ARG_TITLE);
            descriptionId = getArguments().getInt(ARG_DESCRIPTION);
            imageId = getArguments().getInt(ARG_IMAGE);
            pedigreeHelpId = getArguments().getInt(ARG_PEDIGREE_HELP);
        }

        Resources res = getResources();
        viewModel.setTitle(res.getString(titleId));
        viewModel.setDescription(res.getString(descriptionId));
        viewModel.setImageId(imageId);
        viewModel.setPedigreeHelp(pedigreeHelpId);

        Log.d("data-oncreate", "formscreen fragment create " + (this.title = res.getString(titleId)));
    }

    /**
     * Setup view using ViewModel
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.form_screen_fragment, container, false);

        final LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        final TextView descView = root.findViewById(R.id.description);
        viewModel.getDescription().observe(lifecycleOwner, descView::setText);
        descView.setMovementMethod(new ScrollingMovementMethod());

        final ImageView imageView = root.findViewById(R.id.relationshipRepresentation);
        viewModel.getImageId().observe(lifecycleOwner, imageView::setImageResource);

        WindowManager windowManager = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        imageView.getLayoutParams().height = (int) (size.y * Constants.getFloat(getResources(), R.dimen.datagathering_image_screen_prop).getFloat());

        final ImageButton helpButton = root.findViewById(R.id.pedigreeHelpButton);
        helpButton.setOnClickListener(e -> {

            Integer pedigreeHelpId = viewModel.getPedigreeHelp().getValue();
            if (pedigreeHelpId == null) {
                pedigreeHelpId = R.string.template_text;
            }

            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.datagathering_pedigree_help_dialog_title)
                    .setMessage(pedigreeHelpId)
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(R.drawable.ic_baseline_help_outline_24)
                    .show();
        });

        // TODO put title in toolbar

        LinearLayout form = root.findViewById(R.id.input_list_lin_layout);

        Log.d("formscreen-insert", Arrays.toString(inputFragments.toArray()));

        // if input fragments aren't already added to screen fragment
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

    public boolean doesContainConditional() {
        return containsConditional;
    }

    public void onSwippedTo() {

        for (InputTemplateFragment frag : inputFragments) {
            frag.onSwippedTo();
        }
    }
}