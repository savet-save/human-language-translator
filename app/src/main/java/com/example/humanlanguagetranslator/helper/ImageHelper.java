package com.example.humanlanguagetranslator.helper;

import android.graphics.Movie;
import android.graphics.drawable.AnimatedImageDrawable;

import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.Utils;

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

    //TODO : to be completed
    public static void gifShow() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            AnimatedImageDrawable animatedImageDrawable = new AnimatedImageDrawable();
        } else {
            Movie.decodeFile(null);
        }
    }

    public enum ImageType {
        JPEG,
        PNG,
        GIF
    }
}
