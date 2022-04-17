package com.example.humanlanguagetranslator.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class GlobalHandler extends HandlerThread {

    private static final String THREAD_NAME = "GlobalHandle";
    private static final String TAG = "GlobalHandle";

    private static final int DELAY_TIME = 10;
    private static final int PERIOD_TIME = 200;
    private static final int MAX_COUNT = 10;

    private final Timer mTimer;

    private Handler mBackgroundHandler;
    private Handler mUIHandler;

    private static volatile GlobalHandler instance;

    private GlobalHandler() {
        super(THREAD_NAME);
        mTimer = new Timer();
        mUIHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * first is init this
     *
     * @return a GlobalHandle
     */
    public static GlobalHandler getInstance() {
        if (null == instance) {
            synchronized (GlobalHandler.class) {
                if (null == instance) {
                    instance = new GlobalHandler();
                    instance.start();
                    instance.getLooper();
                }
            }
        }
        return instance;
    }

    /**
     * <p> get a global background handler, but maybe is null </p>
     * <p> Warning : need first call getInstance() later sometime(about 10ms) </p>
     *
     * @return background handler
     */
    @Nullable
    public Handler getBackgroundHandler() {
        return mBackgroundHandler;
    }

    /**
     * <p> Post to global Background handle </p>
     * warning : Not use this update UI, The program may crash
     * @param runnable do something
     * @return post result
     */
    public boolean post2BackgroundHandler(Runnable runnable) {
        if (mBackgroundHandler != null) {
            return mBackgroundHandler.post(runnable);
        }

        useTimerQuery(runnable);
        return true;
    }

    /**
     * get a global UI handler
     * @return UI handler
     */
    @NonNull
    public Handler getUIHandler() {
        return mUIHandler;
    }

    /**
     * <p> Post to global UI handle </p>
     * <p> warning : Not execute long-running operations, freezes all UI events </p>
     * @param runnable do something
     * @return post result
     */
    public boolean post2UIHandler(Runnable runnable) {
        return mUIHandler.post(runnable);
    }

    private void useTimerQuery(Runnable runnable) {
        Utils.logDebug(TAG, "use timer!");
        mTimer.schedule(new TimerTask() {
            private int count = 0;

            @Override
            public void run() {
                // check run count
                if (count > MAX_COUNT) {
                    mTimer.cancel();
                    Utils.outLog(TAG, "count out :" + MAX_COUNT);
                    return;
                }
                count++;

                if (null == mBackgroundHandler) {
                    return;
                }
                mBackgroundHandler.post(runnable);
                mTimer.cancel();
            }
        }, DELAY_TIME, PERIOD_TIME);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mBackgroundHandler = new Handler(instance.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                return false;
            }
        });
    }

    @Override
    public boolean quit() {
        mBackgroundHandler = null;
        mUIHandler = null;
        return super.quit();
    }
}
