package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.StringRes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import fr.insalyon.mxyns.icrc.dna.data_gathering.FormScreenFragment;

/**
 * Description of an DataGatheringActivity input. Used to generate a corresponding InputTemplateFragment
 */
public class InputDescription implements Serializable {

    public final String viewType, inputName, displayName;

    @StringRes
    public final Integer viewTextId;

    /**
     * Input name => Fragment class register
     */
    private static final Map<String, Class> viewTemplatesMap;

    private transient final Class input_template_class;

    static {
        viewTemplatesMap = new HashMap<>();
        viewTemplatesMap.put("checkbox", CheckboxTemplateFragment.class);
        viewTemplatesMap.put("integer", SpinnerTemplateFragment.class);
    }

    public InputDescription(String viewType, Integer viewTextId, Resources res) {

        this.viewTextId = viewTextId;
        this.inputName = res.getResourceEntryName(viewTextId);
        this.displayName = res.getString(viewTextId);

        this.viewType = viewType;
        this.input_template_class = viewTemplatesMap.get(viewType);
    }

    public InputTemplateFragment make(FormScreenFragment owner) throws InstantiationException, IllegalAccessException {

        Log.d("data-oncreate", "make input fragment : \ninput : " + inputName +"\ntype="+viewType);
        InputTemplateFragment inputFragment = (InputTemplateFragment) input_template_class.newInstance();
        inputFragment.init(this, owner);

        return inputFragment;
    }

    @Override
    public String toString() {
        return "InputDescription{" +
                "viewType='" + viewType + '\'' +
                ", inputName='" + inputName + '\'' +
                ", viewTextId=" + viewTextId +
                ", input_template_class=" + input_template_class +
                '}';
    }
}
