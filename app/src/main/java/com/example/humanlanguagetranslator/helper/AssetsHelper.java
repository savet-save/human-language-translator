package com.example.humanlanguagetranslator.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;

import androidx.annotation.Nullable;

import com.example.humanlanguagetranslator.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AssetsHelper {

    private static final String TAG = "AssetsHelper";

    private static volatile AssetsHelper instance;
    private AssetManager mAssetManager;

    public static AssetsHelper getInstance(){
        if (null == instance) {
            synchronized (AssetsHelper.class) {
                if (null == instance) {
                    instance = new AssetsHelper();
                }
            }
        }
        return instance;
    }

    private AssetsHelper() {}

    /**
     *  Gets a Json object from the specified path of Assets
     * @param context context
     * @param assetsPath file path an name, For example : "image/doge.jpg"
     * @return  <p> success : json object </p>
     *          <p> fail : null </p>
     */
    @Nullable
    public JSONObject getJsonAssets(Context context, String assetsPath) {
        if (null == context || null == assetsPath) {
            Utils.outLog(TAG, "error : parameter is null");
            return null;
        }
        if (null == mAssetManager) {
            mAssetManager = context.getAssets();
        }
        try {
            InputStream open = mAssetManager.open(assetsPath);
            InputStreamReader inputStreamReader = new InputStreamReader(open, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            return new JSONObject(stringBuilder.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
