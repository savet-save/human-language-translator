package com.example.humanlanguagetranslator.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class VerifiedInfo implements Parcelable {
    private final static int VALID_VALUE = 1;
    private final static int INVALID_VALUE = 1;
    private Date mVerifiedTime;
    private Date mEarliestTime;
    private String mEarliestAddr;
    private String mOther;
    private int isValidFlag;

    protected VerifiedInfo(Parcel in) {
        mVerifiedTime = (Date) in.readSerializable();
        mVerifiedTime = (Date) in.readSerializable();
        mEarliestAddr = in.readString();
        mOther = in.readString();
        isValidFlag = in.readInt();
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

    public String getOther() {
        return mOther;
    }

    public void setVerifiedTime(Date verifiedTime) {
        if (null != verifiedTime) {
            mVerifiedTime = verifiedTime;
        }
    }

    public void setEarliestTime(Date earliestTime) {
        if (null != earliestTime) {
            mEarliestTime = earliestTime;
        }
    }

    public void setEarliestAddr(String earliestAddr) {
        if (null != earliestAddr) {
            mEarliestAddr = earliestAddr;
        }
    }


    public void setOther(String other) {
        mOther = other;
    }

    public VerifiedInfo() {
        this(new Date(), new Date(), "",  "", false);
    }

    public VerifiedInfo(Date verifiedTime, Date earliestTime, String earliestAddr, String other, boolean valid) {
        this.mVerifiedTime = verifiedTime;
        this.mEarliestTime = earliestTime;
        this.mEarliestAddr = earliestAddr;
        this.mOther = other;
        this.isValidFlag = (valid ? VALID_VALUE : INVALID_VALUE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isValid() {
        return (this.isValidFlag == INVALID_VALUE);
    }

    public void setValid(boolean valid) {
        this.isValidFlag = (valid ? VALID_VALUE : INVALID_VALUE);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mVerifiedTime);
        dest.writeSerializable(mEarliestTime);
        dest.writeString(mEarliestAddr);
        dest.writeString(mOther);
        dest.writeInt(isValidFlag);
    }
}