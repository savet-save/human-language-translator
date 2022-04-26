package com.example.humanlanguagetranslator.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.humanlanguagetranslator.ActivityManager;
import com.example.humanlanguagetranslator.BuildConfig;
import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.activity.WordListActivity;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class Utils {
    /**
     * word list menu id
     */
    public static final int ID_ADD_WORD = R.id.menu_item_add_word;
    public static final int ID_SEARCH_WORD = R.id.menu_item_search_word;
    public static final int ID_MENU_ABOUT = R.id.menu_item_about;

    /**
     * word menu id
     */
    public static final int ID_MENU_SAVE = R.id.menu_item_save;
    public static final int ID_MENU_BUILD = R.id.menu_item_build;

    public static final int MAX_LONG_SHOW_LENGTH = String.valueOf(Long.MAX_VALUE).length();

    private static long sSequenceNumber = 0;
    private static final Object SEQUENCE_NUMBER_LOCK = new Object();

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    private static final String TAG = "Utils";

    public enum OutLogType {
        PARAMETER_NULL_ERROR,
        PARAMETER_NULL_WARNING,
        PARAMETER_STRING_NULL_OR_EMPTY
    }

    public static void logDebug(String msg) {
        if (isDebug()) {
            Log.d(TAG, msg);
        }
    }

    public static void logDebug(String tag, String msg) {
        if (isDebug()) {
            Log.d(tag, msg);
        }
    }

    public static void outLog(String msg) {
        Log.d(TAG, msg);
    }

    public static void outLog(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void outLog(String tag, @NonNull OutLogType type) {
        String builder = getCallStack();
        switch (type) {
            case PARAMETER_NULL_ERROR:
                Log.e(tag, "Error : parameter is null, call stack - " + builder);
                break;
            case PARAMETER_NULL_WARNING:
                Log.d(tag, "Waring : parameter is null, call stack - " + builder);
                break;
            case PARAMETER_STRING_NULL_OR_EMPTY:
                Log.d(tag, "Waring : parameter string is null or empty, call stack - " + builder);
                break;
            default:
                Log.d(tag, "Waring : need implementation type : " + type);
                break;
        }
    }

    public static String getCallStack() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        for (int i = 3; i < stackTrace.length; i++) {
            builder.append(stackTrace[i].getMethodName());
            if (i != stackTrace.length - 1) {
                builder.append("() <- ");
            }
        }
        return builder.toString();
    }

    public static boolean isDualPane() {
        String activityName = WordListActivity.class.getSimpleName();
        AppCompatActivity wordListActivity = ActivityManager.findActivity(activityName);
        if (null == wordListActivity) {
            Utils.logDebug(TAG, "isDualPane fail : not find " + activityName +" in ActivityManager");
            return false;
        }
        View viewById = wordListActivity.findViewById(R.id.detail_fragment_container);
        return (null == viewById);
    }

    public static int getWithDp() {
        //获取屏幕宽高
        final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        return px2dp(screenWidth);
    }

    public static int getHeightDp() {
        final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        return px2dp(screenHeight);
    }

    /**
     * Change from px(pixels) to dp based on phone's resolution
     *
     * @param pxValue pixels value
     * @return dp value
     */
    public static int px2dp(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Change from dp to px based on phone's resolution
     *
     * @param dpValue pixels value
     * @return pixels value
     */
    public static int dp2px(float dpValue) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, Resources.getSystem().getDisplayMetrics());
        return (int) (px + 0.5f);
    }

    /**
     * <p> get string whit format from list </p>
     * <p> example : </p>
     * <p> 1. this is ..... </p>
     * <p> 2. this is ..... </p>
     * <p> or : </p>
     * <p> &nbsp;&nbsp;this is .... </p>
     *
     * @return format translation
     */
    @NonNull
    public static String getFormatString(@Nullable List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        if (null == list) {
            return stringBuilder.toString();
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isEmpty()) {
                continue;
            }
            if (list.size() == 1) {
                stringBuilder.append("  ");
            } else {
                stringBuilder.append(i + 1).append(". ");
            }
            stringBuilder.append(list.get(i));
            if (i != (list.size() - 1)) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Checks if the string is empty
     *
     * @param s string
     * @return <p> true : s is null or empty </p>
     * <p> false : not is empty </p>
     */
    public static boolean isEmptyString(String s) {
        return (null == s || s.isEmpty());
    }

    /**
     * Checks if the string list is empty
     *
     * @param list string list
     * @return <p> true : list is null or empty or first element is empty</p>
     * <p> false : not is empty </p>
     */
    public static boolean isEmptyStringList(List<String> list) {
        return null == list || list.isEmpty() || list.get(0).isEmpty();
    }

    public static void enableStrictMode(boolean isCloseApp) {
        if (!isDebug()) {
            return;
        }
        if (isCloseApp) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                    .penaltyDeath().build());
        } else {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                    .build());
        }
    }

    public static String getSequenceAddNumber(int stringLength) {
        StringBuilder builder = new StringBuilder();
        synchronized (SEQUENCE_NUMBER_LOCK) {
            String s = String.valueOf(sSequenceNumber);
            int addNumber = stringLength - s.length();
            while (addNumber > 0) {
                builder.append('0');
                addNumber--;
            }
            sSequenceNumber++;
            if (sSequenceNumber < 0) {
                sSequenceNumber = 0; //keep a positive number
            }
            builder.append(s);
        }
        return builder.toString();
    }

    /**
     * <p> get a not repeat id </p>
     * <p> like : 4e664030-4bc5-40dc-8863-6b02400bec64-0000000000000000011</p>
     *
     * @return a not repeat id string
     */
    public static String getNotRepeatId() {
        return UUID.randomUUID().toString() + "-" + Utils.getSequenceAddNumber(Utils.MAX_LONG_SHOW_LENGTH);
    }

    public static void removeEmptyItem(List<String> list) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.isEmpty()) {
                iterator.remove();
            }
        }
    }

    /**
     * judge is Chinese environment
     * @param context context
     * @return is or not is Chinese environment
     */
    public static boolean isZhEnv(Context context) {
        if (null == context) {
            outLog(TAG, OutLogType.PARAMETER_NULL_WARNING);
            return false;
        }
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith(new Locale("zh").getLanguage());
    }
}
