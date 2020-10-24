package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.Nullable;

import fr.insalyon.mxyns.icrc.dna.R;


public class SpinnerTemplateFragment extends InputTemplateFragment<Integer> {

    private View root;

    public SpinnerTemplateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LifecycleOwner owner = getViewLifecycleOwner();
        root = inflater.inflate(R.layout.fragment_integer_input_template, container, false);

        EditText intField = root.findViewById(R.id.input_template_integer_field);
        TextView textView = root.findViewById(R.id.input_template_integer_text);
        getViewModel().text.observe(owner, textView::setText);
        getViewModel().value.observe(owner, val -> intField.setText(String.valueOf(val)));

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public Integer getValueFromBundle(@Nullable Bundle bundle) {
        return bundle.getInt(ARG_VALUE);
    }

    @Override
    public void putValueToBundle(@Nullable Bundle bundle) {
        String value = "0";
        if (root != null)
            value = ((EditText) root.findViewById(R.id.input_template_integer_field)).getText().toString();

        bundle.putInt(ARG_VALUE, Integer.parseInt(value));
    }
}