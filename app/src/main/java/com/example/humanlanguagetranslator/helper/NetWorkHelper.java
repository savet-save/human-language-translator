package com.example.humanlanguagetranslator.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetWorkHelper {

    private final static String TAG = "NetWorkHelper";
    private final static int DEFAULT_CONNECT_TIMEOUT = 5000; // 5s

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
