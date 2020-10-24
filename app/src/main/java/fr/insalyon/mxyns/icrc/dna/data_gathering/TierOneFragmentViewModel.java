package fr.insalyon.mxyns.icrc.dna.data_gathering;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TierOneFragmentViewModel extends ViewModel {

    private final MutableLiveData<String> title = new MutableLiveData<>(),
                                    description = new MutableLiveData<>(),
                                    option1_text = new MutableLiveData<>(),
                                    option2_text = new MutableLiveData<>();

    private final MutableLiveData<Integer> image_id = new MutableLiveData<>(),
                              index = new MutableLiveData<>();

    private final MutableLiveData<Boolean> option1_value = new MutableLiveData<>(), option2_value = new MutableLiveData<>();

    public LiveData<String> getTitle() {
        return this.title;
    }
    public LiveData<String> getDescription() {
        return this.description;
    }
    public LiveData<String> getOption1Text() {
        return this.option1_text;
    }
    public LiveData<Boolean> getOption1Value() {
        return this.option1_value;
    }
    public LiveData<String> getOption2Text() {
        return this.option2_text;
    }
    public LiveData<Boolean>  getOption2Value() {
        return this.option2_value;
    }
    public LiveData<Integer>  getImageId() {
        return this.image_id;
    }
    public LiveData<Integer> getIndex() {
        return this.index;
    }
    
    public void setTitle(String string) {
        this.title.setValue(string);
    }
    public void setDescription(String desc) {
        this.description.setValue(desc);
    }
    public void setOption1Text(String desc) {
        this.option1_text.setValue(desc);
    }
    public void setOption1Value(Boolean val) {
        this.option1_value.setValue(val);
    }
    public void setOption2Text(String desc) {
        this.option2_text.setValue(desc);
    }
    public void setOption2Value(Boolean val) {
        this.option2_value.setValue(val);
    }
    public void setImageId(Integer id) {
        this.image_id.setValue(id);
    }
    public void setIndex(Integer index) {
        this.index.setValue(index);
    }
}