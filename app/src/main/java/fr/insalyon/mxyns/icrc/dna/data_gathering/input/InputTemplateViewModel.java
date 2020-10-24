package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InputTemplateViewModel<T> extends ViewModel {

    MutableLiveData<T> value = new MutableLiveData<>();
    MutableLiveData<String> text = new MutableLiveData<>();
}
