package fr.insalyon.mxyns.icrc.dna;

import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.StringRes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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

    @StringRes
    public static int getStatusInfoId(Resources res, float score) {

        return score >= Constants.getSecondThreshold(res) ?
                R.string.third_step_info
                : score >= Constants.getFirstThreshold(res) ?
                R.string.second_step_info
                : R.string.first_step_info;
    }

    public static String getStatusInfo(Resources res, float score) {

        return res.getString(getStatusInfoId(res, score));
    }

    public static TypedValue getFloat(Resources res, @DimenRes int float_id) {

        TypedValue value_holder = new TypedValue();
        try {
            res.getValue(float_id, value_holder, true);
        } catch (Exception ignored) {
            return null;
        }

        return value_holder;
    }

    public static HashMap<String, Float> loadScores(Resources res, String scoresFilename) {

        int scoreFileResId = res.getIdentifier(scoresFilename, "raw", MainActivity.class.getPackage().getName());
        try (InputStream is = res.openRawResource(scoreFileResId)) {
//            Log.d("xml-parse-raw", new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")));
            return XmlScoreParser.parse(is);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static class XmlScoreParser {

        private final static String NAMESPACE = null;
        private final static String ROOT_TAG = "resources";
        private final static String SCORE_ENTRY_TAG = "score";
        private final static String SCORE_ENTRY_NAME_ATTRIBUTE = "name";

        public static HashMap<String, Float> parse(InputStream is) throws XmlPullParserException, IOException {

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, NAMESPACE);
            parser.nextTag();
            return readFeed(parser);
        }

        private static HashMap<String, Float> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

            HashMap<String, Float> entries = new HashMap<>();

            parser.require(XmlPullParser.START_TAG, NAMESPACE, ROOT_TAG);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String tagName = parser.getName();

                // Starts by looking for a score entry tag
                if (tagName.equals(SCORE_ENTRY_TAG)) {
                    String scoreName = parser.getAttributeValue(NAMESPACE, SCORE_ENTRY_NAME_ATTRIBUTE);
                    parser.require(XmlPullParser.START_TAG, NAMESPACE, SCORE_ENTRY_TAG);
                    String scoreValueRaw = readText(parser);
                    parser.require(XmlPullParser.END_TAG, NAMESPACE, SCORE_ENTRY_TAG);

                    try {
                        Log.d("parse-score-xml-entry", "found entry : " + scoreName + " = " + scoreValueRaw);
                        entries.put(scoreName, Float.parseFloat(scoreValueRaw));
                    } catch (Exception ex) {
                        Log.d("parse-score-xml", "error while parsing value with name " + scoreName);
                    }

                } else {
                    skip(parser);
                }
            }

            return entries;
        }

        private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }

        private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }

    }
}
