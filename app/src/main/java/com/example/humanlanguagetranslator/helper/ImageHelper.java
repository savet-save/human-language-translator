package com.example.humanlanguagetranslator.helper;

import android.content.Context;
import android.net.TrafficStats;

import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.Utils;
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
        JPEG("jpg"),
        PNG("png"),
        GIF("gif");

        private String mName;
        ImageType(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }

    public static abstract class requestImage implements Runnable {
        private final Context mContext;
        private final String mImageUrl;
        private final ImageType mImageType;
        private final UUID mWordId;
        private final String mFileName;
        private final boolean mForceRequest;

        /**
         * create a requestImage, but not save to cache, and save to file or read from file
         *
         * @param context  context
         * @param imageUrl request image url
         */
        public requestImage(Context context, String imageUrl) {
            this(context, imageUrl, null, null, false);
        }

        /**
         * create a requestImage
         *
         * @param context  context
         * @param imageUrl request image url
         * @param wordId   for save to cache
         * @param fileName save or read image file name
         */
        public requestImage(Context context,
                            String imageUrl,
                            @Nullable UUID wordId,
                            @Nullable String fileName,
                            boolean forceRequest) {
            mContext = context;
            mImageUrl = imageUrl;
            mWordId = wordId;
            mImageType = ImageHelper.getImageType(mImageUrl);
            mFileName = fileName;
            mForceRequest = forceRequest;
        }

        public ImageType getImageType() {
            return mImageType;
        }

        @Override
        public void run() {
            new Thread(() -> {
                TrafficStats.setThreadStatsTag(1); // for clean detectAll() log
                dealWithImage();
            }).start();
        }

        private void dealWithImage() {
            // step 1. check local
            if (!mForceRequest) {
                byte[] imageData = FileHelper.readFile(mFileName + getSuffix(), mContext);
                if (null != imageData) {
                    updateImage(imageData);
                    saveToCache(imageData);
                    Utils.logDebug(TAG, "read image data from local, length :" + imageData.length);
                    return;
                }
            }
            // step 2. check url
            if (Utils.isEmptyString(mImageUrl)) {
                return;
            }
            Utils.logDebug(TAG, "image type :" + mImageType);
            // step 3. chek net status
            if (!NetWorkHelper.checkNetworkStatus(mContext)) {
                Utils.outLog(TAG, "network not connect!");
                //TODO need add handler deal with this, request image data when network connect
                return;
            }
            // step 4. request image data
            NetWorkHelper.requestUrlData(mImageUrl,
                    new NetWorkHelper.DownLoaderCallback() {
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
                            saveToCache(data);
                            saveToFile(data);
                            updateImage(data);
                        }
                    });
        }

        public abstract void updateImage(byte[] data);

        public void saveToCache(byte[] data) {
            if (null == mWordId) {
                return;
            }
            Dictionary.getInstance().putImageData(mWordId, data);
        }

        public String getSuffix() {
            return "." + mImageType.getName();
        }

        public void saveToFile(byte[] data) {
            if (null == mFileName) {
                return;
            }
            if (FileHelper.saveFile(data, mFileName + getSuffix(), mContext)) {
                Utils.logDebug(TAG, "save " + mFileName + " file success");
            }
        }
    }
}
