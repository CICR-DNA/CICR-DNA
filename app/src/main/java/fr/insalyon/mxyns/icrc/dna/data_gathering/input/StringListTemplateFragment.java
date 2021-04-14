package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import fr.insalyon.mxyns.icrc.dna.MainActivity;

public class StringListTemplateFragment extends ListTemplateFragment<String> {

    @Override
    protected String[] getValuesArray() {
        return getResources().getStringArray(getResources().getIdentifier(this.input_name + "_values", "array", MainActivity.class.getPackage().getName()));
    }
}
