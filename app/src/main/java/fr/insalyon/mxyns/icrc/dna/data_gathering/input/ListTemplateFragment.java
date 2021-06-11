package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.Arrays;

import fr.insalyon.mxyns.icrc.dna.R;


public abstract class ListTemplateFragment<T> extends InputTemplateFragment<ListOption<T>> {

    protected View root;
    private final ArrayList<ListOption<T>> values = new ArrayList<>();

    public ListTemplateFragment() { }

    @Override
    protected void setEnabled(Boolean aBoolean) {

        if (root == null) return;

        root.findViewById(R.id.input_template_list).setEnabled(aBoolean);
        root.findViewById(R.id.input_list_text).setEnabled(aBoolean);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LifecycleOwner owner = getViewLifecycleOwner();
        root = inflater.inflate(R.layout.fragment_list_template, container, false);

        // for some reason dropdown comboboxes are called spinners
        Spinner list = root.findViewById(R.id.input_template_list);

        T[] res_values = getValuesArray();

        // generate ListOptions from resources array
        values.clear();
        for (T res_val : res_values)
            values.add(new ListOption<>(values.size(), res_val));

        // force data restoration after bc we now have the values' array to compare with saved value
        initializeUIFromBundle(getArguments());

        // set array used by list
        ArrayAdapter<ListOption<T>> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, values);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        list.setAdapter(spinnerArrayAdapter);

        TextView textView = root.findViewById(R.id.input_list_text);
        getViewModel().text.observe(owner, textView::setText);
        getViewModel().value.observe(owner,
                (v) -> list.setSelection(v.index)
        );

        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                updateValue(values.get(pos), true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return root;
    }

    protected abstract T[] getValuesArray();

    @Override
    public ListOption<T> parseValue(String value) {
        Log.d("list-parseValue", value);
        ListOption<T> res = values.stream().filter((v) -> v.value.equals(value)).findFirst().orElse(null);
        Log.d("list-parseValue", "res = " + res + " in " + Arrays.toString(values.toArray()));

        return res;
    }

    @Override
    public void putValueToBundle(@Nullable Bundle bundle) {

        // default value
        String value = null;

        // if was already created once (not putting default value)
        if (root != null) {
            Spinner list = (Spinner) root.findViewById(R.id.input_template_list);
            ListOption<java.lang.String> selected = (ListOption<java.lang.String>) list.getSelectedItem();
            value = selected.value;
        }

        bundle.putSerializable(ARG_VALUE, value);
    }

    @Override
    protected int valueToCount(ListOption<T> value) {
        return value.index;
    }
}