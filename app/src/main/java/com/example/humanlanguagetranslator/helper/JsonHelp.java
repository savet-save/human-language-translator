package com.example.humanlanguagetranslator.helper;

import androidx.annotation.NonNull;

import com.example.humanlanguagetranslator.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class JsonHelp {
    private static final String TAG = "JsonHelp";

    /**
     * get ArrayList with String from JsonObject
     * @param jsonObject from jsonObject
     * @param arrayKey JSONArray key
     * @return ArrayList with String from JsonObject, if parameter is null or not find arrayKey
     * from jsonObject, return a empty ArrayList
     */
    @NonNull
    public static ArrayList<String> getArrayListWithString(JSONObject jsonObject, String arrayKey) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (null == jsonObject || null == arrayKey) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return arrayList;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(arrayKey);
            for (int i = 0; i < jsonArray.length(); i++) {
                String value = jsonArray.getString(i);
                if (!value.isEmpty()) {
                    arrayList.add(value);
                }
            }
        } catch (JSONException e) {
            Utils.outLog(TAG, "warning : can't get json array, key : " + arrayKey + "\n"
                    + "from : " + jsonObject);
        }
        return arrayList;
    }

    /**
     * get Date from jsonObject
     * @param jsonObject from jsonObject
     * @param dateKey Date json key
     * @return Date , if parameter is null , not find dateKey from jsonObject or String parse error
     * return a current time Date
     */
    @NonNull
    public static Date getDate(JSONObject jsonObject, String dateKey) {
        Date date = null;
        if (null == jsonObject || null == dateKey) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return new Date();
        }
        try {
            String dateString = jsonObject.getString(dateKey);
            if (!dateString.isEmpty()) {
                date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        if (null == date) {
            date = new Date();
        }
        return date;
    }
}
