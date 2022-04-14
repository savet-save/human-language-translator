package com.example.humanlanguagetranslator.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.humanlanguagetranslator.GlobalHandler;
import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.Utils;
import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.VerifiedInfo;
import com.example.humanlanguagetranslator.data.Word;
import com.example.humanlanguagetranslator.helper.ImageHelper;
import com.example.humanlanguagetranslator.view.GifView;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class WordFragment extends Fragment {
    private static final String ARGS_WORD_ID = "args_word_id";
    private static final String TAG = "WordFragment";
    private static final String DIALOG_DATE_TAG = "DialogDate";

    private Word mWord;
    private TextView mSynonymText;
    private Button mDateButton;
    private TextView mVerifiedTimeText;
    private OnWordUpdatedCallback mCallback;
    private FragmentManager mFragmentManager;
    private TextView mTypeText;
    private TextView mTranslationText;
    private TextView mContentText;
    private TextView mQuarryText;
    private TextView mExampleText;
    private TextView mEarliestDateText;
    private TextView mEarliestAddrText;
    private TextView mVerifiedOtherText;
    private TextView mAuthorText;
    private TextView mRestorersText;
    private ImageView mImageView;
    private GifView mGifView;
    private ImageHelper.requestImage mRequestImage;

    public interface OnWordUpdatedCallback {
        void onWordUpdated(Word word);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnWordUpdatedCallback) {
            mCallback = (OnWordUpdatedCallback) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (null != arguments) {
            UUID wordId = (UUID) arguments.getSerializable(ARGS_WORD_ID);
            mWord = Dictionary.getInstance().getWord(wordId);
            if (mWord != null) {
                mRequestImage = new ImageHelper.requestImage(getActivity(),
                        mWord.getPictureLink(),
                        mWord.getId(),
                        mWord.getContent(),
                        false) {
                    @Override
                    public void updateImage(byte[] data) {
                        myUpdateImage(data);
                    }
                };
            }
        }
        FragmentActivity activity = getActivity();
        if (null != activity) {
            mFragmentManager = activity.getSupportFragmentManager();
        }
    }

    private void myUpdateImage(byte[] data) {
        if (ImageHelper.ImageType.GIF == mRequestImage.getImageType()) {
            Movie movie = Movie.decodeByteArray(data, 0, data.length);
            GlobalHandler.getInstance().post2UIHandler(new Runnable() {
                @Override
                public void run() {
                    mGifView.setMovie(movie);
                    mGifView.setVisibility(View.VISIBLE);
                    mImageView.setVisibility(View.GONE);
                }
            });
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (null == bitmap) {
                Utils.outLog(TAG, "decode bitmap fail from Byte Array");
            }
            GlobalHandler.getInstance().post2UIHandler(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(bitmap);
                    mGifView.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_details, container, false);
        mContentText = (TextView) view.findViewById(R.id.details_word_content_text);
        mSynonymText = (TextView) view.findViewById(R.id.details_word_synonym_text);
        mTypeText = (TextView) view.findViewById(R.id.details_word_type_text);
        mTranslationText = (TextView) view.findViewById(R.id.details_word_translation_text);
        mQuarryText = (TextView) view.findViewById(R.id.details_word_quarry_text);
        mExampleText = (TextView) view.findViewById(R.id.details_word_example_text);
        mVerifiedTimeText = (TextView) view.findViewById(R.id.details_word_verified_date_text);
        mEarliestDateText = (TextView) view.findViewById(R.id.details_word_earliest_date_text);
        mEarliestAddrText = (TextView) view.findViewById(R.id.details_word_earliest_addr_text);
        mVerifiedOtherText = (TextView) view.findViewById(R.id.details_word_verified_other_text);
        mAuthorText = (TextView) view.findViewById(R.id.details_word_author_text);
        mRestorersText = (TextView) view.findViewById(R.id.details_word_restorers_text);
        mImageView = (ImageView) view.findViewById(R.id.details_word_item_image);
        mGifView = (GifView) view.findViewById(R.id.details_word_item_gif_image);

        // init set data button
        mDateButton = (Button) view.findViewById(R.id.details_word_set_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mFragmentManager) {
                    Utils.outLog(TAG, "can't get FragmentActivity");
                    return;
                }
                DatePickerFragment dialog = DatePickerFragment.newInstance(null);
                dialog.show(mFragmentManager, DIALOG_DATE_TAG);

            }
        });

        //set show data
        updateAllUI();

        return view;
    }

    /**
     * set show text in view
     *
     * @param view   TextView
     * @param value  view text
     * @param isGone if true set view is gone(not visibility)
     */
    private void setTextShowUI(TextView view, String value, boolean isGone) {
        if (null == view) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return;
        }
        if (isGone) {
            view.setVisibility(View.GONE);
        }
        view.setText(value);

    }

    private void updateWordListUI() {
        if (null != mCallback) {
            mCallback.onWordUpdated(mWord);
        }
    }

    // 可见时
    @Override
    public void onResume() {
        super.onResume();
        if (null == mFragmentManager) {
            return;
        }
        Utils.logDebug(TAG, "set Fragment Result Listener id : "
                + (mWord == null ? "null word" : mWord.getId()));
        mFragmentManager.setFragmentResultListener(DatePickerFragment.REQUEST_DATE_KEY,
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    if (!DatePickerFragment.REQUEST_DATE_KEY.equals(requestKey)) {
                        Utils.logDebug(TAG, "not equals");
                        return;
                    }
                    Date date = (Date) result.getSerializable(DatePickerFragment.DATE_KEY);
                    Utils.logDebug(TAG, "date :" + date);
                    mWord.getVerifiedInfo().setVerifiedTime(date);
                    updateAllUI();
                    updateWordListUI();
                });
    }

    // 不可见时
    @Override
    public void onPause() {
        super.onPause();
        if (null == mFragmentManager) {
            return;
        }
        Utils.logDebug(TAG, "clear Fragment Result Listener id : "
                + (mWord == null ? "null word" : mWord.getId()));
        mFragmentManager.clearFragmentResultListener(DatePickerFragment.REQUEST_DATE_KEY);
    }

    private void updateAllUI() {
        if (null == mWord) {
            Utils.logDebug(TAG, "update UI fail : mWord is null");
            return;
        }
        setTextShowUI(mContentText, mWord.getContent(), false);

        String formatSynonym = mWord.getFormatSynonym(null);
        String synonym = getString(R.string.format_word_synonym, formatSynonym);
        setTextShowUI(mSynonymText, synonym, Utils.isEmptyString(formatSynonym));
        Utils.logDebug(TAG, synonym);

        String type = getString(R.string.format_word_type, mWord.getWordType().getName());
        setTextShowUI(mTypeText, type, false);

        setTextShowUI(mTranslationText, Utils.getFormatString(mWord.getTranslations()), false);

        String quarries = Utils.getFormatString(mWord.getQuarries());
        setTextShowUI(mQuarryText, quarries, Utils.isEmptyString(quarries));
        setTextShowUI(mExampleText, Utils.getFormatString(mWord.getExamples()), false);

        VerifiedInfo verifiedInfo = mWord.getVerifiedInfo();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(verifiedInfo.getVerifiedTime());
        String verifiedDate = getString(R.string.format_word_data,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        Utils.logDebug(TAG, "verifiedDate :" + verifiedDate + " valid : " + verifiedInfo.isValid());
        setTextShowUI(mVerifiedTimeText, getString(R.string.verified_date) + verifiedDate,
                !verifiedInfo.isValid());

        calendar.setTime(verifiedInfo.getEarliestTime());
        String earliestDate = getString(R.string.format_word_data,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        setTextShowUI(mEarliestDateText, getString(R.string.earliest_date) + earliestDate,
                !verifiedInfo.isValid());

        String earliestAddr = verifiedInfo.getEarliestAddr();
        setTextShowUI(mEarliestAddrText, getString(R.string.earliest_addr) + earliestAddr,
                Utils.isEmptyString(earliestAddr));

        String verifiedOther = verifiedInfo.getOther();
        setTextShowUI(mVerifiedOtherText, verifiedOther, Utils.isEmptyString(verifiedOther));

        String author = mWord.getAuthor();
        setTextShowUI(mAuthorText, getString(R.string.word_author) + author,
                Utils.isEmptyString(author));

        String restorers = Utils.getFormatString(mWord.getRestorers());
        setTextShowUI(mRestorersText, getString(R.string.word_restorers) + restorers,
                Utils.isEmptyString(restorers));


        byte[] imageData = Dictionary.getInstance().getImageData(mWord.getId());
        if (imageData != null) {
            myUpdateImage(imageData);
        } else {
            requestNewImage();
        }
    }

    private void requestNewImage() {
        GlobalHandler.getInstance().post2BackgroundHandler(mRequestImage);
    }

    public static WordFragment newInstance(UUID wordId) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_WORD_ID, wordId);

        WordFragment wordFragment = new WordFragment();
        wordFragment.setArguments(args);
        return wordFragment;
    }
}
