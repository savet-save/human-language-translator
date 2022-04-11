package com.example.humanlanguagetranslator.view;

import android.os.Build;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.Utils;

/**
 * from https://blog.csdn.net/lcq5211314123/article/details/43492509
 * @author qndroid
 */
public class GifView extends View {
    private static final String TAG = "GifView";

    private Movie mMovie;

    /**
     * 动画开始时间
     */
    private long mMovieStart;
    private boolean mPaused = false;
    /**
     * 当前帧动画时间
     */
    private int mCurrentAnimationTime;
    /**
     * 自定义的三个属性
     */
    private boolean mDecodeSTREAM;
    private int mSrcID;
    private int mDefaultTime;

    private static final boolean DECODES_STREAM_DEF_VALUE = true;
    private static final int SRC_ID_DEF_VALUE = -1;
    private static final int DEFAULT_TIME_DEF_VALUE = 1000;

    public GifView(Context context) {
        super(context);
        init(context, null);
    }

    public GifView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GifView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GifView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GifView);
        mSrcID = typedArray.getResourceId(R.styleable.GifView_gif_src, SRC_ID_DEF_VALUE);
        mDecodeSTREAM = typedArray.getBoolean(R.styleable.GifView_decode_stream,
                DECODES_STREAM_DEF_VALUE);
        mDefaultTime = typedArray.getInteger(R.styleable.GifView_default_animation_time,
                DEFAULT_TIME_DEF_VALUE);
        typedArray.recycle();
        if (SRC_ID_DEF_VALUE == mSrcID) {
            Utils.logDebug(TAG, "src id not specified");
            return;
        }

        setFocusable(true);
        java.io.InputStream is;
        is = context.getResources().openRawResource(mSrcID);

        if (mDecodeSTREAM) {
            mMovie = Movie.decodeStream(is);    // 根据文件流创建Movie绘制对象
        } else {
            byte[] array = streamToBytes(is);
            mMovie = Movie.decodeByteArray(array, 0, array.length);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null) {
            if (!mPaused) {
                // 更新帧时间
                updateAnimationTime();
                drawMovieFrame(canvas);
                invalidate();
            } else {
                // 暂停时，不更新帧时间，则只画当前帧
                drawMovieFrame(canvas);
            }
        }
    }

    public void setMovie(Movie movie) {
        if (null != movie) {
            mMovie = movie;
            invalidate();
        }
    }

    private void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();
        // 如果第一帧，记录起始时间
        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        // 取出动画的时长
        int dur = mMovie.duration();
        if (dur == 0) {
            dur = mDefaultTime;
        }
        // 算出需要显示第几帧
        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
    }

    private void drawMovieFrame(Canvas canvas) {
        // 设置要显示的帧，绘制即可
        mMovie.setTime(mCurrentAnimationTime);
        canvas.save();
        mMovie.draw(canvas, 0, 0);
        canvas.restore();
    }

    /**
     * 设置暂停
     *
     * @param paused
     */
    public void setPaused(boolean paused) {
        this.mPaused = paused;
        if (!paused) {
            /**
             * 更新动画起点时间
             */
            mMovieStart = android.os.SystemClock.uptimeMillis()
                    - mCurrentAnimationTime;
        }
        invalidate();
    }

    /**
     * 判断gif图是否停止了
     *
     * @return
     */
    public boolean isPaused() {
        return this.mPaused;
    }

    /**
     * 重写此方法，使自定义View支持wrap_content
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {

            int desired = (int) (getPaddingLeft() + mMovie.width() + getPaddingRight());
            width = Math.min(desired, widthSize);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int desired = (int) (getPaddingTop() + mMovie.height() + getPaddingBottom());
            height = Math.min(desired, heightSize);
        }

        setMeasuredDimension(width, height);
    }

    /**
     * 将流转化为字节数组
     *
     * @param is input stream
     * @return byte[]
     */
    private static byte[] streamToBytes(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
            os.close();
            is.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return os.toByteArray();
    }

}

