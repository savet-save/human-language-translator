package com.example.humanlanguagetranslator.helper;

import android.app.usage.NetworkStats;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.example.humanlanguagetranslator.callback.DownLoaderCallback;

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



    /*
     * 连接网络
     * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问
     */
//    private Runnable connectNet = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                String filePath = "https://img-my.csdn.net/uploads/201402/24/1393242467_3999.jpg";
//                mFileName = "test.jpg";
//
//                //以下是取得图片的两种方法
//                //方法1：取得的是byte数组, 从byte数组生成bitmap
//                byte[] data = getImage(filePath);
//                if (data != null) {
//                    mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
//                } else {
////                    Toast.makeText(Net?\WorkHelper.this, "Image error!", 1).show();
//                }
//
//
//                //******** 方法2：取得的是InputStream，直接从InputStream生成bitmap ***********/
//                mBitmap = BitmapFactory.decodeStream(getImageStream(filePath));
//                //********************************************************************/
//
//                // 发送消息，通知handler在主线程中更新UI
////                mConnectHandler.sendEmptyMessage(0);
//                Log.d(TAG, "set image ...");
//            } catch (Exception e) {
//                Toast.makeText(NetWorkHelper.this, "无法链接网络！", 1).show();
//                e.printStackTrace();
//            }
//        }
//    };

}
