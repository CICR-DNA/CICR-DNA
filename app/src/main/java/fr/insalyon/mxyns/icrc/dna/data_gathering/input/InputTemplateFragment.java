package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

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

    protected abstract T getValueFromBundle(Bundle bundle);

    protected abstract void putValueToBundle(Bundle bundle);

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
        viewModel.text_id.setValue(text_id);

        // on text_id change, change displayed text
        viewModel.text_id.observe(this, newId -> viewModel.text.setValue(getResources().getString(newId)));
        viewModel.value.setValue(value);
    }

    protected void updateViewModelValue(T value) {

        if (viewModel == null)
            return;

        viewModel.value.setValue(value);
    }

    public InputResult<T> getValue() {

        int id = viewModel.getTextId();
        String name = getResources().getResourceEntryName(id);

        TypedValue unit_score_holder = new TypedValue();
        try {
            getResources().getValue(getResources().getIdentifier(name, "dimen", requireActivity().getPackageName()), unit_score_holder, true);
        } catch (Exception e) {
            Log.d("result-calc", "error while looking for unit_score " + name + " for Input " + viewModel.text.getValue());
        }

        return new InputResult<>(id,
                name,
                viewModel.text.getValue(),
                viewModel.value.getValue(),
                valueToScore(viewModel.value.getValue(), unit_score_holder.getFloat()));
    }

    protected abstract float valueToScore(T value, float unit_score);

    public InputTemplateViewModel<T> getViewModel() {
        return viewModel;
    }

}