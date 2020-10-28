package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import java.io.Serializable;

public class InputResult<T> implements Serializable {


    private final int id;
    private final String name;
    private final String displayName;
    private final String jsonPath;

    private final T raw;
    private final float score;
    private final int count;

    public InputResult(int id, String jsonPath, String name, String displayName, T raw, int count, float score) {

        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.raw = raw;
        this.score = score;
        this.count = count;
        this.jsonPath = jsonPath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public T getRaw() {
        return raw;
    }

    public float getScore() {
        return score;
    }


    @Override
    public String toString() {
        return "InputResult{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", raw=" + raw +
                ", score=" + score +
                '}';
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public int getCount() {
        return count;
    }
}
