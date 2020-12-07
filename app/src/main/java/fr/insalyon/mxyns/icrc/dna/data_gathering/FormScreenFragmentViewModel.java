package fr.insalyon.mxyns.icrc.dna.data_gathering;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Contains UI Related data saved on Activity destruction (eg rotation) and to be reloaded when Fragment must be shown
 */
public class FormScreenFragmentViewModel extends ViewModel {

    private final MutableLiveData<String> title = new MutableLiveData<>(),
            description = new MutableLiveData<>();

    private final MutableLiveData<Integer> image_id = new MutableLiveData<>(),
            pedigree_help = new MutableLiveData<>();

    public LiveData<String> getTitle() {
        return this.title;
    }

    public LiveData<String> getDescription() {
        return this.description;
    }

    public LiveData<Integer> getPedigreeHelp() {
        return this.pedigree_help;
    }

    public LiveData<Integer> getImageId() {
        return this.image_id;
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

    public void setPedigreeHelp(Integer pedigreeHelp) {
        this.pedigree_help.setValue(pedigreeHelp);
    }
}