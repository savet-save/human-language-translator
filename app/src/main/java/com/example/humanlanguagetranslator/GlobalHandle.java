package com.example.humanlanguagetranslator;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GlobalHandle extends HandlerThread {

    private static final String THREAD_NAME = "GlobalHandle";

    private Handler mHandler;

    private static volatile GlobalHandle instance;

    private GlobalHandle() {
        super(THREAD_NAME);
    }

    /**
     * first is init this
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
     * <p> Warning : need first call getInstance() later sometime(about 200ms) </p>
     * @return a handler
     */
    @Nullable
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * post to global handle
     * @param runnable do something
     * @return post result
     */
    public boolean post2Handle(Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
            return true;
        }
        return false;
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
