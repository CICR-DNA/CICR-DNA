package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.Nullable;

import fr.insalyon.mxyns.icrc.dna.R;


public class CheckboxTemplateFragment extends InputTemplateFragment<Boolean> {

    private View root;

    public CheckboxTemplateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LifecycleOwner owner = getViewLifecycleOwner();
        root = inflater.inflate(R.layout.fragment_checkbox_template, container, false);

        CheckBox checkbox = root.findViewById(R.id.input_template_checkbox);
        getViewModel().text.observe(owner, checkbox::setText);
        getViewModel().value.observe(owner, checkbox::setChecked);

        checkbox.setOnCheckedChangeListener((a, isChecked) -> updateViewModelValue(isChecked));

        return root;
    }

    @Override
    public Boolean getValueFromBundle(@Nullable Bundle bundle) {
        return bundle.getBoolean(ARG_VALUE);
    }

    @Override
    public void putValueToBundle(@Nullable Bundle bundle) {
        boolean value = false;
        if (root != null)
            value = ((CheckBox) root.findViewById(R.id.input_template_checkbox)).isChecked();

        bundle.putBoolean(ARG_VALUE, value);
    }

    @Override
    protected int valueToCount(Boolean value) {

        return value ? 1 : 0;
    }
}