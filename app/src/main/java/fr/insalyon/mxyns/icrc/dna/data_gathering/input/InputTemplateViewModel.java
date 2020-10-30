package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonElement;

public class InputTemplateViewModel<T> extends ViewModel {

    MutableLiveData<T> value = new MutableLiveData<>();
    MutableLiveData<Integer> text_id = new MutableLiveData<>();
    MutableLiveData<String> text = new MutableLiveData<>();

    public int getTextId() {

        return text_id.getValue();
    }

    public void setValue(T raw) {
        value.setValue(raw);
    }
}
