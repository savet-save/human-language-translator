package com.example.humanlanguagetranslator.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Word implements Parcelable {
    private final UUID mId;
    private String mContent;
    private WordJsonDefine.WordType mWordType;
    private ArrayList<String> mTranslation;
    private ArrayList<String> mQuarry;
    private ArrayList<String> mExample;
    private VerifiedInfo mVerifiedInfo;
    private String mAuthor;
    private ArrayList<String> mRestorers;

    public Word() {
        this("",
                WordJsonDefine.WordType.UNDEFINE,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new VerifiedInfo(),
                "",
                new ArrayList<>());
    }

    public Word(String word,
                WordJsonDefine.WordType wordType,
                ArrayList<String> translation,
                ArrayList<String> quarry,
                ArrayList<String> example,
                VerifiedInfo verifiedInfo,
                String author,
                ArrayList<String> restorers) {
        mId = UUID.randomUUID();
        mContent = word;
        mWordType = wordType;
        mTranslation = translation;
        mQuarry = quarry;
        mExample = example;
        mVerifiedInfo = verifiedInfo;
        mAuthor = author;
        mRestorers = restorers;
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

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        if (null != content) {
            mContent = content;
        }
    }

    public Date getFirstDate() {
        return mVerifiedInfo.getEarliestTime();
    }

    public void setFirstDate(Date firstDate) {
        if (null != firstDate) {
            mVerifiedInfo.setEarliestTime(firstDate);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Parcelable reader
    protected Word(Parcel in) {
        mId = (UUID) in.readSerializable();
        mContent = in.readString();
        mWordType = (WordJsonDefine.WordType) in.readSerializable();
        mTranslation = new ArrayList<>();
        in.readList(mTranslation, List.class.getClassLoader());
        mQuarry = new ArrayList<>();
        in.readList(mQuarry, List.class.getClassLoader());
        mExample = new ArrayList<>();
        in.readList(mExample, List.class.getClassLoader());
        mVerifiedInfo = in.readParcelable(VerifiedInfo.class.getClassLoader());
        mAuthor = in.readString();
        mRestorers = new ArrayList<>();
        in.readList(mRestorers, List.class.getClassLoader());
    }

    @Override
    //Parcelable writer
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mId);
        dest.writeString(mContent);
        dest.writeSerializable(mWordType);
        dest.writeList(mTranslation);
        dest.writeList(mQuarry);
        dest.writeList(mExample);
        dest.writeParcelable(mVerifiedInfo, flags);
        dest.writeString(mAuthor);
        dest.writeList(mRestorers);
    }
}
