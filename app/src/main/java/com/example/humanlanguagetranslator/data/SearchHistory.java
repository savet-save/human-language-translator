package com.example.humanlanguagetranslator.data;

import java.util.ArrayList;
import java.util.List;

public class SearchHistory {

    private List<String> mHistory;

    private static volatile SearchHistory sSearchHistory;

    public static SearchHistory getInstance() {
        if (sSearchHistory == null) {
            synchronized (Dictionary.class) {
                if (sSearchHistory == null) {
                    sSearchHistory = new SearchHistory();
                }
            }
        }
        return sSearchHistory;
    }

    private SearchHistory() {
        mHistory = new ArrayList<>();
        mHistory.add("123");
        mHistory.add("aaaaaa");
        mHistory.add("快快快");
        mHistory.add("q");
        mHistory.add("ddddddddddddd");
        mHistory.add("awwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
        for (int i = 0; i < 11; i++) {
            mHistory.add(+ i + "aqqqqqqqqqqqqqqqqqqqqqqqqwwwwwwwwwwwwwwwwwwwwwwwwwweeeeeeeeeeeeeeeeeeeeeeee");
        }

    }

    public List<String> getHistory() {
        return mHistory;
    }

    public int getSize() {
        return mHistory.size();
    }
}
