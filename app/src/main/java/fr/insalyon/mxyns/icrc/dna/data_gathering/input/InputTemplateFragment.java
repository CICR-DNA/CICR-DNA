package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;

import fr.insalyon.mxyns.icrc.dna.DataGatheringActivity;
import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.data_gathering.FormScreenFragment;

/**
 * Represents an user input of a certain type
 *
 * @param <T> type of the input
 */
public abstract class InputTemplateFragment<T> extends Fragment {

    public static final String ARG_TEXT_ID = "text_id";
    public static final String ARG_TEXT = "display_name";
    public static final String ARG_NAME = "input_name";
    public static final String ARG_VALUE = "value";

    private InputTemplateViewModel<T> viewModel;
    public String input_name;
    public InputDescription description;
    public FormScreenFragment owner;

    public InputTemplateFragment() {
    }

    /**
     * Must be called after instantiation for fragment to work
     *
     * @param inputDescription input description from which the input fragment will be generated
     * @param owner
     * @return
     */
    public InputTemplateFragment<T> init(InputDescription inputDescription, FormScreenFragment owner) {

        Bundle args = new Bundle();
        this.description = inputDescription;
        this.input_name = inputDescription.inputName;
        this.owner = owner;
        args.putInt(ARG_TEXT_ID, inputDescription.viewTextId);
        args.putString(ARG_TEXT, inputDescription.displayName);
        args.putString(ARG_NAME, this.input_name);
        putValueToBundle(args); // put default value
        setArguments(args);

        Log.d("name-null-init", String.valueOf(getArguments()));

        JsonObject data = DataGatheringActivity.data.get(this.input_name);
        if (data != null && data.get("raw") != null) {
            setRawValue(data.get("raw").getAsString());
        } else
            Log.d("init", "init : can't get value");

        Log.d("haha init", "input init my friend : " + inputDescription);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = (InputTemplateViewModel<T>) new ViewModelProvider(this).get(InputTemplateViewModel.class);

        if (this instanceof CheckboxTemplateFragment)
            Log.d("no-check-zone-oncreate", "CheckBox " + this.input_name + " : check is  (viewmodel:" + getViewModel().value.getValue() + ", data=" + DataGatheringActivity.data.get(input_name) + " )");

        this.input_name = requireArguments().getString(ARG_NAME);

        // on text_id change, change displayed text
        viewModel.text_id.observe(this, newId -> viewModel.text.setValue(getResources().getString(newId)));

        Log.d("name-null-create", String.valueOf(getArguments()));
        initializeUIFromBundle(getArguments());

        Log.d("data-oncreate", "input fragment created " + this.input_name + " aka " + viewModel.text.getValue());
    }

    private void initializeUIFromBundle(Bundle arguments) {

        @StringRes int text_id = R.string.template_text;
        T value = null;

        if (arguments != null) {
            text_id = arguments.getInt(ARG_TEXT_ID);
            value = parseValue(arguments.getSerializable(ARG_VALUE).toString());
        }

        viewModel.text.setValue(getResources().getString(text_id));
        viewModel.text_id.setValue(text_id);

        if (value != null && viewModel.value.getValue() == null) {
            viewModel.value.setValue(value);
            Log.d("initialize-ui", input_name + " value = " + value);
        } else
            Log.d("initialize-ui", "no value");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this instanceof CheckboxTemplateFragment)
            Log.d("no-check-zone-onresume", "CheckBox " + this.input_name + " : check is  (viewmodel:" + getViewModel().value.getValue() + ", data=" + DataGatheringActivity.data.get(input_name) + " )");

        updateValue(viewModel.value.getValue());
    }

    protected void updateValue(T value) {

        if (this instanceof CheckboxTemplateFragment)
            Log.d("no-check-zone-b4updt", "CheckBox " + this.input_name + " : check is  (viewmodel:" + getViewModel().value.getValue() + ", data=" + DataGatheringActivity.data.get(input_name) + " )");

        if (value == null)
            return;

        JsonObject result = new JsonObject();
        result.addProperty("raw", value.toString());
        result.addProperty("count", valueToCount(value));
        DataGatheringActivity.data.put(this.input_name, result);


        Log.d("name-null-set", getResources().getString(viewModel.text_id.getValue()) + " : set value");

        if (viewModel == null)
            return;

        viewModel.value.setValue(value);

        if (this instanceof CheckboxTemplateFragment)
            Log.d("no-check-zone-aftrupdt", "CheckBox " + this.input_name + " : check is  (viewmodel:" + getViewModel().value.getValue() + ", data=" + DataGatheringActivity.data.get(input_name) + " )");
    }

    public InputResult getInputResult() {

        JsonObject data = DataGatheringActivity.data.get(this.input_name);
        String rawValue = data.get("raw").getAsString();
        String displayName = getArguments().getString(ARG_TEXT);

        return new InputResult(
                input_name,
                displayName,
                rawValue,
                valueToCount(parseValue(rawValue))
        );
    }

    protected abstract void putValueToBundle(Bundle bundle);

    protected abstract int valueToCount(T value);

    public InputTemplateViewModel<T> getViewModel() {
        return viewModel;
    }

    public abstract T parseValue(String str);

    public void setRawValue(String str) {

        if (str == null) return;

        if (viewModel != null)
            viewModel.setValue(parseValue(str));

        getArguments().putSerializable(ARG_VALUE, str);
    }
}