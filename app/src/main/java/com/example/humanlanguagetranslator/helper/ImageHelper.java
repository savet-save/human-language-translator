package com.example.humanlanguagetranslator.helper;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.Word;
import com.example.humanlanguagetranslator.util.Utils;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageHelper {
    private static final String TAG = "ImageHelper";

    @Nullable
    public static ImageType getImageType(String fileName) {
        if (Utils.isEmptyString(fileName)) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_STRING_NULL_OR_EMPTY);
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

    public static abstract class RequestImage implements Runnable {
        private static final String TAG = "RequestImage";
        private static final int DEFAULT_NUMBER_THREADS = 10;
        private static final ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_NUMBER_THREADS);
        private static final Object syncObject = new Object();

        private final Context mContext;
        private String mImageUrl;
        @Nullable
        private ImageType mImageType;
        private final UUID mWordId;
        /**
         * not with suffix
         */
        private String mFileName;
        private final boolean mForceRequest;
        private Word mWord;

        /**
         * create a requestImage, but not save to cache, and save to file or read from file
         *
         * @param context  context
         * @param imageUrl request image url
         */
        public RequestImage(Context context, String imageUrl) {
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
        public RequestImage(Context context,
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

        /**
         * create a can change it dynamically image url requestImage,
         * and save to cache, save or read image from file
         *
         * @param context      context
         * @param word         request word
         * @param forceRequest save or read image file name
         */
        public RequestImage(Context context, @NonNull Word word, boolean forceRequest) {
            this(context, word.getPictureLink(), word.getId(), word.getContent(), forceRequest);
            mWord = word;
        }

        @Nullable
        public ImageType getImageType() {
            return mImageType;
        }

        @Override
        public void run() {
            executorService.submit(() -> {
                //for can get exception info
                try {
                    dealWithImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        private void dealWithImage() {
            // step 1. check local
            if (!mForceRequest && !Utils.isEmptyString(mFileName) && mImageType != null) {
                byte[] imageData = FileHelper.readFile(mFileName + getSuffix(),
                        mContext,
                        FileHelper.SaveDir.IMAGE_DATE);
                if (null != imageData) {
                    updateImage(imageData);
                    saveToCache(imageData);
                    Utils.logDebug(TAG, "read image data from local, length :" + imageData.length);
                    return;
                }
            }
            // step 2. check url
            if (mWord != null) {
                mImageUrl = mWord.getPictureLink();
            }
            if (Utils.isEmptyString(mImageUrl)) {
                Utils.logDebug(TAG, "mImageUrl is empty, end current method");
                return;
            }
            mImageType = ImageHelper.getImageType(mImageUrl); // url maybe change
            Utils.logDebug(TAG, "image type :" + mImageType);

            // step 3. chek net status
            if (!NetworkHelper.checkNetworkStatus(mContext)) {
                synchronized (syncObject) {
                    if (!NetworkHelper.checkNetworkStatus(mContext)) {
                        Utils.outLog(TAG, "network not connect!");
                        NetworkHelper.waitNetworkConnect(mContext, syncObject);
                    }
                }
            }

            // step 4. request image data
            NetworkHelper.requestUrlData(mImageUrl,
                    new NetworkHelper.DownLoaderCallback() {
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

        /**
         * get image Suffix
         *
         * @return Suffix , or null if mImageType is null
         */
        @Nullable
        public String getSuffix() {
            if (null == mImageType) {
                return null;
            }
            return "." + mImageType.getName();
        }

        public void saveToFile(byte[] data) {
            if (mWord != null) {
                mFileName = mWord.getContent();
            }
            if (Utils.isEmptyString(mFileName)) {
                return;
            }
            if (FileHelper.saveFile(data,
                    mFileName + getSuffix(),
                    mContext,
                    FileHelper.SaveDir.IMAGE_DATE)) {
                Utils.logDebug(TAG, "save " + mFileName + " file success");
            }
        }
    }
}
