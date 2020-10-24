package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import androidx.annotation.StringRes;

import java.util.HashMap;
import java.util.Map;

public class InputDescription {

    public final String viewType;

    @StringRes
    public final Integer viewTextId;

    private static final Map<String, Class> viewTemplatesMap;
    private static Integer count = 0;

    private final Class input_template_class;

    static {
        viewTemplatesMap = new HashMap<>();
        viewTemplatesMap.put("checkbox", CheckboxTemplateFragment.class);
        viewTemplatesMap.put("integer", SpinnerTemplateFragment.class);
    }

    public InputDescription(String viewType, Integer viewTextId) {

        this.viewTextId = viewTextId;
        this.viewType = viewType;
        this.input_template_class = viewTemplatesMap.get(viewType);
    }

    public InputTemplateFragment make() throws InstantiationException, IllegalAccessException {

        InputTemplateFragment inputFragment = (InputTemplateFragment) input_template_class.newInstance();

        count += 1;

        return inputFragment.init(this);
    }

    public String toString() {

        return "Input[type=" + viewType + ", class=" + input_template_class + ", textId=" + viewTextId + "]";
    }
}
