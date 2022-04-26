package com.example.humanlanguagetranslator.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.humanlanguagetranslator.helper.JsonHelper;
import com.example.humanlanguagetranslator.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class VerifiedInfo implements Parcelable {
    public final static int VERIFIED_TIME_BIT = 0;
    public final static int EARLIEST_TIME_BIT = 1;
    private Date mVerifiedTime;
    private Date mEarliestTime;
    private String mEarliestAddr;
    private String mOther;
    /**
     * <pre>
     * Verified Time valid bit : 0
     * Earliest Time valid bit : 1
     * </pre>
     */
    private int mValidFlag;


    protected VerifiedInfo(Parcel in) {
        mVerifiedTime = (Date) in.readSerializable();
        mVerifiedTime = (Date) in.readSerializable();
        mEarliestAddr = in.readString();
        mOther = in.readString();
        mValidFlag = in.readInt();
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

    @NonNull
    public Date getVerifiedTime() {
        return mVerifiedTime;
    }

    @NonNull
    public Date getEarliestTime() {
        return mEarliestTime;
    }

    public String getEarliestAddr() {
        return mEarliestAddr;
    }

    public String getOther() {
        return mOther;
    }

    /**
     * set verified time and set data is valid, if not null
     * @param verifiedTime verified time
     */
    public void setVerifiedTime(Date verifiedTime) {
        if (null == verifiedTime) {
            return;
        }
        mVerifiedTime = verifiedTime;
        setVerifiedTimeValid(true);
    }

    /**
     * set earliest time and set data is valid, if not null
     * @param earliestTime earliest time
     */
    public void setEarliestTime(Date earliestTime) {
        if (null == earliestTime) {
            return;
        }
        mEarliestTime = earliestTime;
        setEarliestTimeValid(true);
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
        this(new Date(), new Date(), "", "", false, false);
    }

    public VerifiedInfo(Date verifiedTime, Date earliestTime, String earliestAddr, String other,
                        boolean verifiedTimeValid, boolean earliestTimeValid) {
        mVerifiedTime = (verifiedTime == null ? new Date() : verifiedTime); // for no null
        mEarliestTime = (earliestTime == null ? new Date() : earliestTime); // for no null
        mEarliestAddr = earliestAddr;
        mOther = other;
        mValidFlag = 0;
        Utils.logDebug("verifiedTimeValid : " + verifiedTimeValid + " earliestTimeValid : " + earliestTimeValid);
        setVerifiedTimeValid(verifiedTimeValid);
        setEarliestTimeValid(earliestTimeValid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean getValid(int bit) {
        return (mValidFlag & (0x1 << bit)) != 0;
    }

    private void setValid(boolean isValid, int bit) {
        if (bit < 0 || bit > 32) {
            return; // out of range
        }
        if (isValid) {
            mValidFlag |=  (0x1 << bit); //set bit 1
        } else {
            mValidFlag &= ~(0x1 << bit); //clean bit
        }
    }

    public boolean getVerifiedTimeValid() {
        return getValid(VERIFIED_TIME_BIT);
    }

    public boolean getEarliestTimeValid() {
        return getValid(EARLIEST_TIME_BIT);
    }

    private void setVerifiedTimeValid(boolean isValid) {
        setValid(isValid, VERIFIED_TIME_BIT);
    }

    private void setEarliestTimeValid(boolean isValid) {
        setValid(isValid, EARLIEST_TIME_BIT);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mVerifiedTime);
        dest.writeSerializable(mEarliestTime);
        dest.writeString(mEarliestAddr);
        dest.writeString(mOther);
        dest.writeInt(mValidFlag);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(WordJsonDefine.Explain.VERIFIED_TIME_KEY, getVerifiedTimeValid() ? JsonHelper.getFormatDate(mVerifiedTime) : "")
                    .put(WordJsonDefine.Explain.EARLIEST_TIME_KEY, getEarliestTimeValid() ? JsonHelper.getFormatDate(mEarliestTime) : "")
                    .put(WordJsonDefine.Explain.EARLIEST_ADDR_KEY, mEarliestAddr)
                    .put(WordJsonDefine.Explain.OTHER_KEY, mOther);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String toJsonString() {
        return JsonHelper.getJsonString(toJSONObject());
    }
}