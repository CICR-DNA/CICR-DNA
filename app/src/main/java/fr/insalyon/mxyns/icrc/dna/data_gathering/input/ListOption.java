package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

public class ListOption<T> {

    public final int index;
    public final T value;

    public ListOption(int index, T value) {

        this.index = index;
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
