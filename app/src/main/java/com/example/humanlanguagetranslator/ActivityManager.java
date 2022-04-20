package com.example.humanlanguagetranslator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {

    public static List<AppCompatActivity> activities = new ArrayList<>();

    /**
     * 添加Activity
     *
     * @param activity 添加的Activity对象
     */
    public static void addActivity(AppCompatActivity activity) {
        synchronized (ActivityManager.class) {
            activities.add(activity);
        }
    }

    /**
     * 删除Activity
     *
     * @param activity 删除的Activity对象
     */
    public static void removeActivity(AppCompatActivity activity) {
        synchronized (ActivityManager.class) {
            activities.remove(activity);
        }
    }

    /**
     * find Activity in ActivityManager cache
     *
     * @param activityName is SimpleName
     * @return Activity, or null if not find
     */
    @Nullable
    public static AppCompatActivity findActivity(String activityName) {
        synchronized (ActivityManager.class) {
            for (AppCompatActivity activity : activities) {
                String name = activity.getClass().getName();
                if (name.equals(activityName)) {
                    return activity;
                }
            }
        }
        return null;
    }

    /**
     * 关闭指定的Activity
     *
     * @param activityName 需要关闭的Activity包名类名
     */
    public static void finishActivity(String activityName) {
        //在activities集合中找到类名与指定类名相同的Activity就关闭
        synchronized (ActivityManager.class) {
            for (AppCompatActivity activity : activities) {
                String name = activity.getClass().getName();
                if (name.equals(activityName)) {
                    if (activity.isFinishing()) {
                        //当前activity如果已经Finish，则只从activities清除就好了
                        activities.remove(activity);
                    } else {
                        //没有Finish则Finish
                        activity.finish();
                    }
                }
            }
        }
    }
}
