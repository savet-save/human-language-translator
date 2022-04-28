package com.example.humanlanguagetranslator.data;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.helper.AssetsHelper;
import com.example.humanlanguagetranslator.helper.FileHelper;
import com.example.humanlanguagetranslator.helper.JsonHelper;
import com.example.humanlanguagetranslator.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class Dictionary {
    public static final int NOT_FOUND_POSITION = -1;
    public static final String DICTIONARY_ASSETS_PATH = "core/dictionary.json";
    public static final String DICTIONARY_FILE_NAME = "dictionary.json";

    private static final String TAG = "Dictionary";

    private static volatile Dictionary sDictionary;

    private static final Object WORD_CACHE_LOCK = new Object();
    private final List<Word> mWords;
    /**
     * for fast find word
     */
    private final Map<UUID, Integer> mWordPositionCache;

    private static final Object BITMAPS_CACHE_LOCK = new Object();
    private final Map<UUID, byte[]> mImageCache;
    private double mUseVersion = 1.0f;
    private List<String> mWordsNameList = null;

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
        mWordPositionCache = new HashMap<>();
        mImageCache = new HashMap<>();
    }

    public void init(Context context) {
        if (mWords.size() != 0) {
            return;
        }
        parseWordDefine(context);
        parseDictionary(context);
    }

    private void parseDictionary(Context context) {
        byte[] bytes = FileHelper.readFile(DICTIONARY_FILE_NAME, context, FileHelper.SaveDir.JSON_DATE);
        JSONObject defineJson = null;
        if (bytes != null) {
            Utils.logDebug(TAG, "try use file dictionary.json");
            defineJson = JsonHelper.parseBytes(bytes);
        }
        if (null == defineJson) {
            Utils.logDebug(TAG, "use assets dictionary.json");
            defineJson = AssetsHelper.getInstance().getJsonAssets(context, DICTIONARY_ASSETS_PATH);
        }
        if (null == defineJson) {
            Utils.logDebug(TAG, "warning : can't get dictionary json object");
            return;
        }
        try {
            mUseVersion = defineJson.getDouble(WordJsonDefine.Explain.USE_VERSION_KEY);
            JSONArray wordsName = defineJson.getJSONArray(WordJsonDefine.Explain.WORDS_KEY);
            mWordsNameList = JsonHelper.getStringArrayList(wordsName);
            for (int i = 0; i < wordsName.length(); i++) {
                Utils.logDebug(TAG, defineJson.toString());
                JSONArray jsonArray = defineJson.getJSONArray(wordsName.getString(i));
                for (int j = 0; j < jsonArray.length(); j++) {
                    addWord(parseWord(jsonArray.getJSONObject(j)), i);
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

            ArrayList<String> translation = JsonHelper.getArrayListWithString(wordJson,
                    WordJsonDefine.Explain.TRANSLATION_KEY);

            ArrayList<String> quarry = JsonHelper.getArrayListWithString(wordJson,
                    WordJsonDefine.Explain.QUARRY_KEY);

            ArrayList<String> example = JsonHelper.getArrayListWithString(wordJson,
                    WordJsonDefine.Explain.EXAMPLE_KEY);

            //verified info
            JSONObject verifiedInfoJson = wordJson.getJSONObject(WordJsonDefine.Explain.VERIFIED_INFO_KEY);
            Date verifiedDate = JsonHelper.getDate(verifiedInfoJson,
                    WordJsonDefine.Explain.VERIFIED_TIME_KEY);
            Date earliestDate = JsonHelper.getDate(verifiedInfoJson,
                    WordJsonDefine.Explain.EARLIEST_TIME_KEY);
            String earliestAddr = verifiedInfoJson.getString(WordJsonDefine.Explain.EARLIEST_ADDR_KEY);
            String other = verifiedInfoJson.getString(WordJsonDefine.Explain.OTHER_KEY);
            VerifiedInfo verifiedInfo = new VerifiedInfo(verifiedDate, earliestDate, earliestAddr,
                    other, verifiedDate != null, earliestDate != null);

            String author = wordJson.getString(WordJsonDefine.Explain.AUTHOR_KEY);

            ArrayList<String> restorers = JsonHelper.getArrayListWithString(wordJson,
                    WordJsonDefine.Explain.RESTORERS_KEY);

            String pictureLink = wordJson.getString(WordJsonDefine.Explain.PICTURE_LINK);

            return new Word(word, synonym, wordType, translation,
                    quarry, example, verifiedInfo, author, restorers, pictureLink);
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
        WordJsonDefine.setExplainFromJson(templateExplain, WordJsonDefine.Explain.PICTURE_LINK);
        try {
            JSONObject verifiedInfo = templateExplain.getJSONObject(
                    WordJsonDefine.getExplainAnyKey(WordJsonDefine.Explain.VERIFIED_INFO_KEY));
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.VERIFIED_INFO_KEY);
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.VERIFIED_TIME_KEY);
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.EARLIEST_TIME_KEY);
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.EARLIEST_ADDR_KEY);
            WordJsonDefine.setExplainFromJson(verifiedInfo, WordJsonDefine.Explain.OTHER_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * waring : this return result can't add/remove item
     *
     * @return word list cache
     */
    public List<Word> getWords() {
        return mWords;
    }

    /**
     * Find word by ID
     *
     * @param wordId word id
     * @return word, or null if not find
     */
    @Nullable
    public Word getWord(UUID wordId) {
        if (null == wordId) {
            return null;
        }
        int position = getPosition(wordId);
        if (NOT_FOUND_POSITION != position) {
            return mWords.get(position);
        }
        return null;
    }

    /**
     * get position
     *
     * @param wordId word id
     * @return find position, if not find return NOT_FOUND_POSITION
     */
    public int getPosition(UUID wordId) {
        Integer position = mWordPositionCache.get(wordId);
        if (null == position) {
            return NOT_FOUND_POSITION;
        }
        return position;
    }

    /**
     * add word to cache, mWordsNameList index default is 0, or update if id existing
     *
     * @param word added or update word, if is null, give up the operation
     */
    public void addWord(Word word) {
        this.addWord(word, word.getNameListIndex());
    }

    /**
     * add word to cache, or update if id existing
     *
     * @param word      added or update word, if is null, give up the operation
     * @param nameIndex mWordsNameList index
     */
    public void addWord(Word word, int nameIndex) {
        if (null == word) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return;
        }
        synchronized (WORD_CACHE_LOCK) {
            Integer position = mWordPositionCache.get(word.getId());
            if (null == position) {
                mWordPositionCache.put(word.getId(), mWords.size());
                mWords.add(word);
            } else {
                mWords.set(position, word);
            }
        }
        word.setNameListIndex(nameIndex);
    }

    /**
     * remove word from cache
     * @param word word
     * @return whether successfully removed
     */
    public boolean removeWord(Word word) {
        if (null == word) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return false;
        }
        synchronized (WORD_CACHE_LOCK) {
            Integer position = mWordPositionCache.get(word.getId());
            if (null == position) {
                Utils.logDebug(TAG, "not word's position cache, remove fail");
                return false;
            } else {
                mWords.remove((int) position);
            }
        }
        return true;
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

    public void putImageData(UUID uuid, byte[] imageData) {
        if (null == uuid || null == imageData) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return;
        }
        synchronized (BITMAPS_CACHE_LOCK) {
            mImageCache.put(uuid, imageData);
        }
    }

    @Nullable
    public byte[] getImageData(UUID uuid) {
        if (null == uuid) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return null;
        }
        synchronized (BITMAPS_CACHE_LOCK) {
            return mImageCache.get(uuid);
        }
    }

    @Nullable
    public List<String> getWordsNameList() {
        return mWordsNameList;
    }

    /**
     * get words name list size
     *
     * @return last 0, if not has list
     */
    public int getNameListSize() {
        if (null == mWordsNameList) {
            return 0;
        }
        return mWordsNameList.size();
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(WordJsonDefine.Explain.USE_VERSION_KEY, mUseVersion)
                    .put(WordJsonDefine.Explain.WORDS_KEY, JsonHelper.getJSONArrayFromStringList(mWordsNameList));
            List<List<Word>> lists = getOrganizeWordList();
            for (int i = 0; i < lists.size(); i++) { // lists size >= mWordsNameList size
                jsonObject.put(mWordsNameList.get(i), JsonHelper.getJSONArrayFromWordList(lists.get(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Create a List based on which Words the name belongs to
     *
     * @return a List
     */
    public List<List<Word>> getOrganizeWordList() {
        List<List<Word>> lists = new ArrayList<>();
        for (int i = 0; i < mWords.size(); i++) {
            Word checkedWord = mWords.get(i);
            Utils.logDebug(TAG, "checkedWord.getNameListIndex() : " + checkedWord.getNameListIndex());
            //check index out of bounds
            while (checkedWord.getNameListIndex() >= lists.size()) {
                lists.add(new LinkedList<>());
            }
            lists.get(checkedWord.getNameListIndex()).add(checkedWord);
        }
        return lists;
    }

    public String toJsonString() {
        return JsonHelper.getJsonString(toJSONObject());
    }
}
