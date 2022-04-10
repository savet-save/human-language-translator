package com.example.humanlanguagetranslator.callback;

public abstract class DownLoaderCallback {
    /**
     * Callback if the request fails
     */
    public abstract void onFailure(Exception e);

    /**
     * Callback when the request is successful
     */
    public abstract void onResponse(byte[] data);
}
