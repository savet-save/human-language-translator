package com.example.humanlanguagetranslator.helper;

import android.content.Context;
import android.net.TrafficStats;

import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.Utils;
import com.example.humanlanguagetranslator.callback.DownLoaderCallback;
import com.example.humanlanguagetranslator.data.Dictionary;

import java.util.UUID;

public class ImageHelper {
    private static final String TAG = "ImageHelper";

    @Nullable
    public static ImageType getImageType(String fileName) {
        if (Utils.isEmptyString(fileName)) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return null;
        }

        String[] data = fileName.split("\\.");
        String suffix = data[data.length - 1];
        switch (suffix) {
            case "jpg":
            case "jpeg":
                return ImageType.JPEG;
            case "png":
                return ImageType.PNG;
            case "gif":
                return ImageType.GIF;
            default:
                return null;
        }
    }

    public enum ImageType {
        JPEG,
        PNG,
        GIF
    }

    public static abstract class requestImage implements Runnable {
        private final Context mContext;
        private final String mImageUrl;
        private final ImageType mImageType;
        private final UUID mWordId;

        /**
         * create a requestImage , but not save to Dictionary
         * @param context context
         * @param imageUrl image url
         */
        public requestImage(Context context, String imageUrl){
            this(context, imageUrl, null);
        }

        /**
         * create a requestImage
         * @param context context
         * @param imageUrl request image url
         * @param wordId for save to Dictionary
         */
        public requestImage(Context context, String imageUrl, @Nullable UUID wordId){
            mContext = context;
            mImageUrl = imageUrl;
            mWordId = wordId;
            mImageType = ImageHelper.getImageType(mImageUrl);
        }

        public ImageType getImageType() {
            return mImageType;
        }

        public UUID getWordId() {
            return mWordId;
        }

        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TrafficStats.setThreadStatsTag(1); // for clean detectAll() log
                    // step 1. check url
                    if (Utils.isEmptyString(mImageUrl)) {
                        return;
                    }

                    Utils.logDebug(TAG, "image type :" + mImageType);
                    // step 2. chek net status
                    if (!NetWorkHelper.checkNetworkStatus(mContext)) {
                        Utils.outLog(TAG, "network not connect!");
                        //TODO need add handler deal with this, request image data when network connect
                        return;
                    }
                    // step 3. request image data
                    NetWorkHelper.requestUrlData(mImageUrl,
                            new DownLoaderCallback() {
                                @Override
                                public void onFailure(Exception e) {
                                    Utils.logDebug(TAG, "image request fail");
                                }

                                @Override
                                public void onResponse(byte[] data) {
                                    Utils.logDebug(TAG, "image onResponse");
                                    if (mImageType == null) {
                                        Utils.outLog(TAG, "image type is null");
                                        return;
                                    }
                                    if (mWordId != null) {
                                        Dictionary.getInstance().putImageData(mWordId, data);
                                    }
                                    updateImage(data);
                                }
                            });
                }
            }).start();
        }

        public abstract void updateImage(byte[] data);
    }
}
