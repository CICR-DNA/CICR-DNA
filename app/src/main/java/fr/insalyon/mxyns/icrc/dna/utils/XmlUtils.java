package fr.insalyon.mxyns.icrc.dna.utils;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class XmlUtils {

    /**
     * From @codebycliff
     *
     * @param res
     * @param hashMapResId
     * @return
     * @link https://gist.github.com/codebycliff/11198553
     */
    public static Map<String, String> getHashMapResource(Resources res, int hashMapResId) {
        Map<String, String> map = null;
        XmlResourceParser parser = res.getXml(hashMapResId);

        String key = null, value = null;

        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("utils", "Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("map")) {
                        boolean isLinked = parser.getAttributeBooleanValue(null, "linked", false);

                        map = isLinked
                                ? new LinkedHashMap<String, String>()
                                : new HashMap<String, String>();
                    } else if (parser.getName().equals("entry")) {
                        key = parser.getAttributeValue(null, "key");

                        if (null == key) {
                            parser.close();
                            return null;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("entry")) {
                        map.put(key, value);
                        key = null;
                        value = null;
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (null != key) {
                        value = parser.getText();
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    public static class XmlTagExtractor {

        private final static String NAMESPACE = null;
        private final static String ROOT_TAG = "resources";

        public static HashMap<String, Float> parseFloat(InputStream is, String entryTag, String entryKeyAttr) throws XmlPullParserException, IOException {
            HashMap<String, Float> parsed = new HashMap<>();
            parse(is, entryTag, entryKeyAttr).forEach((k, v) -> {
                try {
                    Log.d("parse-score-xml-entry", "found entry : " + k + " = " + v);
                    parsed.put(k, Float.parseFloat(v));
                } catch (Exception ex) {
                    Log.d("parse-score-xml", "error while parsing value with name " + k);
                }
            });

            return parsed;
        }

        public static HashMap<String, String> parse(InputStream is, String entryTag, String entryKeyAttr) throws XmlPullParserException, IOException {

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, NAMESPACE);
            parser.nextTag();
            return readFeed(parser, entryTag, entryKeyAttr);
        }

        private static HashMap<String, String> readFeed(XmlPullParser parser, String entryTag, String entryKeyAttr) throws XmlPullParserException, IOException {

            HashMap<String, String> entries = new HashMap<>();

            parser.require(XmlPullParser.START_TAG, NAMESPACE, ROOT_TAG);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String tagName = parser.getName();

                // Starts by looking for a score entry tag
                if (tagName.equals(entryTag)) {
                    String scoreName = parser.getAttributeValue(NAMESPACE, entryKeyAttr);
                    parser.require(XmlPullParser.START_TAG, NAMESPACE, entryTag);
                    String scoreValueRaw = readText(parser);
                    parser.require(XmlPullParser.END_TAG, NAMESPACE, entryTag);
                    entries.put(scoreName, scoreValueRaw);

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
