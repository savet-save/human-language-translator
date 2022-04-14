package com.example.humanlanguagetranslator.helper;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
    private static final String TAG = "FileHelper";
    private static final String DATA_PATH_NAME = "ImageData";
    private static final int READ_TO_END_FLAG = -1;

    /**
     * 保存文件
     */
    public static boolean saveFile(byte[] data, String fileName, Context context) {
        if (null == data || null == fileName || null == context) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return false;
        }

        File dirFile = new File(context.getFilesDir() + DATA_PATH_NAME);
        if (!dirFile.exists()) {
            boolean result = dirFile.mkdir();
            if (!result) {
                Utils.outLog(TAG, "can't mkdir :" + dirFile.getAbsolutePath());
                return false;
            }
        }

        File myCaptureFile = new File(dirFile.getPath() + "/" + fileName);
        if (myCaptureFile.exists() && !myCaptureFile.canWrite()) {
            Utils.outLog(TAG, myCaptureFile.getName() + " can't write");
            return false;
        }

        FileOutputStream fileOutputStream = null;
        try {
            Utils.logDebug(TAG, "save path : " + myCaptureFile.getAbsolutePath() + "\n" +
                    "save length : " + data.length);
            fileOutputStream = new FileOutputStream(myCaptureFile);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            Utils.outLog(TAG, "save to file fail : " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * read file
     *
     * @param fileName file name
     * @param context  context
     * @return read data from file, if error is null
     */
    @Nullable
    public static byte[] readFile(String fileName, Context context) {
        if (null == fileName || null == context) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return null;
        }

        File dirFile = new File(context.getFilesDir() + DATA_PATH_NAME);
        if (!dirFile.exists()) {
            boolean result = dirFile.mkdir();
            if (!result) {
                Utils.outLog(TAG, "can't mkdir :" + dirFile.getAbsolutePath());
                return null;
            }
        }

        File myCaptureFile = new File(dirFile.getPath() + "/" + fileName);
        if (myCaptureFile.exists() && !myCaptureFile.canRead()) {
            Utils.outLog(TAG, myCaptureFile.getName() + " can't read");
            return null;
        }

        byte[] data = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fileInputStream = null;
        try {
            Utils.logDebug(TAG, "read path : " + myCaptureFile.getAbsolutePath());
            fileInputStream = new FileInputStream(myCaptureFile);
            int count = 0;
            while ((count = fileInputStream.read(data)) != READ_TO_END_FLAG) {
                byteArrayOutputStream.write(data, 0, count);
            }
            fileInputStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            Utils.outLog(TAG, "save to file fail : " + e.getMessage());
            return null;
        }
        return byteArrayOutputStream.toByteArray();
    }
}
