package com.example.humanlanguagetranslator.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

public class Word implements Parcelable {
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

    //Parcelable reader
    protected Word(Parcel in) {
        mId = (UUID) in.readSerializable();
        mTitle = in.readString();
        mFirstDate = (Date) in.readSerializable();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    //Parcelable writer
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mId);
        dest.writeString(mTitle);
        dest.writeSerializable(mFirstDate);
    }
}
