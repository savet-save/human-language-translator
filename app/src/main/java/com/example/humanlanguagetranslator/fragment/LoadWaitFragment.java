package com.example.humanlanguagetranslator.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.util.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadWaitFragment extends Fragment {
    private static final String TAG = "LoadWaitFragment";
    private static final int DEFAULT_DURATION = 800;
    private static final String ARG_HINT_TEXT = "ARG_HINT_TEXT";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);;

    private View mLoadImage;
    private TextView mHintText;
    private ObjectAnimator mAnimation;


    public static LoadWaitFragment newInstance(String hintText) {
        Bundle arg = new Bundle();
        arg.putString(ARG_HINT_TEXT, hintText);

        LoadWaitFragment loadWaitFragment = new LoadWaitFragment();
        loadWaitFragment.setArguments(arg);
        return loadWaitFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_load_wait, container, false);
        mLoadImage = view.findViewById(R.id.load_image);
        mHintText = view.findViewById(R.id.load_hint_text);
        initLoadImageAnimation();
        initHintText();

        return view;
    }

    private void initHintText() {
        if (null == mHintText) {
            Utils.outLog(TAG, "initHintText : hint text view is null");
            return;
        }
        Bundle arguments = getArguments();
        if (null != arguments) {
            String text = arguments.getString(ARG_HINT_TEXT);
            if (null == text) {
                return;
            }
            mHintText.setText(text);
        }
    }

    private void initLoadImageAnimation() {
        if (null == mLoadImage) {
            Utils.outLog(TAG, "initLoadImageAnimation fail : load image view is null");
            return;
        }
        mAnimation = ObjectAnimator.ofFloat(mLoadImage, "rotation", 0f, 360f);
        mAnimation.setDuration(DEFAULT_DURATION);
        mAnimation.setRepeatMode(ValueAnimator.RESTART);
        mAnimation.setRepeatCount(ValueAnimator.INFINITE); // forever keep
        mAnimation.setInterpolator(new LinearInterpolator()); //for playing at a constant speed
        mAnimation.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeLoadAnimation();
    }

    @Override
    public void onStop() {
        super.onStop();
        pauseLoadAnimation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        endLoadAnimation();
        Utils.logDebug(TAG, "load fragment is destroy");
    }

    public static void shutdownExecutor() {
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    public static void doAsyncTask(Runnable task) {
        executorService.submit(task);
    }

    private void pauseLoadAnimation() {
        if (null == mAnimation) {
            return;
        }
        if (!mAnimation.isPaused()) {
            mAnimation.pause();
        }
    }

    private void resumeLoadAnimation() {
        if (null == mAnimation) {
            return;
        }
        if (mAnimation.isPaused()) {
            mAnimation.resume();
        }
    }

    private void endLoadAnimation() {
        if (null == mAnimation) {
            return;
        }
        if (mAnimation.isRunning()) {
            mAnimation.end();
        }
    }
}
