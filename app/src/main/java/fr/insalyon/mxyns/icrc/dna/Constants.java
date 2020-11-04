package fr.insalyon.mxyns.icrc.dna;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;

public class Constants {

    public static float getFirstThreshold(Resources res) {

        TypedValue value_holder = new TypedValue();
        try {
            res.getValue(R.dimen.first_threshold, value_holder, true);
            return value_holder.getFloat();
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static float getSecondThreshold(Resources res) {

        TypedValue value_holder = new TypedValue();
        try {
            res.getValue(R.dimen.second_threshold, value_holder, true);
            return value_holder.getFloat();
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static int getStatusColor(Resources res, float score) {

        return res.getColor(score >= Constants.getSecondThreshold(res) ?
                R.color.status_ok
                : score >= Constants.getFirstThreshold(res) ?
                R.color.status_medium
                : R.color.status_bad);
    }
}
