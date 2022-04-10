package com.example.humanlanguagetranslator.helper;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
    private static final String TAG = "FileHelper";
    private static final String DATA_PATH_NAME = "ImageData";

    /**
     * 保存文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static void saveFile(Bitmap bm, Bitmap.CompressFormat compressFormat, String fileName, Context context) throws IOException {
        File dirFile = new File(context.getFilesDir() + DATA_PATH_NAME);
        if (!dirFile.exists()) {
            boolean result = dirFile.mkdir();
            if (!result) {
                return;
            }
        }
        File myCaptureFile = new File(dirFile.getPath() + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(compressFormat, 100, bos);
        bos.flush();
        bos.close();
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
//
//    private Handler messageHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            mSaveDialog.dismiss();
//            Log.d(TAG, mSaveMessage);
//            Toast.makeText(FileHelper.this, mSaveMessage, Toast.LENGTH_SHORT).show();
//        }
//    };
}
