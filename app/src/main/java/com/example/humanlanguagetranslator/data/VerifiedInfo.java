package com.example.humanlanguagetranslator.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class VerifiedInfo implements Parcelable {
    private Date mVerifiedTime;
    private Date mEarliestTime;
    private String mEarliestAddr;
    private String mQuarryAddr;
    private String mOther;

    protected VerifiedInfo(Parcel in) {
        mVerifiedTime = (Date) in.readSerializable();
        mVerifiedTime = (Date) in.readSerializable();
        mEarliestAddr = in.readString();
        mQuarryAddr = in.readString();
        mOther = in.readString();
    }

    public static final Creator<VerifiedInfo> CREATOR = new Creator<VerifiedInfo>() {
        @Override
        public VerifiedInfo createFromParcel(Parcel in) {
            return new VerifiedInfo(in);
        }

        @Override
        public VerifiedInfo[] newArray(int size) {
            return new VerifiedInfo[size];
        }
    };

    public Date getVerifiedTime() {
        return mVerifiedTime;
    }

    public Date getEarliestTime() {
        return mEarliestTime;
    }

    public String getEarliestAddr() {
        return mEarliestAddr;
    }

    public String getQuarryAddr() {
        return mQuarryAddr;
    }

    public String getOther() {
        return mOther;
    }

    public void setVerifiedTime(Date verifiedTime) {
        mVerifiedTime = verifiedTime;
    }

    public void setEarliestTime(Date earliestTime) {
        mEarliestTime = earliestTime;
    }

    public void setEarliestAddr(String earliestAddr) {
        mEarliestAddr = earliestAddr;
    }

    public void setQuarryAddr(String quarryAddr) {
        mQuarryAddr = quarryAddr;
    }

    public void setOther(String other) {
        mOther = other;
    }

    public VerifiedInfo() {
        this(new Date(), new Date(), "", "", "");
    }

    public VerifiedInfo(Date verifiedTime, Date earliestTime, String earliestAddr, String quarryAddr, String other) {
        this.mVerifiedTime = verifiedTime;
        this.mEarliestTime = earliestTime;
        this.mEarliestAddr = earliestAddr;
        this.mQuarryAddr = quarryAddr;
        this.mOther = other;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mVerifiedTime);
        dest.writeSerializable(mEarliestTime);
        dest.writeString(mEarliestAddr);
        dest.writeString(mQuarryAddr);
        dest.writeString(mOther);
    }
}