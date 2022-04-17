package com.example.humanlanguagetranslator.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.example.humanlanguagetranslator.util.GlobalHandler;
import com.example.humanlanguagetranslator.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchHistory {

    private static final String QUERY_HISTORY = "QUERY_HISTORY";
    private static final String SPLIT_REGEX = "&";
    private static final String TAG = "SearchHistory";

    private List<String> mHistory;
    private final SharedPreferences mSharedPreferences;

    private static volatile SearchHistory sSearchHistory;

    public static SearchHistory getInstance(Context context) {
        if (sSearchHistory == null) {
            synchronized (SearchHistory.class) {
                if (sSearchHistory == null) {
                    sSearchHistory = new SearchHistory(context);
                }
            }
        }
        return sSearchHistory;
    }

    /**
     * read stored query info from disk
     * @return if has info, return it, if not return empty ArrayList
     */
    @NonNull
    private List<String> readStoredQueryInfo() {
        if (null == mSharedPreferences) {
            Utils.outLog(TAG, "error : need instantiation mSharedPreferences, can't read");
            return new ArrayList<>();
        }
        String historyCollection = mSharedPreferences.getString(QUERY_HISTORY, null);
        if (null == historyCollection) {
            Utils.outLog(TAG, "error : can't get historyCollection from mSharedPreferences");
            return new ArrayList<>();
        }
        String[] historyArray = historyCollection.split(SPLIT_REGEX);
        return new ArrayList<>(Arrays.asList(historyArray));
    }

    /**
     * write stored query info to disk
     */
    private void writeStoredQueryInfo() {
        if (null == mSharedPreferences) {
            Utils.outLog(TAG, "error : need instantiation mSharedPreferences, can't save");
            return;
        }
        StringBuilder historyCollection = new StringBuilder();
        synchronized (SearchHistory.class) {
            for (String info : mHistory) {
                historyCollection.append(info)
                        .append(SPLIT_REGEX);
            }
        }
        mSharedPreferences.edit()
                .putString(QUERY_HISTORY, historyCollection.toString())
                .apply();
    }

    private SearchHistory(Context context) {
        if (null == context) {
            Utils.outLog(TAG, "warning : parameter 'context' is null, can't get history info from disk");
            mHistory = new ArrayList<>();
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mHistory = readStoredQueryInfo();
    }

    public List<String> getHistory() {
        return mHistory;
    }

    /**
     * save history info
     * @param info history info
     */
    public void putHistory(String info) {
        if (null == info) {
            Utils.outLog(TAG, "warning : put history info is null");
            return;
        }
        synchronized (SearchHistory.class) {
            for (String history : mHistory) {
                if (history.equals(info)) {
                    return;
                }
            }
            mHistory.add(info);
        }
        GlobalHandler.getInstance().post2BackgroundHandler(new Runnable() {
            @Override
            public void run() {
                writeStoredQueryInfo();
            }
        });
    }

    /**
     * clean all history info
     */
    public void cleanHistory() {
        synchronized (SearchHistory.class) {
            mHistory.clear();
        }
        GlobalHandler.getInstance().post2BackgroundHandler(new Runnable() {
            @Override
            public void run() {
                if (mSharedPreferences == null) {
                    return;
                }
                mSharedPreferences.edit()
                        .clear()
                        .apply();
            }
        });
    }

    public int getSize() {
        return mHistory.size();
    }
}
