package fr.insalyon.mxyns.icrc.dna.data_gathering.input;

import java.io.Serializable;

public class InputResult implements Serializable {

    private final String inputName;
    private final String displayName;

    private final String rawValue;

    // must be set later by loading string resource input_name + "_path"
    // bc InputResult is made by a Fragment that can be detached at the time the method is called
    private String jsonPath;
    private final int count;

    public InputResult(String inputName, String displayName, String rawValue, int count) {

        this.inputName = inputName;
        this.displayName = displayName;
        this.rawValue = rawValue;
        this.count = count;
    }

    public String getInputName() {
        return inputName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRaw() {
        return rawValue;
    }

    public int getCount() {
        return count;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @Override
    public String toString() {
        return "InputResult{" +
                "input_name='" + inputName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", rawValue='" + rawValue + '\'' +
                ", jsonPath='" + jsonPath + '\'' +
                ", count=" + count +
                '}';
    }
}