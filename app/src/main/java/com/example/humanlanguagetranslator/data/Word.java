package com.example.humanlanguagetranslator.data;

import java.util.Date;
import java.util.UUID;

public class Word {
    private final UUID mId;
    private String mTitle;
    private Date mFirstDate;

    public Word() {
        this("");
    }

    public Word(String content) {
        mId = UUID.randomUUID();
        mTitle = content;
        mFirstDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        if (null != title) {
            mTitle = title;
        }
    }

    public Date getFirstDate() {
        return mFirstDate;
    }

    public void setFirstDate(Date firstDate) {
        if (null != firstDate) {
            mFirstDate = firstDate;
        }
    }
}
