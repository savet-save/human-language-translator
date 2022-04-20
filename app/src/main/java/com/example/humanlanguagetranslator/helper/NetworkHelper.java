package com.example.humanlanguagetranslator.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.humanlanguagetranslator.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class NetworkHelper {
    private static final Timer mTimer = new Timer();
    private static final String TAG = "NetWorkHelper";
    private final static int DEFAULT_CONNECT_TIMEOUT = 5000; // 5s
    private static final int TIMER_DELAY_TIME = 500;
    private static final int TIMER_PERIOD_TIME = 500;
    private static TimerTask sTimerTask = null;

    /**
     * Get data from network
     *
     * @param url url
     */
    public static void requestUrlData(String url, DownLoaderCallback callback) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            connection.setRequestMethod("GET");
            InputStream inStream = connection.getInputStream();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                callback.onResponse(readStream(inStream));
            }
            connection.disconnect();
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Verify that the network status is Available
     */
    public static boolean checkNetworkStatus(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;

    }

    /**
     * wait Network Connect, then Wakes up all threads that are waiting on this object's monitor
     * <pre>use like :
     *  Object syncObject = new Object;
     *  synchronized (syncObject) {
     *      NetWorkHelper.waitNetWorkConnect(context, syncObject);
     *  }</pre>
     *
     * @param context    context
     * @param syncObject sync Object
     */
    public static void waitNetworkConnect(Context context, Object syncObject) {
        if (null == sTimerTask) {
            synchronized (mTimer) {
                if (null == sTimerTask) {
                    sTimerTask = new CheckNetworkTask(context, syncObject);
                    mTimer.schedule(sTimerTask, TIMER_DELAY_TIME, TIMER_PERIOD_TIME);
                }
            }
        }

        try {
            syncObject.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get data from stream
     *
     * @param inStream input stream
     * @return byte[]
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    private static class CheckNetworkTask extends TimerTask {

        private final Context mContext;
        private final Object mSyncObject;
        private final UUID mUUID = UUID.randomUUID();

        public CheckNetworkTask(Context context, Object syncObject) {
            mContext = context;
            mSyncObject = syncObject;
        }

        @Override
        public void run() {
            synchronized (mSyncObject) {
                if (checkNetworkStatus(mContext)) {
                    mSyncObject.notifyAll();
                    mTimer.cancel();
                }
            }
            Utils.logDebug(TAG, mUUID + " wait network connect..");
        }
    }

    public static abstract class DownLoaderCallback {
        /**
         * Callback if the request fails
         */
        public abstract void onFailure(Exception e);

        /**
         * Callback when the request is successful
         */
        public abstract void onResponse(byte[] data);
    }

}
