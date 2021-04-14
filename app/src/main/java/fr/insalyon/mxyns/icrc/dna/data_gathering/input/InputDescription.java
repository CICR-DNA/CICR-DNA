package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.StringRes;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import fr.insalyon.mxyns.icrc.dna.data_gathering.FormScreenFragment;

/**
 * Description of an DataGatheringActivity input. Used to generate a corresponding InputTemplateFragment
 */
public class InputDescription implements Serializable {

    public final InputType viewType;
    public final String inputName, displayName;

    @StringRes
    public final Integer viewTextId;

    /**
     * Input name => Fragment class register
     */
    private static final Map<InputType, Class> viewTemplatesMap;

    private transient final Class input_template_class;

    public enum InputType {
        Checkbox,
        Spinner,
        StringList
    }

    static {
        viewTemplatesMap = new HashMap<>();
        viewTemplatesMap.put(InputType.Checkbox, CheckboxTemplateFragment.class);
        viewTemplatesMap.put(InputType.Spinner, SpinnerTemplateFragment.class);
        viewTemplatesMap.put(InputType.StringList, StringListTemplateFragment.class);
    }

    /**
     * Flags that indicates if the input of the field depends on the input of other fields
     */
    public final boolean conditional;

    /**
     * Predicate evaluated on Fragment resume. Defines if the element should be enabled or not
     */
    public final Supplier<Boolean> predicate;

    public InputDescription(InputType type, Integer viewTextId, Resources res) {
        this(type, viewTextId, res, false, null);
    }

    public InputDescription(InputType type, Integer viewTextId, Resources res, Supplier<Boolean> predicate) {
        this(type, viewTextId, res, predicate != null, predicate);
    }

    protected InputDescription(InputType type, Integer viewTextId, Resources res, boolean conditional, Supplier<Boolean> predicate) {
        this.viewTextId = viewTextId;
        this.inputName = res.getResourceEntryName(viewTextId);
        this.displayName = res.getString(viewTextId);
        this.predicate = predicate;

        this.viewType = type;
        this.input_template_class = viewTemplatesMap.get(viewType);
        this.conditional = conditional;
    }

    public InputTemplateFragment make(FormScreenFragment owner) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Log.d("data-oncreate", "make input fragment : \ninput : " + inputName + "\ntype=" + viewType);
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
