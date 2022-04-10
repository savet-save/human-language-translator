package com.example.humanlanguagetranslator.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Word implements Parcelable {
    private static final String SYNONYM_SPLIT_REGEX = "%";
    private static final String SYNONYM_DEFAULT_FORMAT = " ";

    private final UUID mId;
    private String mContent;
    private String mSynonym;
    private WordJsonDefine.WordType mWordType;
    private ArrayList<String> mTranslations;
    private ArrayList<String> mQuarries;
    private ArrayList<String> mExamples;
    private VerifiedInfo mVerifiedInfo;
    private String mAuthor;
    private ArrayList<String> mRestorers;
    private String mPictureLink;


    public Word() {
        this("",
                "",
                WordJsonDefine.WordType.UNDEFINE,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new VerifiedInfo(),
                "",
                new ArrayList<>(),
                "");
    }

    public Word(String word,
                String synonym,
                WordJsonDefine.WordType wordType,
                ArrayList<String> translation,
                ArrayList<String> quarry,
                ArrayList<String> example,
                VerifiedInfo verifiedInfo,
                String author,
                ArrayList<String> restorers,
                String pictureExternalLink) {
        mId = UUID.randomUUID();
        mContent = word;
        mSynonym = synonym;
        mWordType = wordType;
        mTranslations = translation;
        mQuarries = quarry;
        mExamples = example;
        mVerifiedInfo = verifiedInfo;
        mAuthor = author;
        mRestorers = restorers;
        mPictureLink = pictureExternalLink;
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

    public ArrayList<String> getTranslations() {
        return mTranslations;
    }

    public void setTranslations(ArrayList<String> translations) {
        if (null != translations) {
            mTranslations = translations;
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
        mSynonym = in.readString();
        mWordType = (WordJsonDefine.WordType) in.readSerializable();
        mTranslations = new ArrayList<>();
        in.readList(mTranslations, List.class.getClassLoader());
        mQuarries = new ArrayList<>();
        in.readList(mQuarries, List.class.getClassLoader());
        mExamples = new ArrayList<>();
        in.readList(mExamples, List.class.getClassLoader());
        mVerifiedInfo = in.readParcelable(VerifiedInfo.class.getClassLoader());
        mAuthor = in.readString();
        mRestorers = new ArrayList<>();
        in.readList(mRestorers, List.class.getClassLoader());
        mPictureLink = in.readString();
    }

    @Override
    //Parcelable writer
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mId);
        dest.writeString(mContent);
        dest.writeString(mSynonym);
        dest.writeSerializable(mWordType);
        dest.writeList(mTranslations);
        dest.writeList(mQuarries);
        dest.writeList(mExamples);
        dest.writeParcelable(mVerifiedInfo, flags);
        dest.writeString(mAuthor);
        dest.writeList(mRestorers);
        dest.writeString(mPictureLink);
    }

    public String getSynonym() {
        return mSynonym;
    }

    /**
     *  get Format Synonym
     * @param format default blank space
     * @return
     *  <p> example : format is ',' </p>
     *  <p> Synonym1,Synonym2,Synonym3 </p>
     */
    public String getFormatSynonym(@Nullable String format) {
        if (null == format) {
            format = SYNONYM_DEFAULT_FORMAT;
        }
        StringBuilder builder = new StringBuilder();
        String[] synonym = mSynonym.split(SYNONYM_SPLIT_REGEX);
        for (int i = 0; i <synonym.length; i++) {
            builder.append(synonym[i]);
            if (i != synonym.length - 1) {
                builder.append(format);
            }
        }
        return builder.toString();
    }

    /**
     * set Synonym
     * @param formatSynonym use '%' joint synonym, example : Synonym1%Synonym2%
     */
    public void setSynonym(String formatSynonym) {
        if (null != formatSynonym) {
            mSynonym = formatSynonym;
        }
    }

    public WordJsonDefine.WordType getWordType() {
        return mWordType;
    }

    public void setWordType(WordJsonDefine.WordType wordType) {
        mWordType = wordType;
    }

    public ArrayList<String> getQuarries() {
        return mQuarries;
    }

    public void setQuarries(ArrayList<String> quarries) {
        if (null != quarries) {
            mQuarries = quarries;
        }
    }

    public ArrayList<String> getExamples() {
        return mExamples;
    }

    public void setExamples(ArrayList<String> examples) {
        if (null != examples) {
            mExamples = examples;
        }
    }

    public VerifiedInfo getVerifiedInfo() {
        return mVerifiedInfo;
    }

    public void setVerifiedInfo(VerifiedInfo verifiedInfo) {
        if (null != verifiedInfo) {
            mVerifiedInfo = verifiedInfo;
        }
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        if (null != author) {
            mAuthor = author;
        }
    }

    public ArrayList<String> getRestorers() {
        return mRestorers;
    }

    public void setRestorers(ArrayList<String> restorers) {
        if (null != restorers) {
            mRestorers = restorers;
        }
    }

    public String getPictureLink() {
        return mPictureLink;
    }

    public void setPictureLink(String pictureLink) {
        if (null != pictureLink) {
            mPictureLink = pictureLink;
        }
    }
}
