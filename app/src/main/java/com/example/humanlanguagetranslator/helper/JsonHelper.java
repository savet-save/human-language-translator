package com.example.humanlanguagetranslator.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.data.Word;
import com.example.humanlanguagetranslator.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class JsonHelper {
    private static final String TAG = "JsonHelp";
    public static final String DATE_DEFAULT_PATTERN = "yyyy-MM-dd";
    public static final int JSON_DEFAULT_INDENT_SPACES = 2;

    /**
     * get ArrayList with String from JsonObject
     *
     * @param jsonObject from jsonObject
     * @param arrayKey   JSONArray key
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
     *
     * @param jsonObject from jsonObject
     * @param dateKey    Date json key
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
                date = new SimpleDateFormat(DATE_DEFAULT_PATTERN, Locale.getDefault()).parse(dateString);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        if (null == date) {
            date = new Date();
        }
        return date;
    }

    /**
     * get format date String
     *
     * @param date date
     * @return format date String, or null if param is null
     */
    @Nullable
    public static String getFormatDate(Date date) {
        if (null == date) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_DEFAULT_PATTERN, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    /**
     * get a JSONArray
     *
     * @param list String list
     * @return <p> JSONArray </p>
     * <p> if param list is null : empty JSONArray </p>
     * <p> if param list is empty : has a empty String JSONArray </p>
     * <p> other : the JSONArray </p>
     */
    @NonNull
    public static JSONArray getJSONArrayFromStringList(List<String> list) {
        JSONArray jsonArray = new JSONArray();
        if (null == list) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return jsonArray;
        }
        if (list.isEmpty()) {
            list.add("");
        }
        for (String s : list) {
            jsonArray.put(s);
        }
        return jsonArray;
    }

    /**
     * get ArrayList whit String
     *
     * @param jsonArray jsonArray
     * @return ArrayList
     * <p> if param jsonArray is null : a empty ArrayList </p>
     * <p> other : the ArrayList </p>
     */
    @NonNull
    public static ArrayList<String> getStringArrayList(JSONArray jsonArray) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (null == jsonArray) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return arrayList;
        }
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                String value = null;
                value = jsonArray.getString(i);
                if (!value.isEmpty()) {
                    arrayList.add(value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * get a JSONArray
     *
     * @param list list
     * @return ArrayList
     * <p> if param list is null : a empty JSONArray </p>
     * <p> other : the JSONArray </p>
     */
    @NonNull
    public static JSONArray getJSONArrayFromWordList(List<Word> list) {
        JSONArray jsonArray = new JSONArray();
        if (null == list) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return jsonArray;
        }
        for (Word s : list) {
            jsonArray.put(s.toJSONObject());
        }
        return jsonArray;
    }

    @NonNull
    public static String getJsonString(JSONObject jsonObject) {
        String string = null;
        try {
            string = jsonObject.toString(JsonHelper.JSON_DEFAULT_INDENT_SPACES)
                    .replace("\\/", "/"); // not need '\/'
        } catch (JSONException e) {
            e.printStackTrace();
            string = "";
        }
        return string;
    }

    @Nullable
    public static JSONObject parseBytes(byte[] data) {
        try {
            return new JSONObject(new String(data));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
