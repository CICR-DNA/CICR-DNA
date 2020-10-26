package fr.insalyon.mxyns.icrc.dna.data_gathering;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FormScreenFragmentViewModel extends ViewModel {

    private final MutableLiveData<String> title = new MutableLiveData<>(),
            description = new MutableLiveData<>();

    private final MutableLiveData<Integer> image_id = new MutableLiveData<>(),
            tier = new MutableLiveData<>();

    public LiveData<String> getTitle() {
        return this.title;
    }

    public LiveData<String> getDescription() {
        return this.description;
    }

    public LiveData<Integer> getImageId() {
        return this.image_id;
    }

    public LiveData<Integer> getTier() {
        return this.tier;
    }

    public void setTitle(String string) {
        this.title.setValue(string);
    }

    public void setImageId(Integer id) {
        this.image_id.setValue(id);
    }

    public void setDescription(String desc) {
        this.description.setValue(desc);
    }

    public void setTier(Integer tier) {
        this.tier.setValue(tier);
    }
}