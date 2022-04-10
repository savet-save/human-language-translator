package com.example.humanlanguagetranslator;

import android.content.res.Resources;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public final class Utils {
    /**
     * id
     */
    public static final int ID_ADD_WORD = R.id.menu_item_add_word;
    public static final int ID_SEARCH_WORD = R.id.menu_item_search_word;
    public static final int ID_MENU_ABOUT = R.id.menu_item_about;

    public static final int ERROR_CONTEXT = 0;

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    private static final String TAG = "Utils";

    public enum OutLogType {
        PARAMETER_NULL_ERROR,
        PARAMETER_NULL_WARNING
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

    public static void outLog(String tag, OutLogType type) {
        String builder = getCallStack();
        switch (type) {
            case PARAMETER_NULL_ERROR:
                Log.e(tag, "Error : parameter is null, call stack - " + builder);
                break;
            case PARAMETER_NULL_WARNING:
                Log.d(tag, "Waring : parameter is null, call stack - " + builder);
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

    public static boolean isDualPane(AppCompatActivity activity) {
        if (null == activity) {
            return false;
        }
        View viewById = activity.findViewById(R.id.detail_fragment_container);
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
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param pxValue 像素值
     * @return 转换完成的dp值
     */
    public static int px2dp(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px
     *
     * @param dpValue 像素值
     * @return 转换完成的px值
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
     * @return <p> true : s is null or empty </>
     * <p> false : not is empty </p>
     */
    public static boolean isEmptyString(String s) {
        return (null == s || s.isEmpty());
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
}
