package com.example.humanlanguagetranslator.helper;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.humanlanguagetranslator.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
    private static final String TAG = "FileHelper";
    private static final String DATA_PATH_NAME = "ImageData";

    /**
     * 保存文件
     */
    public static void saveFile(byte[] data, String fileName, Context context) throws IOException {
        File dirFile = new File(context.getFilesDir() + DATA_PATH_NAME);
        if (!dirFile.exists()) {
            boolean result = dirFile.mkdir();
            if (!result) {
                return;
            }
        }
        File myCaptureFile = new File(dirFile.getPath() + fileName);
        if (myCaptureFile.canWrite()) {
            FileOutputStream fileOutputStream = new FileOutputStream(myCaptureFile);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
        } else {
            Utils.logDebug(TAG, myCaptureFile.getName() + " can't write");
        }
    }

//    private Runnable saveFileRunnable = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                saveFile(mBitmap, mFileName);
//                mSaveMessage = "图片保存成功！";
//            } catch (IOException e) {
//                mSaveMessage = "图片保存失败！";
//                e.printStackTrace();
//            }
//            messageHandler.sendMessage(messageHandler.obtainMessage());
//        }
//
//    };
}
