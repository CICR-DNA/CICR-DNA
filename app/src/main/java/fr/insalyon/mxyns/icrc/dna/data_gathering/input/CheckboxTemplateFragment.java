package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.Nullable;

import fr.insalyon.mxyns.icrc.dna.R;


public class CheckboxTemplateFragment extends InputTemplateFragment<Boolean> {

    private View root;

    public CheckboxTemplateFragment() {
    }

    @Override
    protected void setEnabled(Boolean aBoolean) {

        root.findViewById(R.id.input_template_checkbox).setEnabled(aBoolean);
        root.findViewById(R.id.input_checkbox_text).setEnabled(aBoolean);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LifecycleOwner owner = getViewLifecycleOwner();
        root = inflater.inflate(R.layout.fragment_checkbox_template, container, false);

        CheckBox checkbox = root.findViewById(R.id.input_template_checkbox);
        TextView textView = root.findViewById(R.id.input_checkbox_text);
        getViewModel().text.observe(owner, textView::setText);
        getViewModel().value.observe(owner, checkbox::setChecked);

        checkbox.setOnCheckedChangeListener((a, isChecked) -> updateValue(isChecked, true));

        return root;
    }

    @Override
    public void putValueToBundle(@Nullable Bundle bundle) {
        boolean value = false;
        if (root != null)
            value = ((CheckBox) root.findViewById(R.id.input_template_checkbox)).isChecked();

        bundle.putSerializable(ARG_VALUE, value);
    }

    @Override
    protected int valueToCount(Boolean value) {

        return value ? 1 : 0;
    }

    @Override
    public Boolean parseValue(String str) {
        return Boolean.parseBoolean(str);
    }
}