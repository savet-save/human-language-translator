package com.example.humanlanguagetranslator.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WordJsonDefine {
    public static final String DEFINE_PATH = "core/define.json";
    // about version
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
    public enum WordType {
        UNDEFINE("undefine"),
        UNVERIFIED("unverified"),
        VERIFIED("verified");
        private final String mName;
        private static Map<String, WordType> mCache = null;
        WordType(String name) {
            this.mName = name;
        }

        public String getName() {
            return mName;
        }

        /**
         * Convert the string to WordType
         * @param name Convert the string
         * @return <p> success : Convert WordType </p>
         *         <p> fail : UNDEFINE </p>
         */
        @NonNull
        public static WordType getWordType(String name) {
            if (null == name) {
                Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                return UNDEFINE;
            }
            //init cache
            if (null == mCache) {
                mCache = new HashMap<>();
                for (WordType wordType : WordType.values()) {
                    mCache.put(wordType.getName(), wordType);
                }
            }
            //get type
            WordType wordType = mCache.get(name.toLowerCase(Locale.ROOT));
            if (null == wordType) {
                wordType = WordType.UNDEFINE;
                Utils.outLog(TAG, "getWordType fail, return default :" + UNDEFINE.getName());
            }
            return wordType;
        }
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
        public static final String QUARRY_KEY = "quarry";
        public static final String EXAMPLE_KEY = "example";

        public static final String VERIFIED_INFO_KEY = "verified_info";
        public static final String VERIFIED_TIME_KEY = "verified_time";
        public static final String EARLIEST_TIME_KEY = "earliest_time";
        public static final String EARLIEST_ADDR_KEY = "earliest_addr";
        public static final String OTHER_KEY = "other";

        public static final String AUTHOR_KEY = "author";
        public static final String RESTORERS_KEY = "restorers";
        public static final String PICTURE_LINK = "picture_link";

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
