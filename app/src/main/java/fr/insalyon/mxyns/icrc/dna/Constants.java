package fr.insalyon.mxyns.icrc.dna;

import android.content.res.Resources;
import android.util.TypedValue;

public class Constants {

    public static float FIRST_THRESHOLD = 0;
    public static float SECOND_THRESHOLD = 0;

    public static void init(Resources res) {

        TypedValue value_holder = new TypedValue();
        try {
            res.getValue(R.dimen.first_threshold, value_holder, true);
            Constants.FIRST_THRESHOLD = value_holder.getFloat();

            res.getValue(R.dimen.second_threshold, value_holder, true);
            Constants.SECOND_THRESHOLD = value_holder.getFloat();
        } catch (Exception ignored) {}
    }
}
