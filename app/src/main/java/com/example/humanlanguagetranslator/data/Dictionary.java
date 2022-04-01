package com.example.humanlanguagetranslator.data;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.Utils;
import com.example.humanlanguagetranslator.helper.AssetsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Dictionary {
    public static final int NOT_FOUND_POSITION = -1;

    private static final String TAG = "WordLab";

    private static volatile Dictionary sDictionary;

    private final List<Word> mWords;

    public static Dictionary getInstance() {
        if (sDictionary == null) {
            synchronized (Dictionary.class) {
                if (sDictionary == null) {
                    sDictionary = new Dictionary();
                }
            }
        }
        return sDictionary;
    }

    private Dictionary() {
        mWords = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            Word word = new Word("test" + i);
            mWords.add(word);
        }
    }

    public void init(Context context) {
        JSONObject defineJson = AssetsHelper.getInstance().getJsonAssets(context, "core/define.json");
        if (null == defineJson) {
            Utils.logDebug(TAG, "warning : can't get define json object");
            return;
        }
        try {
            //version
            double version = defineJson.getDouble(WordJsonDefine.VERSION_KEY);
            WordJsonDefine.setVersionValue(version);

            //说明
            JSONObject templateExplain = defineJson.getJSONObject(
                    WordJsonDefine.getExplainAnyKey(WordJsonDefine.Explain.TEMPLATE_KEY));
            parseTemplateExplain(templateExplain);
            JSONObject otherExplain = defineJson.getJSONObject(
                    WordJsonDefine.getExplainAnyKey(WordJsonDefine.Explain.OTHER_EXPLAIN_KEY));
            parseOtherExplain(otherExplain);

            Utils.logDebug(TAG, "version :" + WordJsonDefine.getVersionValue() + "\n" +
                    "template : " + templateExplain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseOtherExplain(JSONObject otherExplain) {
        if (null == otherExplain) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_ERROR);
            return;
        }
        WordJsonDefine.setExplainFromJson(otherExplain, WordJsonDefine.Explain.OTHER_EXPLAIN_KEY);
        WordJsonDefine.setExplainFromJson(otherExplain, WordJsonDefine.Explain.WORDS_KEY);
        WordJsonDefine.setExplainFromJson(otherExplain, WordJsonDefine.Explain.USE_VERSION_KEY);
    }

    private void parseTemplateExplain(JSONObject templateExplain) {
        if (null == templateExplain) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_ERROR);
            return;
        }
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.TEMPLATE_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.WORD_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.SYNONYM_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.TYPE_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.TRANSLATION_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.EXAMPLE_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.AUTHOR_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.RESTORERS_KEY);
        try {
            JSONObject verifiedInfo = templateExplain.getJSONObject(
                    WordJsonDefine.getExplainAnyKey(WordJsonDefine.Explain.VERIFIED_INFO_KEY));
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.VERIFIED_INFO_KEY);
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.VERIFIED_TIME_KEY);
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.EARLIEST_ADDR_KEY);
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.QUARRY_ADDR_KEY);
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.OTHER_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Word> getWords() {
        return mWords;
    }

    @Nullable
    public Word getWord(UUID wordId) {
        if (null == wordId) {
            return null;
        }
        for (Word word : mWords) {
            if (word.getId().equals(wordId)) {
                return word;
            }
        }
        return null;
    }

    public int getPosition(UUID wordId) {
        if (null == wordId) {
            return NOT_FOUND_POSITION;
        }
        for (int i = 0; i < mWords.size(); i++) {
            if (mWords.get(i).getId().equals(wordId)) {
                return i;
            }
        }
        return NOT_FOUND_POSITION;
    }

    public void addWord(Word word) {
        if (null == word) {
            return;
        }
        mWords.add(word);
    }

    public void updateWord(Word word) {
        if (null == word) {
            return;
        }
        for (int i = 0; i < mWords.size(); i++) {
            if (mWords.get(i).getId().equals(word.getId())) {
                mWords.set(i, word);
            }
        }
    }

    public List<Word> getFilterResult(String filter) {
        List<Word> result = new ArrayList<>();
        final String regex = ".*" + filter + ".*";//if contains
        for (Word word : mWords) {
            if (Pattern.matches(regex, word.getTitle())) {
                result.add(word);
            }
        }
        return result;
    }
}
