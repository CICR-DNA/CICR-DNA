package fr.insalyon.mxyns.icrc.dna;

import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

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

    @ColorInt
    public static int getStatusColor(Resources res, float score) {

        return res.getColor(score >= Constants.getSecondThreshold(res) ?
                R.color.status_ok
                : score >= Constants.getFirstThreshold(res) ?
                R.color.status_medium
                : R.color.status_bad);
    }

    @StringRes
    public static int getStatusLabelId(Resources res, float score) {

        return score >= Constants.getSecondThreshold(res) ?
                R.string.third_step_label
                : score >= Constants.getFirstThreshold(res) ?
                R.string.second_step_label
                : R.string.first_step_label;
    }

    public static String getStatusLabel(Resources res, float score) {

        return res.getString(getStatusLabelId(res, score));
    }
}
