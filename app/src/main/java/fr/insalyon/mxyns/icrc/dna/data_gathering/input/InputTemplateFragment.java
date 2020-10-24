package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.insalyon.mxyns.icrc.dna.R;

public abstract class InputTemplateFragment<T> extends Fragment {

    public static final String ARG_TEXT = "text";
    public static final String ARG_VALUE = "value";

    private InputTemplateViewModel<T> viewModel = null;

    public InputTemplateFragment() {
    }

    public abstract T getValueFromBundle(Bundle bundle);

    public abstract void putValueToBundle(Bundle bundle);

    public InputTemplateFragment<T> init(InputDescription inputDescription) {

        Bundle args = new Bundle();
        args.putInt(ARG_TEXT, inputDescription.viewTextId);
        putValueToBundle(args);
        setArguments(args);

        return this;
    }

    @SuppressWarnings("UncheckedCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = (InputTemplateViewModel<T>) new ViewModelProvider(this).get(InputTemplateViewModel.class);

        @StringRes int text_id = R.string.template_text;
        T value = null;

        Bundle arguments = getArguments();
        if (arguments != null) {
            text_id = requireArguments().getInt(ARG_TEXT);
            value = getValueFromBundle(arguments);
        }

        viewModel.text.setValue(getResources().getString(text_id));
        viewModel.value.setValue(value);
    }

    public InputTemplateViewModel<T> getViewModel() {
        return viewModel;
    }
}