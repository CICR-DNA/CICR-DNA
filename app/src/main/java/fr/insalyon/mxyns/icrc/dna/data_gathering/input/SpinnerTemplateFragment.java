package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
        intField.setText(String.valueOf(getViewModel().value.getValue()));
        intField.setSelection(intField.getText().length());

        intField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                int val;
                try {
                    val = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {
                    val = 0;
                    intField.setError(getResources().getString(R.string.wrong_integer_input_error));
                }

                updateValue(val, true);
            }
        });

        // Inflate the layout for this fragment
        return root;
    }



    @Override
    protected void setEnabled(Boolean aBoolean) {

        root.findViewById(R.id.input_template_integer_field).setEnabled(aBoolean);
        root.findViewById(R.id.input_template_integer_text).setEnabled(aBoolean);
    }


    @Override
    public void putValueToBundle(@Nullable Bundle bundle) {
        String value = "0";
        if (root != null)
            value = ((EditText) root.findViewById(R.id.input_template_integer_field)).getText().toString();

        bundle.putSerializable(ARG_VALUE, Integer.parseInt(value));
    }

    @Override
    protected int valueToCount(Integer value) {
        return value;
    }

    @Override
    public Integer parseValue(String str) {
        return Integer.parseInt(str);
    }
}