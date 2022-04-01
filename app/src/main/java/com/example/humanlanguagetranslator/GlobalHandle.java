package com.example.humanlanguagetranslator;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class GlobalHandle extends HandlerThread {

    private static final String THREAD_NAME = "GlobalHandle";
    private static final String TAG = "GlobalHandle";

    private static final int DELAY_TIME = 10;
    private static final int PERIOD_TIME = 200;
    private static final int MAX_COUNT = 10;

    private final Timer timer;

    private Handler mHandler;

    private static volatile GlobalHandle instance;

    private GlobalHandle() {
        super(THREAD_NAME);
        timer = new Timer();
    }

    /**
     * first is init this
     *
     * @return a GlobalHandle
     */
    public static GlobalHandle getInstance() {
        if (null == instance) {
            synchronized (GlobalHandle.class) {
                if (null == instance) {
                    instance = new GlobalHandle();
                    instance.start();
                    instance.getLooper();
                }
            }
        }
        return instance;
    }

    /**
     * <p> get a global handle, but maybe is null </p>
     * <p> Warning : need first call getInstance() later sometime(about 10ms) </p>
     *
     * @return a handler
     */
    @Nullable
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * post to global handle
     *
     * @param runnable do something
     * @return post result
     */
    public boolean post2Handle(Runnable runnable) {
        if (mHandler != null) {
            return mHandler.post(runnable);
        }

        useTimerQuery(runnable);
        return true;
    }

    private void useTimerQuery(Runnable runnable) {
        Utils.logDebug(TAG, "use timer!");
        timer.schedule(new TimerTask() {
            private int count = 0;

            @Override
            public void run() {
                // check run count
                if (count > MAX_COUNT) {
                    timer.cancel();
                    Utils.outLog(TAG, "count out :" + MAX_COUNT);
                    return;
                }
                count++;

                if (null == mHandler) {
                    return;
                }
                mHandler.post(runnable);
                timer.cancel();
            }
        }, DELAY_TIME, PERIOD_TIME);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler(instance.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                return false;
            }
        });
    }

    @Override
    public boolean quit() {
        mHandler = null;
        return super.quit();
    }
}
