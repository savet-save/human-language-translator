package com.example.humanlanguagetranslator.data;

import androidx.annotation.Nullable;

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
