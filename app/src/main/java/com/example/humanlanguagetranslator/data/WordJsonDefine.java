package com.example.humanlanguagetranslator.data;

import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WordJsonDefine {
    //about version
    public static final String VERSION_KEY = "version";
    public static final double defaultVersionValue = 1.0;

    private static double versionValue = defaultVersionValue;
    private static final String TAG = "WordJsonDefine";

    public static double getVersionValue() {
        return versionValue;
    }

    public static void setVersionValue(double versionValue) {
        WordJsonDefine.versionValue = versionValue;
    }

    //about type
    public enum wordType {
        UNDEFINE,
        UNVERIFIED,
        VERIFIED
    }

    public static String getExplainAnyKey(String anyKey) {
        return Explain.EXPLAIN_SYMBOL + anyKey;
    }

    public static void setExplainFromJson(JSONObject jsonObject, String explainKey) {
        try {
            String templateExplain = jsonObject.getString(WordJsonDefine.getExplainAnyKey(explainKey));
            WordJsonDefine.Explain.putExplainValue(explainKey, templateExplain);
        } catch (JSONException e) {
            Utils.outLog(TAG, "warning : " + explainKey + " is not fond");
        }
    }

    public static class Explain {
        // template explain
        public static final String EXPLAIN_SYMBOL = "//";
        public static final String TEMPLATE_KEY = "template";
        public static final String WORD_KEY = "word";
        public static final String SYNONYM_KEY = "synonym";
        public static final String TYPE_KEY = "type";
        public static final String TRANSLATION_KEY = "translation";
        public static final String EXAMPLE_KEY = "example";
        public static final String VERIFIED_INFO_KEY = "verified_info";
        public static final String VERIFIED_TIME_KEY = "verified_time";
        public static final String EARLIEST_ADDR_KEY = "earliest_addr";
        public static final String QUARRY_ADDR_KEY = "quarry_addr";
        public static final String OTHER_KEY = "other";
        public static final String AUTHOR_KEY = "author";
        public static final String RESTORERS_KEY = "restorers";

        // other explain
        public static final String OTHER_EXPLAIN_KEY = "other_explain";
        public static final String WORDS_KEY = "words";
        public static final String USE_VERSION_KEY = "use_version";

        private static final Map<String, String> sExplainMap = new HashMap<>();

        public static void putExplainValue(String key, String value) {
            if (null == key || null == value) {
                return;
            }
            sExplainMap.put(key, value);
        }

        @Nullable
        public static String getExplainValue(String key) {
            if (null == key) {
                return null;
            }
            return sExplainMap.get(key);
        }
    }

}
