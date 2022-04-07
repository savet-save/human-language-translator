package com.example.humanlanguagetranslator.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.Utils;
import com.example.humanlanguagetranslator.helper.AssetsHelper;
import com.example.humanlanguagetranslator.helper.JsonHelp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Dictionary {
    public static final int NOT_FOUND_POSITION = -1;
    public static final String DICTIONARY_PATH = "core/dictionary.json";

    private static final String TAG = "Dictionary";

    private static volatile Dictionary sDictionary;

    private static final Object wordCacheLock = new Object();
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
    }

    public void init(Context context) {
        if (mWords.size() != 0) {
            return;
        }
        parseWordDefine(context);
        parseDictionary(context);
    }

    private void parseDictionary(Context context) {
        JSONObject defineJson = AssetsHelper.getInstance().getJsonAssets(context, DICTIONARY_PATH);
        if (null == defineJson) {
            Utils.logDebug(TAG, "warning : can't get dictionary json object");
            return;
        }
        try {
            double useVersion = defineJson.getDouble(WordJsonDefine.Explain.USE_VERSION_KEY);
            JSONArray wordsName = defineJson.getJSONArray(WordJsonDefine.Explain.WORDS_KEY);
            for (int i = 0; i < wordsName.length(); i++) {
                Utils.logDebug(TAG, defineJson.toString());
                JSONArray jsonArray = defineJson.getJSONArray(wordsName.getString(i));
                for (int j = 0; j < jsonArray.length(); j++) {
                    addWord(parseWord(jsonArray.getJSONObject(j)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private Word parseWord(JSONObject wordJson) {
        try {
            Utils.logDebug(TAG, wordJson.toString());
            String word = wordJson.getString(WordJsonDefine.Explain.WORD_KEY);

            String synonym = wordJson.getString(WordJsonDefine.Explain.SYNONYM_KEY);

            String typeString = wordJson.getString(WordJsonDefine.Explain.TYPE_KEY);
            WordJsonDefine.WordType wordType = WordJsonDefine.WordType.getWordType(typeString);

            ArrayList<String> translation = JsonHelp.getArrayListWithString(wordJson,
                    WordJsonDefine.Explain.TRANSLATION_KEY);

            ArrayList<String> quarry = JsonHelp.getArrayListWithString(wordJson,
                    WordJsonDefine.Explain.QUARRY_KEY);

            ArrayList<String> example = JsonHelp.getArrayListWithString(wordJson,
                    WordJsonDefine.Explain.EXAMPLE_KEY);

            //verified info
            JSONObject verifiedInfoJson = wordJson.getJSONObject(WordJsonDefine.Explain.VERIFIED_INFO_KEY);
            Date verifiedDate = JsonHelp.getDate(verifiedInfoJson,
                    WordJsonDefine.Explain.VERIFIED_TIME_KEY);
            Date earliestDate = JsonHelp.getDate(verifiedInfoJson,
                    WordJsonDefine.Explain.EARLIEST_TIME_KEY);
            String earliestAddr = verifiedInfoJson.getString(WordJsonDefine.Explain.EARLIEST_ADDR_KEY);
            String quarryAddr = verifiedInfoJson.getString(WordJsonDefine.Explain.QUARRY_ADDR_KEY);
            String other = verifiedInfoJson.getString(WordJsonDefine.Explain.OTHER_KEY);
            VerifiedInfo verifiedInfo = new VerifiedInfo(verifiedDate, earliestDate, earliestAddr, quarryAddr, other);

            String author = wordJson.getString(WordJsonDefine.Explain.AUTHOR_KEY);

            ArrayList<String> restorers = JsonHelp.getArrayListWithString(wordJson,
                    WordJsonDefine.Explain.RESTORERS_KEY);

            return new Word(word, synonym, wordType, translation,
                    quarry, example, verifiedInfo, author, restorers);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseWordDefine(Context context) {
        JSONObject defineJson = AssetsHelper.getInstance().getJsonAssets(context, WordJsonDefine.DEFINE_PATH);
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
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_ERROR);
            return;
        }
        WordJsonDefine.setExplainFromJson(otherExplain, WordJsonDefine.Explain.OTHER_EXPLAIN_KEY);
        WordJsonDefine.setExplainFromJson(otherExplain, WordJsonDefine.Explain.WORDS_KEY);
        WordJsonDefine.setExplainFromJson(otherExplain, WordJsonDefine.Explain.USE_VERSION_KEY);
    }

    private void parseTemplateExplain(JSONObject templateExplain) {
        if (null == templateExplain) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_ERROR);
            return;
        }
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.TEMPLATE_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.WORD_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.SYNONYM_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.TYPE_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.TRANSLATION_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.QUARRY_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.EXAMPLE_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.AUTHOR_KEY);
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.RESTORERS_KEY);
        try {
            JSONObject verifiedInfo = templateExplain.getJSONObject(
                    WordJsonDefine.getExplainAnyKey(WordJsonDefine.Explain.VERIFIED_INFO_KEY));
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.VERIFIED_INFO_KEY);
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.VERIFIED_TIME_KEY);
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.EARLIEST_TIME_KEY);
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

    /**
     * add word to cache
     * @param word added word
     */
    public void addWord(@Nullable Word word) {
        if (null == word) {
            return;
        }
        synchronized (wordCacheLock) {
            mWords.add(word);
        }
    }

    public void updateWord(Word word) {
        if (null == word) {
            return;
        }
        for (int i = 0; i < mWords.size(); i++) {
            if (mWords.get(i).getId().equals(word.getId())) {
                synchronized (wordCacheLock) {
                    mWords.set(i, word);
                }
            }
        }
    }

    public List<Word> getFilterResult(String filter) {
        List<Word> result = new ArrayList<>();
        final String regex = ".*" + filter + ".*";//if contains
        for (Word word : mWords) {
            if (Pattern.matches(regex, word.getContent())) {
                result.add(word);
            }
        }
        return result;
    }
}
