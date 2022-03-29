package com.example.humanlanguagetranslator;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public final class Utils {
    /**  id */
    public static final int ID_ADD_WORD = R.id.menu_item_add_word;
    public static final int ID_SEARCH_WORD = R.id.menu_item_search_word;

    public static final int ERROR_CONTEXT = 0;

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    private static final String TAG = "Utils";

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
        return (int) ( px + 0.5f);
    }
}
