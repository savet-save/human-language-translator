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

import com.example.humanlanguagetranslator.data.WordJsonDefine;
import com.example.humanlanguagetranslator.util.GlobalHandler;
import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.util.Utils;
import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.VerifiedInfo;
import com.example.humanlanguagetranslator.data.Word;
import com.example.humanlanguagetranslator.helper.ImageHelper;
import com.example.humanlanguagetranslator.view.GifView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class WordFragment extends Fragment {
    private static final String ARGS_WORD_ID = "args_word_id";
    private static final String TAG = "WordFragment";
    private static final String COMMON_INPUT_DIALOG_TAG = "InputDialog";
    private static final String ARGS_ADD_MODE = "ARGS_ADD_MODE";

    private Word mWord;
    private TextView mSynonymText;
    private Button mDateButton;
    private TextView mVerifiedDateText;
    private OnWordUpdatedCallback mCallback;
    @Nullable
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
    private boolean isAddMode;
    private List<CommonInputFragment.InputViewType> mViewList;

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
            isAddMode = arguments.getBoolean(ARGS_ADD_MODE);
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
        mContentText = view.findViewById(R.id.details_word_content_text);
        mSynonymText = view.findViewById(R.id.details_word_synonym_text);
        mTypeText = view.findViewById(R.id.details_word_type_text);
        mTranslationText = view.findViewById(R.id.details_word_translation_text);
        mQuarryText = view.findViewById(R.id.details_word_quarry_text);
        mExampleText = view.findViewById(R.id.details_word_example_text);
        mVerifiedDateText = view.findViewById(R.id.details_word_verified_date_text);
        mEarliestDateText = view.findViewById(R.id.details_word_earliest_date_text);
        mEarliestAddrText = view.findViewById(R.id.details_word_earliest_addr_text);
        mVerifiedOtherText = view.findViewById(R.id.details_word_verified_other_text);
        mAuthorText = view.findViewById(R.id.details_word_author_text);
        mRestorersText = view.findViewById(R.id.details_word_restorers_text);
        mImageView = view.findViewById(R.id.details_word_item_image);
        mGifView = view.findViewById(R.id.details_word_item_gif_image);

        if (isAddMode) {
            bindAllView();
//            mDateButton = view.findViewById(R.id.details_word_set_date);
//            mDateButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (null == mFragmentManager) {
//                        Utils.outLog(TAG, "can't get FragmentActivity");
//                        return;
//                    }
//                    CommonInputFragment dialog = CommonInputFragment.
//                            newDatePickerInstance("ss", null, null);
//                    dialog = CommonInputFragment.newTextInstance("ss", "ttt", null, "test" + mWord.getId());
//                    dialog.show(mFragmentManager, COMMON_INPUT_DIALOG_TAG);
//                }
//            });
        }

        //set show data
        updateAllUI();

        return view;
    }

    private void bindAllView() {
        if (null == mWord || null == mFragmentManager) {
            Utils.logDebug(TAG, "bindAllView() fail : mWord or mFragmentManager is null" + "\n" +
                    "mWord : " + mWord + "  mFragmentManager : " + mFragmentManager);
            return;
        }
        initViewList();
        setAllOnClick();
        mFragmentManager.setFragmentResultListener(CommonInputFragment.RESULT_DATE_KEY,
                this,
                (requestKey, result) -> {
                    if (!CommonInputFragment.RESULT_DATE_KEY.equals(requestKey)) {
                        Utils.logDebug(TAG, "not equals");
                        return;
                    }
                    Date date = (Date) result.getSerializable(CommonInputFragment.RESULT_DATE_KEY);
                    Utils.logDebug(TAG, "date :" + date);
                    mWord.getVerifiedInfo().setVerifiedTime(date);
                    updateAllUI();
                    updateWordListUI();
                });
    }


    private void setAllOnClick() {
        if (null == mWord) {
            Utils.logDebug(TAG, "setAllOnClick() fail : mWord is null");
            return;
        }
        for (CommonInputFragment.InputViewType inputViewType : mViewList) {
            View view = inputViewType.getView();
            view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewClick(inputViewType);
                }
            });
        }
    }

    private void onViewClick(CommonInputFragment.InputViewType inputViewType) {
        if (null == mFragmentManager) {
            Utils.outLog(TAG, "can't get FragmentActivity");
            return;
        }
        CommonInputFragment.InputType inputType = inputViewType.getInputType();
        if (null == inputType) {
            return;
        }
        CommonInputFragment dialog;
        switch (inputType) {
            case TEXT_INPUT:
                dialog = CommonInputFragment.
                        newTextInstance(inputViewType.getTitle(),
                                inputViewType.getHint(),
                                inputViewType.getHasContent(),
                                null);
                break;
            case DATE_PICKER:
                dialog = CommonInputFragment.
                        newDatePickerInstance(inputViewType.getTitle(),
                                inputViewType.getDate(),
                                null);
                break;
            case SPINNER:
                dialog = CommonInputFragment.
                        newSelectInstance(inputViewType.getTitle(),
                                inputViewType.getSpinnerAllItem(),
                                inputViewType.getSelectItem(),
                                null);
                break;
            default:
                Utils.logDebug(TAG, "need implementation : " + inputType);
                return;
        }
        dialog.show(mFragmentManager, COMMON_INPUT_DIALOG_TAG);
    }

    private void initViewList() {
        if (null == mViewList) {
            mViewList = new ArrayList<>();
            mViewList.add(new CommonInputFragment.InputViewType(mContentText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.WORD_KEY));
            mViewList.add(new CommonInputFragment.InputViewType(mSynonymText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.SYNONYM_KEY));
            mViewList.add(new CommonInputFragment.InputViewType(mTypeText, CommonInputFragment.InputType.SPINNER)
                    .setSpinnerInfo(WordJsonDefine.Explain.TYPE_KEY, WordJsonDefine.WordType.getNames(), mWord.getWordType().ordinal()));
            mViewList.add(new CommonInputFragment.InputViewType(mTranslationText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.TRANSLATION_KEY));
            mViewList.add(new CommonInputFragment.InputViewType(mQuarryText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.QUARRY_KEY));
            mViewList.add(new CommonInputFragment.InputViewType(mExampleText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.EXAMPLE_KEY));

            VerifiedInfo verifiedInfo = mWord.getVerifiedInfo();
            mViewList.add(new CommonInputFragment.InputViewType(mVerifiedDateText, CommonInputFragment.InputType.DATE_PICKER)
                    .setDatePickerInfo(WordJsonDefine.Explain.VERIFIED_TIME_KEY, verifiedInfo.getVerifiedTime()));
            mViewList.add(new CommonInputFragment.InputViewType(mEarliestDateText, CommonInputFragment.InputType.DATE_PICKER)
                    .setDatePickerInfo(WordJsonDefine.Explain.EARLIEST_TIME_KEY, verifiedInfo.getEarliestTime()));

            mViewList.add(new CommonInputFragment.InputViewType(mEarliestAddrText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.EARLIEST_ADDR_KEY));
            mViewList.add(new CommonInputFragment.InputViewType(mVerifiedOtherText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.OTHER_KEY));
            mViewList.add(new CommonInputFragment.InputViewType(mAuthorText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.AUTHOR_KEY));
            mViewList.add(new CommonInputFragment.InputViewType(mRestorersText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.RESTORERS_KEY));
            mViewList.add(new CommonInputFragment.InputViewType(mImageView, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.PICTURE_LINK));
            mViewList.add(new CommonInputFragment.InputViewType(mGifView, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.PICTURE_LINK));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        mFragmentManager.setFragmentResultListener("test" + mWord.getId(),
                this,
                (requestKey, result) -> {
                    String text = result.getString(CommonInputFragment.RESULT_TEXT_KEY);
                    Utils.logDebug(TAG, "text :" + text);
                    mDateButton.setText(text);
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
        mFragmentManager.clearFragmentResultListener("test" + mWord.getId());
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
        setTextShowUI(mVerifiedDateText, getString(R.string.verified_date) + verifiedDate,
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

    public static WordFragment newInstance(UUID wordId, boolean isAddMode) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_WORD_ID, wordId);
        args.putBoolean(ARGS_ADD_MODE, isAddMode);

        WordFragment wordFragment = new WordFragment();
        wordFragment.setArguments(args);
        return wordFragment;
    }
}
